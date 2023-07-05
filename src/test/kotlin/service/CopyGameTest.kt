package service

import entity.*
import java.lang.IllegalStateException
import kotlin.test.*

/**
 * [CopyGameTest] tests [GameService].copyGame()
 * Mainly to ensure that the copy is actually a deep copy with identical values
 */

class CopyGameTest {

    private val rootService = RootService()

    @BeforeTest
    fun prepareGame() {

        val gameSettings = mutableListOf(
            Triple("PlayerHuman", Color.BLACK, PlayerType.HUMAN),
            Triple("PlayerAI", Color.BROWN, PlayerType.BEST_AI),
            Triple("PlayerRandom", Color.GRAY, PlayerType.RANDOM_AI)
        )

        rootService.gameService.startNewGame(gameSettings)

    }

    /**
     * Most methods contain a checkNotNull check on the currentGame object since it is nullable.
     * This just calls the method with a null game to increase test coverage
     */
    @Test
    fun trivialNullTest() {

        // set currentGame pointer to null
        rootService.currentGame = null

        // call method and check if it catches the null case
        assertFailsWith<IllegalStateException> { rootService.gameService.gameCopy() }

    }

    /**
     * Checks if the [Sagani] object that was returned by gameCopy is equal (i.e. has same values)
     * but not the same (so it is a different instance of a Sagani object)
     */
    @Test
    fun checkIfCopyWorks() {

        val game = rootService.currentGame
        assertNotNull(game)

        // make a copy
        rootService.gameService.gameCopy()
        val copy = rootService.currentGame
        assertNotNull(copy)

        // compare hashcodes of both game objects
        assertEquals(copy.hashCode(), game.hashCode())

        // check if the two objects are two different instances
        // if they are the same instance it can't be a deep copy
        assertNotSame(copy, game)

    }

    /**
     * Specifically test for deep copy by
     *  1. making a copy
     *  2. changing some properties of the copy
     *  3. Check if the original [Sagani] object has changed as well
     */
    @Test
    fun checkIfCopyIsDeepCopy() {

        val game = rootService.currentGame
        assertNotNull(game)

        // put tile into player board
        game.players[0].board[Pair(0,0)] = game.offerDisplay.removeFirst()
        game.players[1].board[Pair(0,0)] = game.offerDisplay.removeFirst()
        game.players[2].board[Pair(0,0)] = game.offerDisplay.removeFirst()

        // make a copy
        rootService.gameService.gameCopy()
        val copy = rootService.currentGame
        assertNotNull(copy)

        // first two player will take one tile from the offeringTiles
        game.players[0].board[Pair(0, 0)] = game.offerDisplay.removeFirst()
        game.players[1].board[Pair(0, 0)] = game.offerDisplay.removeFirst()

        //both players loose 2 sound discs
        repeat(2) {
            game.players[0].discs.removeFirst()
            game.players[1].discs.removeFirst()
        }

        // give players some points
        game.players[0].points = Pair(1, 42)
        game.players[1].points = Pair(2, 123)

        // change some other game properties
        game.turnCount = 2
        game.actPlayer = game.players[2]
        game.intermezzoStorage.add(game.stacks.removeFirst())

        // Now check if the copy has changed as well
        assertNotEquals(game, copy)
        assertNotSame(game, copy)

        // make a third copy and check for one Tile if it was copied correctly
        rootService.gameService.gameCopy()
        val secondCopy = rootService.currentGame
        assertNotNull(secondCopy)

        assertEquals(secondCopy.players[0].board[Pair(0,0)], copy.players[0].board[Pair(0,0)])


    }

    /**
     * Make two copies. During the second copying the lastTurn property of the [Sagani]
     * Object is another [Sagani] object which might lead to an infinite loop
     * In case of an infinite loop there will be a StackOverflowError
     */
    @Test
    fun copyGameTwice() {

        val game = rootService.currentGame
        assertNotNull(game)

        // make first copy
        rootService.gameService.gameCopy()

        // make second copy
        rootService.gameService.gameCopy()


    }

    /**
     * During copying of the game the connection [Sagani] game -> game.lastTurn is temporarily cut
     * This tests if all objects are reconnected properly.
     */
    @Test
    fun checkIfLinkIsReestablished() {

        //make three copies
        rootService.gameService.gameCopy()
        rootService.gameService.gameCopy()
        rootService.gameService.gameCopy()
        rootService.gameService.saveGame("42.json")

        val game = rootService.currentGame
        assertNotNull(game)
        assertNotNull(game.lastTurn)
        assertNotNull(game.lastTurn?.lastTurn)
        assertNotNull(game.lastTurn?.lastTurn?.lastTurn)

    }

}
