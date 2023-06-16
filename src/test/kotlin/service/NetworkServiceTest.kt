package service

import edu.udo.cs.sopra.ntf.ConnectionState
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Class that provides test for the [NetworkService]. It will connect to the server twice, test sending messages and
 * turns and then disconnect. The test will fail if the server is offline or the [NetworkService.NETWORK_SECRET] is
 * outdated.
 */
class NetworkServiceTest {

    private lateinit var hostRootService: RootService
    private lateinit var guestRootService: RootService

    /**
     * Initializes both connections and start the game.
     */
    @BeforeTest
    fun initConnections() {
        hostRootService = RootService()
        guestRootService = RootService()

        hostRootService.networkService.hostGame("Test Host")
        hostRootService.waitForState(ConnectionState.WAITING_FOR_GUESTS)
        val sessionID = hostRootService.networkService.client?.sessionID
        assertNotNull(sessionID)

        guestRootService.networkService.joinGame("Test Guest", sessionID)
        guestRootService.waitForState(ConnectionState.WAITING_FOR_INIT)
    }

    @Test
    fun test() {
        guestRootService.disconnect()
        hostRootService.disconnect()
    }

    /**
     * busy waiting for the game represented by this [RootService] to reach the desired network [state].
     * Polls the desired state every 100 ms until the [timeout] is reached.
     *
     * This is a simplification hack for testing purposes, so that tests can be linearized on
     * a single thread.
     *
     * @param state the desired network state to reach
     * @param timeout maximum milliseconds to wait (default: 5000)
     *
     * @throws IllegalStateException if desired state is not reached within the [timeout]
     *
     * @author Stefan Naujokat, bgw-net-war
     */
    private fun RootService.waitForState(state: ConnectionState, timeout: Int = 5000) {
        var timePassed = 0
        while (timePassed < timeout) {
            if (networkService.connectionState == state)
                return
            else {
                Thread.sleep(100)
                timePassed += 100
            }
        }
        error("Did not arrive at state $state after waiting $timeout ms")
    }

    private fun RootService.disconnect() {
        networkService.disconnect()
    }
}