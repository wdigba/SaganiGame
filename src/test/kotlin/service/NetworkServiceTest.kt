package service

import Location
import edu.udo.cs.sopra.ntf.ConnectionState
import entity.Color
import entity.Direction
import entity.PlayerType
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Class that provides test for the [NetworkService]. It will connect to the server twice, test sending messages and
 * turns and then disconnect. The test will fail if the server is offline or the [NetworkService.NETWORK_SECRET] is
 * outdated.
 */
class NetworkServiceTest {

    private lateinit var hostRootService: RootService
    private lateinit var guestRootService: RootService

    private lateinit var hostRefreshable: TestRefreshable
    private lateinit var guestRefreshable: TestRefreshable

    /**
     * Initializes both connections and start the game.
     */
    @BeforeTest
    fun initConnections() {
        hostRefreshable = TestRefreshable()
        hostRootService = RootService()
        hostRootService.addEachRefreshable(hostRefreshable)
        guestRefreshable = TestRefreshable()
        guestRootService = RootService()
        guestRootService.addEachRefreshable(guestRefreshable)

        hostRootService.networkService.hostGame("Test Host")
        hostRootService.waitForState(ConnectionState.WAITING_FOR_GUESTS)
        val sessionID = hostRootService.networkService.client?.sessionID
        assertNotNull(sessionID)

        guestRootService.networkService.joinGame("Test Guest", sessionID)
        guestRootService.waitForState(ConnectionState.WAITING_FOR_INIT)
        assertEquals(hostRefreshable.refreshAfterPlayerListChangeCalled, true)
    }

    /**
     * Disconnects both clients from the server.
     */
    @AfterTest
    fun disconnect() {
        hostRefreshable.reset()
        guestRootService.disconnect()
        assertEquals(hostRefreshable.refreshAfterPlayerListChangeCalled, true)
        hostRootService.disconnect()
    }

    /**
     * Test if clients can connect to the server and join the same room.
     */
    @Test
    fun `test if clients can connect to the server and join the same room`() {
        assertEquals(ConnectionState.WAITING_FOR_GUESTS, hostRootService.networkService.connectionState)
        assertEquals(ConnectionState.WAITING_FOR_INIT, guestRootService.networkService.connectionState)
    }

    /**
     * Test if clients can send a game init message and have the same initial state.
     */
    @Test
    fun `test if a game can be initialized and synced correctly`() {
        val otherPlayers = hostRootService.networkService.client?.otherPlayers
        assertNotNull(otherPlayers)
        assertEquals(1, otherPlayers.size)
        val players = listOf(
            Triple(otherPlayers.first(), Color.WHITE, PlayerType.NETWORK_PLAYER),
            Triple(hostRootService.networkService.client!!.playerName, Color.GREY, PlayerType.HUMAN)
        )
        hostRootService.gameService.startNewGame(players)
        guestRootService.waitForState(ConnectionState.PLAYING_MY_TURN)
        hostRootService.waitForState(ConnectionState.WAITING_FOR_OPPONENTS)

        val guestGame = guestRootService.currentGame
        assertNotNull(guestGame)
        val hostGame = hostRootService.currentGame
        assertNotNull(hostGame)

        // Check if the players are the same across the clients
        assertEquals(guestGame.players.size, hostGame.players.size)
        for (i in guestGame.players.indices) {
            assertEquals(guestGame.players[i].name, hostGame.players[i].name)
            assertEquals(guestGame.players[i].color, hostGame.players[i].color)
            assertEquals(guestGame.players[i].discs, hostGame.players[i].discs)
            assertEquals(guestGame.players[i].board, hostGame.players[i].board)
        }

        // Check if the stacks and current player are the same across the clients
        assertEquals(guestGame.actPlayer.name, hostGame.actPlayer.name)
        assertEquals(guestGame.stacks, hostGame.stacks)
        assertEquals(guestGame.offerDisplay, hostGame.offerDisplay)
    }

    /**
     * Test if clients can send a turn and have the same state afterwards.
     */
    @Test
    fun `test if a turn gets sent correctly`() {
        // Setup game
        val otherPlayers = hostRootService.networkService.client?.otherPlayers
        assertNotNull(otherPlayers)
        assertEquals(1, otherPlayers.size)
        val players = listOf(
            Triple(otherPlayers.first(), Color.WHITE, PlayerType.NETWORK_PLAYER),
            Triple(hostRootService.networkService.client!!.playerName, Color.GREY, PlayerType.HUMAN)
        )

        // Start game and wait until both clients are in the correct state
        hostRootService.gameService.startNewGame(players)
        guestRootService.waitForState(ConnectionState.PLAYING_MY_TURN)
        hostRootService.waitForState(ConnectionState.WAITING_FOR_OPPONENTS)

        val guestGame = guestRootService.currentGame
        checkNotNull(guestGame)

        // Place a tile and wait until both clients have received the turn
        val tile = guestGame.offerDisplay.first()
        guestRootService.playerActionService.placeTile(tile, Direction.UP, Location(0, 0))
        hostRootService.waitForState(ConnectionState.PLAYING_MY_TURN)
        guestRootService.waitForState(ConnectionState.WAITING_FOR_OPPONENTS)

        // Check if the turn was received correctly
        val hostGame = hostRootService.currentGame
        checkNotNull(hostGame)
        val guestPlayerHostSide = hostGame.players.first()
        val guestPlayerGuestSide = guestGame.players.first()

        assertEquals(guestPlayerHostSide.board, guestPlayerGuestSide.board)
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
            if (networkService.connectionState == state) return
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