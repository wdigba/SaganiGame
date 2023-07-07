package service

import Location
import edu.udo.cs.sopra.ntf.ConnectionState
import edu.udo.cs.sopra.ntf.GameInitMessage
import edu.udo.cs.sopra.ntf.MoveType
import edu.udo.cs.sopra.ntf.Orientation
import edu.udo.cs.sopra.ntf.TilePlacement
import edu.udo.cs.sopra.ntf.TurnChecksum
import edu.udo.cs.sopra.ntf.TurnMessage
import entity.Color
import entity.Direction
import entity.Disc
import entity.Player
import entity.PlayerType
import entity.Sagani
import entity.Tile
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
class SaganiNetworkClient(playerName: String, host: String, private val networkService: NetworkService) :
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
     * The type of player this client is.
     */
    var playerType = PlayerType.BEST_AI // if (playerName == "Client") PlayerType.RANDOM_AI else PlayerType.BEST_AI

    /**
     * The type of move the player performed.
     */
    var moveType: MoveType? = null

    /**
     * The tile that got placed on the board.
     */
    var tile: Tile? = null

    /**
     * The location the player placed the tile at.
     */
    var location: Location? = null

    /**
     * The last turn checksum received from the server.
     */
    var lastTurnChecksum: TurnChecksum? = null

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
                    sessionID = response.sessionID
                    networkService.connectionState = ConnectionState.WAITING_FOR_GUESTS
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

            networkService.onAllRefreshables {
                val players = listOf(playerName) + otherPlayers
                refreshAfterPlayerListChange(players)
            }

            val players = listOf(
                Triple(otherPlayers.first(), Color.WHITE, PlayerType.NETWORK_PLAYER),
                Triple(networkService.client!!.playerName, Color.GREY, playerType)
            )
            networkService.rootService.gameService.startNewGame(players)
        }
    }

    /**
     * Handles a [PlayerLeftNotification] sent by the BGW net server. Will remove the player from the list of other
     * players.
     */
    override fun onPlayerLeft(notification: PlayerLeftNotification) {
        BoardGameApplication.runOnGUIThread {
            otherPlayers -= notification.sender

            networkService.onAllRefreshables {
                val players = listOf(playerName) + otherPlayers
                refreshAfterPlayerListChange(players)
            }
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
                networkService.connectionState == ConnectionState.WAITING_FOR_OPPONENTS
                        || networkService.connectionState == ConnectionState.PLAYING_MY_TURN
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
     * Sends a [TurnMessage] to the BGW net server. The message will only be sent when it's the player's turn.
     *
     * @param player The player that is sending the turn message
     */
    fun sendTurnMessage(player: Player) {
        require(networkService.connectionState == ConnectionState.PLAYING_MY_TURN) { "Cannot send a turn message." }
        val game = networkService.rootService.currentGame
        checkNotNull(game) { "Can not send a turn message while the game hasn't started yet." }
        val type = moveType
        checkNotNull(type) { "Can not send a turn message without a move type." }

        // Check if the intermezzo or last round started this turn
        val intermezzoStart = !(game.lastTurn?.intermezzo ?: false) && game.intermezzo
        val lastRoundStart = !(game.lastTurn?.lastRound ?: false) && game.lastRound

        val checksum = TurnChecksum(player.points.first, player.discs.size, intermezzoStart, lastRoundStart)

        println("${player.name} sent a turn message with checksum $checksum.")

        // Determine the tile placement object to send. If the move type is skip, the placement is null.
        val placement = if (type == MoveType.SKIP) {
            null
        } else {
            val tile = this.tile
            checkNotNull(tile) { "Can not send a turn message without a tile." }
            val location = this.location
            checkNotNull(location) { "Can not send a turn message without a location." }

            println("${player.name} sent a turn message with tile id ${tile.id} at location $location.")

            TilePlacement(tile.id, location.first, location.second, tile.direction.toOrientation())
        }

        sendGameActionMessage(TurnMessage(type, placement, checksum))
        resetVariables()
    }

    /**
     * Sends a [GameInitMessage] to the BGW net server. This message will only be sent when the game has started.
     */
    fun sendGameInit() {
        val game = networkService.rootService.currentGame
        checkNotNull(game) { "Can not send a game init message while the game hasn't started yet." }
        check(game.stacks.size == 72) { "Stack size is not 72" }

        val players = game.players.map { edu.udo.cs.sopra.ntf.Player(it.name, it.color.toNTFColor()) }
        val stack = game.stacks.map { it.id }
        sendGameActionMessage(GameInitMessage(players, stack))

        if (players.first().name == playerName) {
            networkService.connectionState = ConnectionState.PLAYING_MY_TURN
        } else {
            networkService.connectionState = ConnectionState.WAITING_FOR_OPPONENTS
        }
    }

    /**
     * Resets the variables used to send a [TurnMessage]. Should be called after a turn message has been sent.
     */
    private fun resetVariables() {
        moveType = null
        tile = null
        location = null
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
            println("$playerName: Received a turn message from $sender")
            check(networkService.connectionState == ConnectionState.WAITING_FOR_OPPONENTS) {
                "$sender sent a turn when it's not their turn."
            }

            val game = networkService.rootService.currentGame
            checkNotNull(game) { "Received a turn message without a current game." }
            lastTurnChecksum = message.checksum
            if (message.type == MoveType.SKIP) {
                networkService.rootService.gameService.changeToNextPlayer()
                return@runOnGUIThread
            }

            println("$sender sent a turn message")

            val tilePlacement = message.tilePlacement ?: error("Received a turn message without a tile placement.")

            val tile = when (message.type) {
                MoveType.INTERMEZZO -> game.intermezzoStorage.find { it.id == tilePlacement.tileId }
                MoveType.OFFER_DISPLAY -> game.offerDisplay.find { it.id == tilePlacement.tileId }
                MoveType.DRAW_PILE -> game.stacks.find { it.id == tilePlacement.tileId }
                else -> error("Received a turn message with an unknown move type.")
            } ?: error("Could not find the tile with id ${tilePlacement.tileId} in the ${message.type}.")

            val position = Pair(tilePlacement.posX, tilePlacement.posY)
            val direction = tilePlacement.orientation.toDirection()

            networkService.rootService.playerActionService.placeTile(
                tile, direction, position, false
            )
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
                "$sender sent a game init message in an invalid state."
            }

            val players = message.players.map {
                Player(
                    it.name,
                    it.color.toEntityColor(),
                    if (it.name == playerName) playerType else PlayerType.NETWORK_PLAYER
                )
            }.toMutableList()

            players.forEach { player ->
                repeat(24) { player.discs.add(Disc.SOUND) }
            }

            val totalDeck = networkService.rootService.gameService.createStacks()
            val stacks = message.drawPile.map { totalDeck.find { tile -> tile.id == it } ?: error("Tile not found") }
                .toMutableList()

            val game = Sagani(players, stacks)
            repeat(5) {
                game.offerDisplay.add(game.stacks.removeFirst())
            }

            networkService.rootService.currentGame = game

            networkService.connectionState = if (players.first().name == playerName) {
                ConnectionState.PLAYING_MY_TURN
            } else {
                ConnectionState.WAITING_FOR_OPPONENTS
            }

            // refresh GUI
            networkService.onAllRefreshables {
                refreshAfterStartNewGame(
                    game.players[0], setOf(Location(0, 0)), false
                )
            }

            // If it's our turn, and we are an AI, calculate the next move
            if (networkService.connectionState == ConnectionState.PLAYING_MY_TURN) {
                if (this.playerType == PlayerType.RANDOM_AI) {
                    networkService.rootService.kIServiceRandom.calculateRandomMove()
                } else if (this.playerType == PlayerType.BEST_AI) {
                    networkService.rootService.kIService.playBestMove()
                }
            }

        }
    }

    /**
     * Disconnects from the server and throws an [IllegalStateException] with the given [message].
     */
    private fun disconnectAndError(message: Any) {
        networkService.disconnect()
        error(message)
    }

    /**
     * Converts the entity layers [Direction] to the NTF version [Orientation].
     * @throws IllegalStateException If the [Direction] can not be converted to an [Orientation].
     */
    private fun Direction.toOrientation(): Orientation = when (this) {
        Direction.UP -> Orientation.NORTH
        Direction.RIGHT -> Orientation.EAST
        Direction.DOWN -> Orientation.SOUTH
        Direction.LEFT -> Orientation.WEST
        else -> error("Invalid direction.")
    }

    /**
     * Converts the NTF version [Orientation] to the entity layers [Direction].
     */
    private fun Orientation.toDirection(): Direction = when (this) {
        Orientation.NORTH -> Direction.UP
        Orientation.EAST -> Direction.RIGHT
        Orientation.SOUTH -> Direction.DOWN
        Orientation.WEST -> Direction.LEFT
    }

    /**
     * Converts the entity layers [Color] to the NTF version [edu.udo.cs.sopra.ntf.Color].
     */
    private fun Color.toNTFColor(): edu.udo.cs.sopra.ntf.Color = when (this) {
        Color.BLACK -> edu.udo.cs.sopra.ntf.Color.BLACK
        Color.GREY -> edu.udo.cs.sopra.ntf.Color.GREY
        Color.BROWN -> edu.udo.cs.sopra.ntf.Color.BROWN
        Color.WHITE -> edu.udo.cs.sopra.ntf.Color.WHITE
    }

    /**
     * Converts the NTF version [edu.udo.cs.sopra.ntf.Color] to the entity layers [Color].
     */
    private fun edu.udo.cs.sopra.ntf.Color.toEntityColor(): Color = when (this) {
        edu.udo.cs.sopra.ntf.Color.BLACK -> Color.BLACK
        edu.udo.cs.sopra.ntf.Color.GREY -> Color.GREY
        edu.udo.cs.sopra.ntf.Color.BROWN -> Color.BROWN
        edu.udo.cs.sopra.ntf.Color.WHITE -> Color.WHITE
    }
}
