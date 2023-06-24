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
        afterTests(game)
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
        game = afterTests(game)
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
        game = afterTests(game)
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
        game.players[0].points = Pair(45, game.turnCount)
        // function call
        rootService.gameService.changeToNextPlayer()
        // tests
        game = afterTests(game)
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

    private fun afterTests(game: Sagani): Sagani {
        // currentGame is new Sagani object
        // assertNotSame(rootService.currentGame, game)
        val newGameState = rootService.currentGame
        assertNotNull(newGameState)
        // next player
        assertEquals(newGameState.players[1], newGameState.actPlayer)
        // turnCount increased
        assertEquals(1, newGameState.turnCount)
        // refreshAfterChangeToNextPlayer was called
        assert(refreshable.refreshAfterChangeToNextPlayerCalled)
        return newGameState
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