package service

import edu.udo.cs.sopra.ntf.GameInitMessage
import edu.udo.cs.sopra.ntf.TurnMessage
import tools.aqua.bgw.net.client.BoardGameClient
import tools.aqua.bgw.net.common.annotations.GameActionReceiver
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.GameActionResponse
import tools.aqua.bgw.net.common.response.JoinGameResponse

class SaganiNetworkClient(playerName: String, host: String, secret: String, val networkService: NetworkService) :
    BoardGameClient(playerName, host, secret) {
    /**
     * The unique identifier of the session. Null if there is no session.
     */
    var sessionID: String? = null

    /**
     * The names of the other players in the session.
     */
    var otherPlayers = listOf<String>()

    override fun onCreateGameResponse(response: CreateGameResponse) {
        // TODO
    }

    override fun onJoinGameResponse(response: JoinGameResponse) {
        // TODO
    }

    override fun onPlayerJoined(notification: PlayerJoinedNotification) {
        // TODO
    }

    override fun onGameActionResponse(response: GameActionResponse) {
        // TODO
    }

    @GameActionReceiver
    fun onTurnMessageReceived(message: TurnMessage, sender: String) {
        // TODO
    }

    @GameActionReceiver
    fun onHostGameInitReceived(message: GameInitMessage, sender: String) {
        // TODO
    }

}