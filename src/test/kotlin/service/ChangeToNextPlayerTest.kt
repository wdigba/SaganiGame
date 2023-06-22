package service

import entity.Color
import entity.PlayerType
import kotlin.test.*

/**
 * [ChangeToNextPlayerTest] tests [GameService].changeToNextPlayer()
 */
class ChangeToNextPlayerTest {
    private val rootService = RootService()
    private val alice = Triple("Alice", Color.WHITE, PlayerType.HUMAN)
    private val bob = Triple("Bob", Color.BROWN, PlayerType.HUMAN)
    private val playerNames = mutableListOf(alice, bob)

    /**
     * Test without intermezzo
     */
    @Test
    fun noIntermezzoTest() {
        // use TestRefreshable
        val refreshable = TestRefreshable()
        rootService.addEachRefreshable(refreshable)
        // testData
        rootService.gameService.startNewGame(playerNames)
        var game = rootService.currentGame
        assertNotNull(game)
        assertFalse(game.intermezzo)
        assertEquals(game.players[0], game.actPlayer)
        assertEquals(0, game.turnCount)
        assertFalse(refreshable.refreshAfterChangeToNextPlayerCalled)
        // function call
        rootService.gameService.changeToNextPlayer()
        // tests
        // currentGame is new Sagani object
        //assertNotSame(rootService.currentGame, game)
        game = rootService.currentGame
        assertNotNull(game)
        // next player
        assertEquals(game.players[1], game.actPlayer)
        // turnCount increased
        assertEquals(1, game.turnCount)
        // refreshAfterChangeToNextPlayer was called
        assert(refreshable.refreshAfterChangeToNextPlayerCalled)
        // reset refreshable
        refreshable.reset()

        // empty offerDisplay
        // testData
        game.offerDisplay.clear()
        assertEquals(0, game.offerDisplay.size)
        assertEquals(game.players[1], game.actPlayer)
        assertEquals(1, game.turnCount)
        assertFalse(refreshable.refreshAfterChangeToNextPlayerCalled)
        // function call
        rootService.gameService.changeToNextPlayer()
        // tests
        // currentGame is new Sagani object
        //assertNotSame(rootService.currentGame, game)
        game = rootService.currentGame
        assertNotNull(game)
        // offerDisplay refilled
        assertEquals(5, game.offerDisplay.size)
        // next player
        assertEquals(game.players[0], game.actPlayer)
        // turnCount increased
        assertEquals(2, game.turnCount)
        // refreshAfterChangeToNextPlayer was called
        assert(refreshable.refreshAfterChangeToNextPlayerCalled)
        // reset refreshable
        refreshable.reset()
    }

    /**
     * Test without game
     */
    @Test
    fun noGameTest() {
        assertNull(rootService.currentGame)
        assertFailsWith<IllegalStateException>("There is no game.") {
            rootService.gameService.changeToNextPlayer()
        }
    }
}