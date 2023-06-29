import edu.udo.cs.sopra.ntf.ConnectionState
import service.RootService

typealias Location = Pair<Int, Int>

fun main(args: Array<String>) {
    val rootService = RootService()
    val secondRoot = RootService()
    val sessionID = "group7-test-4"
    if ("--host" in args) {
        rootService.networkService.hostGame("Host", sessionID)
    } else if ("--client" in args) {
        rootService.networkService.joinGame("Client", sessionID)
    } else if ("--both" in args) {
        rootService.networkService.hostGame("Host")
        Thread.sleep(1000)
        val id = rootService.networkService.client?.sessionID
        if (id != null) {
            println("Joining game with id $id")
            secondRoot.networkService.joinGame("Client", id)
        } else {
            println("Couldn't create game!")
        }
    }

    while (rootService.networkService.connectionState != ConnectionState.DISCONNECTED) {
        Thread.sleep(1000)
    }
    println("Application ended. Goodbye")
}
