package service

import entity.Color
import entity.PlayerType
import kotlin.test.*

/**
 * [StartNewGameTest] tests [GameService].startNewGame() and [GameService].createStacks()
 */
class StartNewGameTest {
    private val rootService = RootService()
    private val playerNames: MutableList<Triple<String, Color, PlayerType>> = mutableListOf()
    private val alice = Triple("Alice", Color.WHITE, PlayerType.HUMAN)
    private val bob = Triple("Bob", Color.BROWN, PlayerType.HUMAN)
    private val anonym = Triple("", Color.BLACK, PlayerType.HUMAN)
    private val claire = Triple("Claire", Color.WHITE, PlayerType.HUMAN)

    /**
     * test with legal argument
     */
    @Test
    fun correctTest() {
        // use TestRefreshable
        val refreshable = TestRefreshable()
        rootService.addEachRefreshable(refreshable)
        // test data
        playerNames.add(alice)
        playerNames.add(bob)
        var game = rootService.currentGame
        // before function call
        // no currentGame before function call
        assertNull(game)
        // refreshAfterStartNewGame() was not called jet
        assertFalse(refreshable.refreshAfterStartNewGameCalled)
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
        // stacks contains 67 tiles
        assertEquals(67, game.stacks.size)
        // refreshAfterStartNewGame() was called
        assert(refreshable.refreshAfterStartNewGameCalled)
    }

    /**
     * Test with fewer players
     */
    @Test
    fun onePlayerTest() {
        // test data
        playerNames.add(alice)
        // illegal function call
        assertFailsWith<IllegalArgumentException>(
            "You need to be 2 to 4 players to play this game."
        ) { rootService.gameService.startNewGame(playerNames) }
    }

    /**
     * Test with nameless player
     */
    @Test
    fun emptyNameTest() {
        // test data
        playerNames.add(alice)
        playerNames.add(anonym)
        // illegal function call
        assertFailsWith<IllegalArgumentException>(
            "Each player has to have a name."
        ) { rootService.gameService.startNewGame(playerNames) }
    }

    /**
     * Test with duplicating color
     */
    @Test
    fun duplicatingColorTest() {
        // test data
        playerNames.add(alice)
        playerNames.add(bob)
        playerNames.add(claire)
        // illegal function call
        assertFailsWith<IllegalArgumentException>(
            "Each player has to have a unique color."
        ) { rootService.gameService.startNewGame(playerNames) }
    }
}
