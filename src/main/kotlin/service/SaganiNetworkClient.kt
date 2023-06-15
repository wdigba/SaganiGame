package service

import edu.udo.cs.sopra.ntf.ConnectionState
import edu.udo.cs.sopra.ntf.GameInitMessage
import edu.udo.cs.sopra.ntf.MoveType
import edu.udo.cs.sopra.ntf.TurnMessage
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.net.client.BoardGameClient
import tools.aqua.bgw.net.common.annotations.GameActionReceiver
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.notification.PlayerLeftNotification
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.CreateGameResponseStatus
import tools.aqua.bgw.net.common.response.GameActionResponse
import tools.aqua.bgw.net.common.response.GameActionResponseStatus
import tools.aqua.bgw.net.common.response.JoinGameResponse
import tools.aqua.bgw.net.common.response.JoinGameResponseStatus

/**
 * The client for an online game of Sagani.
 *
 * @property playerName The name of the player.
 * @property host The host address of the server.
 * @property networkService The [NetworkService] that created this client.
 */
class SaganiNetworkClient(playerName: String, host: String, val networkService: NetworkService) :
    BoardGameClient(playerName, host, NetworkService.NETWORK_SECRET) {
    /**
     * The unique identifier of the session. Null if there is no session.
     */
    var sessionID: String? = null

    /**
     * The names of the other players in the session.
     */
    var otherPlayers = listOf<String>()

    /**
     * Handles a [CreateGameResponse] sent by the BGW net server. Will await the guest players when its status is
     * [CreateGameResponseStatus.SUCCESS]. As recovery from network problems is not implemented, the method will
     * disconnect and throw an [IllegalStateException].
     *
     * @throws IllegalStateException If the status is not [CreateGameResponseStatus.SUCCESS] or the game is currently
     * not waiting for a [CreateGameResponse].
     */
    override fun onCreateGameResponse(response: CreateGameResponse) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_HOST_CONFIRMATION) {
                "Received a create game response in an unexpected connection state."
            }

            when (response.status) {
                CreateGameResponseStatus.SUCCESS -> {
                    networkService.connectionState = ConnectionState.WAITING_FOR_GUESTS
                    sessionID = response.sessionID
                }

                else -> disconnectAndError(response.status)
            }
        }
    }

    /**
     * Handles a [JoinGameResponse] sent by the BGW net server. Will await the init message when its status is
     * [JoinGameResponseStatus.SUCCESS]. As recovery from network problems is not implemented, the method will
     * disconnect and throw an [IllegalStateException].
     *
     * @throws IllegalStateException If the status is not [JoinGameResponseStatus.SUCCESS] or the game is currently
     * not waiting for a [JoinGameResponse].
     */
    override fun onJoinGameResponse(response: JoinGameResponse) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_JOIN_CONFIRMATION) {
                "Received a join game response in an unexpected connection state."
            }

            when (response.status) {
                JoinGameResponseStatus.SUCCESS -> {
                    otherPlayers = response.opponents
                    sessionID = response.sessionID
                    networkService.connectionState = ConnectionState.WAITING_FOR_INIT
                }

                else -> disconnectAndError(response.status)
            }
        }
    }

    /**
     * Handles a [PlayerJoinedNotification] sent by the BGW net server. Will add the player to the list of other players
     * when the connection state is [ConnectionState.WAITING_FOR_OPPONENTS].
     *
     * @throws IllegalStateException If the connection state is not [ConnectionState.WAITING_FOR_GUESTS].
     */
    override fun onPlayerJoined(notification: PlayerJoinedNotification) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_GUESTS) {
                "Received a player joined notification in an unexpected connection state."
            }

            otherPlayers += notification.sender
        }
    }

    /**
     * Handles a [PlayerLeftNotification] sent by the BGW net server. Will remove the player from the list of other
     * players.
     */
    override fun onPlayerLeft(notification: PlayerLeftNotification) {
        BoardGameApplication.runOnGUIThread {
            otherPlayers -= notification.sender
        }
    }

    /**
     * Handles a [GameActionResponse] sent by the BGW net server. Does nothing when its status is
     * [GameActionResponseStatus.SUCCESS]. As recovery from network problems is not implemented, the method will
     * disconnect and throw an [IllegalStateException].
     *
     * @throws IllegalStateException If the status is not [GameActionResponseStatus.SUCCESS] or the game is currently
     * not waiting for a [GameActionResponse].
     */
    override fun onGameActionResponse(response: GameActionResponse) {
        BoardGameApplication.runOnGUIThread {
            check(
                networkService.connectionState == ConnectionState.WAITING_FOR_OPPONENTS || networkService.connectionState == ConnectionState.PLAYING_MY_TURN
            ) {
                "Received a game action response in an unexpected connection state."
            }

            when (response.status) {
                GameActionResponseStatus.SUCCESS -> {}
                else -> disconnectAndError(response.status)
            }
        }
    }

    /**
     * Handles a [TurnMessage] sent by the BGW net server. Will handle the turn message when the connection state is
     * [ConnectionState.WAITING_FOR_OPPONENTS].
     *
     * @throws IllegalStateException If the connection state is not [ConnectionState.WAITING_FOR_OPPONENTS].
     */
    @GameActionReceiver
    fun onTurnMessageReceived(message: TurnMessage, sender: String) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_OPPONENTS) {
                "Received a turn message in an unexpected connection state."
            }

            val game = networkService.rootService.currentGame
            checkNotNull(game) { "Received a turn message without a current game." }
            if (message.type == MoveType.SKIP) {
                // TODO: Skip intermezzo method call
                return@runOnGUIThread
            }
            val offerDisplay = game.offerDisplay
            val drawPile = game.stacks
            val tileID = message.tilePlacement?.tileId ?: error("Received a turn message without a tile placement.")

            val tile = offerDisplay.find { it.id == tileID } ?: drawPile.find { it.id == tileID }
            ?: error("Received a turn message with an unknown tile.")
            // TODO: Place tile
            // TODO: Validate tile placement using the TurnChecksum
        }
    }

    /**
     * Handles a [GameInitMessage] sent by the BGW net server. Will handle the game init message when the connection
     * state is [ConnectionState.WAITING_FOR_INIT].
     */
    @GameActionReceiver
    fun onHostGameInitReceived(message: GameInitMessage, sender: String) {
        BoardGameApplication.runOnGUIThread {
            check(networkService.connectionState == ConnectionState.WAITING_FOR_INIT) {
                "Received a game init message in an unexpected connection state."
            }

            // TODO: Start a new game
        }
    }

    /**
     * Disconnects from the server and throws an [IllegalStateException] with the given [message].
     */
    private fun disconnectAndError(message: Any) {
        networkService.disconnect()
        error(message)
    }
}