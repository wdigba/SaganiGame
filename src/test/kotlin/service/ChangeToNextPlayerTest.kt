package service

import entity.Color
import entity.PlayerType
import entity.Sagani
import kotlin.test.*

/**
 * [ChangeToNextPlayerTest] tests [GameService].changeToNextPlayer()
 */
class ChangeToNextPlayerTest {
    private val rootService = RootService()
    private val alice = Triple("Alice", Color.WHITE, PlayerType.HUMAN)
    private val bob = Triple("Bob", Color.BROWN, PlayerType.HUMAN)
    private val playerNames = mutableListOf(alice, bob)

    // use TestRefreshable
    private val refreshable = TestRefreshable()

    /**
     * setUp for tests
     */
    @BeforeTest
    fun setUp() {
        rootService.addEachRefreshable(refreshable)
        // testData
        rootService.gameService.startNewGame(playerNames)
        val game = rootService.currentGame
        assertNotNull(game)
        assertEquals(game.players[0], game.actPlayer)
        assertEquals(0, game.turnCount)
        assertFalse(refreshable.refreshAfterChangeToNextPlayerCalled)
    }

    /**
     * Test without intermezzo
     */
    @Test
    fun noIntermezzoTest() {
        // testData
        val game = rootService.currentGame
        assertNotNull(game)
        assertFalse(game.intermezzo)
        // function call
        rootService.gameService.changeToNextPlayer()
        // tests
        afterTests()
    }

    /**
     * Test without intermezzo and empty offerDisplay
     */
    @Test
    fun noIntermezzoEmptyOfferDisplayTest() {
        // testData
        var game = rootService.currentGame
        assertNotNull(game)
        assertFalse(game.intermezzo)
        game.offerDisplay.clear()
        assertEquals(0, game.offerDisplay.size)
        // function call
        rootService.gameService.changeToNextPlayer()
        // tests
        game = afterTests()
        // offerDisplay refilled
        assertEquals(5, game.offerDisplay.size)
    }

    /**
     * Test without intermezzo and empty offerDisplay with less than 4 tiles in stacks after refill
     */
    @Test
    fun shortStacksTest() {
        // testData
        var game = rootService.currentGame
        assertNotNull(game)
        assertFalse(game.intermezzo)
        assertFalse(game.lastRound)
        game.stacks.clear()
        game.stacks.addAll(game.offerDisplay)
        game.offerDisplay.clear()
        assertEquals(0, game.offerDisplay.size)
        // function call
        rootService.gameService.changeToNextPlayer()
        // tests
        game = afterTests()
        // offerDisplay refilled
        assertEquals(5, game.offerDisplay.size)
        // lastRound started
        assert(game.lastRound)

        // game ends after next function call
        // testData
        refreshable.reset()
        assertFalse(refreshable.refreshAfterChangeToNextPlayerCalled)
        assertFalse(refreshable.refreshAfterCalculateWinnerCalled)
        // function call
        rootService.gameService.changeToNextPlayer()
        // tests
        assertFalse(refreshable.refreshAfterChangeToNextPlayerCalled)
        assert(refreshable.refreshAfterCalculateWinnerCalled)
    }

    /**
     * Test without intermezzo and a player has enough points to end the game
     */
    @Test
    fun enoughPointsTest() {
        // testData
        var game = rootService.currentGame
        assertNotNull(game)
        assertFalse(game.intermezzo)
        assertFalse(game.lastRound)
        game.players[0].points = Pair(75, game.turnCount)
        // function call
        rootService.gameService.changeToNextPlayer()
        // tests
        game = afterTests()
        // lastRound started
        assert(game.lastRound)

        // game ends after next function call
        // testData
        refreshable.reset()
        assertFalse(refreshable.refreshAfterChangeToNextPlayerCalled)
        assertFalse(refreshable.refreshAfterCalculateWinnerCalled)
        // function call
        rootService.gameService.changeToNextPlayer()
        // tests
        assertFalse(refreshable.refreshAfterChangeToNextPlayerCalled)
        assert(refreshable.refreshAfterCalculateWinnerCalled)
    }

    private fun afterTests(intermezzo: Boolean = false): Sagani {
        val newGameState = rootService.currentGame
        assertNotNull(newGameState)
        if (intermezzo) {
            // next intermezzoPlayer
            assertEquals(newGameState.players[0], newGameState.intermezzoPlayers[0])
        } else {
            // next player
            assertEquals(newGameState.players[1], newGameState.actPlayer)
        }

        // turnCount increased
        assertEquals(1, newGameState.turnCount)
        // refreshAfterChangeToNextPlayer was called
        assert(refreshable.refreshAfterChangeToNextPlayerCalled)
        return newGameState
    }

    /**
     * Test start intermezzo
     */
    @Test
    fun intermezzoStartTest() {
        // testData
        var game = rootService.currentGame
        assertNotNull(game)
        repeat(4) {
            game!!.intermezzoStorage.add(game!!.stacks.removeFirst())
        }
        game.players[1].points = Pair(1, 0)
        assertFalse(game.intermezzo)
        game.offerDisplay.clear()
        assert(game.offerDisplay.isEmpty())
        // function call
        rootService.gameService.changeToNextPlayer()
        // tests
        game = afterTests(true)
        // intermezzo started
        assert(game.intermezzo)
        assertEquals(game.players, game.intermezzoPlayers)
        // offerDisplay is not refilled
        assert(game.offerDisplay.isEmpty())
    }

    /**
     * Test intermezzo with ending the game
     */
    @Test
    fun intermezzoTest() {
        // testData
        intermezzoStartTest()
        var game = rootService.currentGame
        assertNotNull(game)
        game.players[0].points = Pair(75, game.turnCount)
        assertEquals(2, game.intermezzoPlayers.size)
        assertEquals(game.players[0], game.intermezzoPlayers[0])
        refreshable.reset()
        // function call
        rootService.gameService.changeToNextPlayer()
        // tests
        game = rootService.currentGame
        assertNotNull(game)
        // next IntermezzoPlayer
        assertEquals(1, game.intermezzoPlayers.size)
        assertEquals(game.players[1], game.intermezzoPlayers[0])
        // refreshAfterChangeToNextPlayer was called
        assert(refreshable.refreshAfterChangeToNextPlayerCalled)
        // refreshAfterCalculateWinner was not called
        assertFalse(refreshable.refreshAfterCalculateWinnerCalled)
        refreshable.reset()

        // testData
        assert(game.offerDisplay.isEmpty())
        game.actPlayer = game.players[1]    // so that the game will end
        assertEquals(4, game.intermezzoStorage.size)
        // next turn
        rootService.gameService.changeToNextPlayer()
        // tests
        game = rootService.currentGame
        assertNotNull(game)
        // no IntermezzoPlayer left
        assertEquals(0, game.intermezzoPlayers.size)
        // offerDisplay is refilled
        assert(game.offerDisplay.isNotEmpty())
        // next actPlayer
        assertEquals(game.players[0], game.actPlayer)
        // intermezzoStorage is cleared after none takes a tile
        assert(game.intermezzoStorage.isEmpty())
        // refreshAfterChangeToNextPlayer was not called
        assertFalse(refreshable.refreshAfterChangeToNextPlayerCalled)
        // refreshAfterCalculateWinner was called
        assert(refreshable.refreshAfterCalculateWinnerCalled)
        refreshable.reset()
    }

    /**
     * Test with placing tiles during intermezzo
     */
    @Test
    fun intermezzoPlaceTileTest() {
        // testData
        var game = rootService.currentGame
        assertNotNull(game)
        game.intermezzoPlayers.add(game.players[0])
        game.intermezzo = true
        game.intermezzoStorage.add(game.stacks.removeFirst())
        assertEquals(1, game.intermezzoPlayers.size)
        assertEquals(1, game.intermezzoStorage.size)
        // function call
        rootService.gameService.changeToNextPlayer()
        // tests
        game = rootService.currentGame
        assertNotNull(game)
        assertFalse(game.intermezzo)
        assertEquals(0, game.intermezzoPlayers.size)
        assertEquals(1, game.intermezzoStorage.size)
    }

    /**
     * Test without game
     */
    @Test
    fun noGameTest() {
        rootService.currentGame = null
        assertFailsWith<IllegalStateException>("There is no game.") {
            rootService.gameService.changeToNextPlayer()
        }
    }
}
