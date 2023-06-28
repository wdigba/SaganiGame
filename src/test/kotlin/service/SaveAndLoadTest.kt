package service

import entity.*
import kotlin.test.*

/**
 * [SaveAndLoadTest] tests [GameService].saveGame() and .loadGame()
 * Since we need to load the file saved by saveGame anyways in order to test if it works
 * both are tested together
 */
class SaveAndLoadTest {

    private val rootService = RootService()

    @BeforeTest
    fun prepareGame() {

        val gameSettings = mutableListOf(
            Triple("PlayerHuman", Color.BLACK, PlayerType.HUMAN),
            Triple("PlayerAI", Color.BROWN, PlayerType.BEST_AI),
            Triple("PlayerRandom", Color.GREY, PlayerType.RANDOM_AI)
        )

        rootService.gameService.startNewGame(gameSettings)

    }

    /**
     * Saves the [Sagani] object in currentGame to a file. Then loads the file and compares the two
     */
    @Test
    fun saveAndReloadMultipleGames() {

        // store a copy of the Sagani object to compare with after reloading
        rootService.gameService.gameCopy()
        var oldGame = rootService.currentGame
        assertNotNull(oldGame)
        oldGame = oldGame.lastTurn
        assertNotNull(oldGame)
        oldGame.nextTurn = null

        // make some copies to establish a game history
        rootService.gameService.gameCopy()
        rootService.gameService.gameCopy()

        // save currentGame to file
        rootService.gameService.saveGame("/saveLoadTest.json")

        // delete previous game
        rootService.currentGame = null

        // reload file
        rootService.gameService.loadGame("/saveLoadTest.json")

        // Equals means both objects have the same values
        assertEquals(oldGame, rootService.currentGame)
        // NotSame means they are different instances
        assertNotSame(oldGame, rootService.currentGame)

    }

    /**
     * Saves and reloads a single [Sagani] object
     */
    @Test
    fun saveAndReloadSingleGame() {

        // store a copy of the Sagani object to compare with after reloading
        val game = rootService.currentGame
        assertNotNull(game)

        assertNull(game.lastTurn)
        assertNull(game.nextTurn)

        // save currentGame to file
        rootService.gameService.saveGame("/saveLoadTest.json")

        // delete previous game
        rootService.currentGame = null

        // reload file
        rootService.gameService.loadGame("/saveLoadTest.json")

        // Equals means both objects have the same values
        assertEquals(game, rootService.currentGame)
        // NotSame means they are different instances
        assertNotSame(game, rootService.currentGame)

        assertNull(game.lastTurn)
        assertNull(game.nextTurn)

    }

    /**
     * Checks if loaded [Sagani] game chain is still in correct order
     */
    @Test
    fun checkIfOrderIsPreserved() {

        // store a copy of the Sagani object to compare with after reloading
        rootService.gameService.gameCopy()
        val newerGame = rootService.currentGame
        assertNotNull(newerGame)

        val olderGame = newerGame.lastTurn
        assertNotNull(olderGame)

        //both players loose 2 sound discs
        repeat(2) {
            olderGame.players[0].discs.removeFirst()
            newerGame.players[1].discs.removeFirst()
        }

        // give players some points
        olderGame.players[0].points = Pair(1, 42)
        newerGame.players[1].points = Pair(2, 123)

        // change some other game properties
        olderGame.turnCount = 2
        newerGame.actPlayer = newerGame.players[2]
        newerGame.intermezzoStorage.add(newerGame.stacks.removeFirst())

        // save currentGame to file
        rootService.gameService.saveGame("/saveLoadTest.json")

        // delete previous game
        rootService.currentGame = null

        // reload file
        rootService.gameService.loadGame("/saveLoadTest.json")
        val game = rootService.currentGame
        assertNotNull(game)
        val lastGame = game.lastTurn
        assertNotNull(lastGame)

        // check for changes
        assertEquals(24, game.players[0].discs.size)
        assertEquals(22, game.players[1].discs.size)
        assertEquals(22, lastGame.players[0].discs.size)
        assertEquals(24, lastGame.players[1].discs.size)

        assertEquals(Pair(1, 42), lastGame.players[0].points)
        assertEquals(Pair(2, 123), game.players[1].points)

        assertEquals(2, lastGame.turnCount)
        assertEquals(game.players[2], game.actPlayer)
        assertEquals(1, game.intermezzoStorage.size)

    }

    /**
     * During saving process the chain of games is iterates through. This tests if after saving the currentGame
     * is still the same
     */
    @Test
    fun checkIfCurrentGameUnchanged() {

        // store a copy of the Sagani object to compare with after reloading
        rootService.gameService.gameCopy()
        val game = rootService.currentGame
        assertNotNull(game)

        game.turnCount = 42
        game.lastTurn?.turnCount = 10

        rootService.gameService.saveGame("test.txt")

        assertEquals(42, rootService.currentGame?.turnCount)
        assertEquals(10, rootService.currentGame?.lastTurn?.turnCount)

    }
}
