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
    fun saveAndReload() {

        // store a copy of the Sagani object to compare with after reloading
        rootService.gameService.gameCopy()
        var oldGame = rootService.currentGame
        assertNotNull(oldGame)
        oldGame = oldGame.lastTurn
        assertNotNull(oldGame)
        oldGame.nextTurn = null

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


}
