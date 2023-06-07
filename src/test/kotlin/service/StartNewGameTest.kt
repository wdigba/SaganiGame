package service

import entity.Color
import entity.PlayerType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * [StartNewGameTest] tests [GameService].startNewGame()
 */
class StartNewGameTest {
    private val rootService = RootService()
    private val playerNames: MutableList<Triple<String, Color, PlayerType>> = mutableListOf()
    private val alice = Triple("Alice", Color.WHITE, PlayerType.HUMAN)
    private val bob = Triple("Bob", Color.BROWN, PlayerType.HUMAN)
    private val anonym = Triple("", Color.BLACK, PlayerType.HUMAN)

    /**
     * test with legal argument
     */
    @Test
    fun correctTest(){
        // use TestRefreshable
        val refreshable = TestRefreshable()
        rootService.addEachRefreshable(refreshable)
        // test data
        playerNames.add(alice)
        playerNames.add(bob)
        var game = rootService.currentGame
        // no currentGame before function call
        assertNull(game)
        // function call
        rootService.gameService.startNewGame(playerNames)
        // tests
        // currentGame after function call
        game = rootService.currentGame
        assertNotNull(game)
        // alice and bob are playing this game
        assertEquals(2, game.players.size)
        assertEquals(alice.first, game.players[0].name)
        assertEquals(alice.second, game.players[0].color)
        assertEquals(alice.third, game.players[0].playerType)
        assertEquals(bob.first, game.players[1].name)
        assertEquals(bob.second, game.players[1].color)
        assertEquals(bob.third, game.players[1].playerType)
        assert(refreshable.refreshAfterStartNewGameCalled)
    }
}