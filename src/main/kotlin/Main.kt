import edu.udo.cs.sopra.ntf.ConnectionState
import entity.PlayerType
import service.RootService

typealias Location = Pair<Int, Int>
/**
 * Main entrypoint of the application.
 */
fun main(args: Array<String>) {
    val rootService = RootService()
    val secondRoot = RootService()
    val sessionID = "12345"
    val saveGame = true
    if ("--host" in args) {
        rootService.networkService.hostGame("Niklas", sessionID)
    } else if ("--client" in args) {
        rootService.networkService.joinGame("Niklas", sessionID)
    } else if ("--both" in args) {
        rootService.networkService.hostGame("Host")
        Thread.sleep(1000)
        val id = rootService.networkService.client?.sessionID
        if (id != null) {
            rootService.networkService.client?.playerType = PlayerType.BEST_AI
            println("Joining game with id $id")
            secondRoot.networkService.joinGame("Client", id)
        } else {
            println("Couldn't create game!")
        }
    }

    while (rootService.networkService.connectionState != ConnectionState.DISCONNECTED) {
        Thread.sleep(1000)
    }

    if (saveGame) {
        rootService.gameService.saveGame("./${System.currentTimeMillis()}.json")
    }

    println("Application ended. Goodbye")
}
