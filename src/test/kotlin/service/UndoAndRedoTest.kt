package service

import entity.*
import kotlin.test.*

/**
 * [UndoAndRedoTest] tests [GameService].undo() and .redo()
 * Test by creating a copy, changing one of them and then use undo and redo to "move" between them
 */
class UndoAndRedoTest {

    private val rootService = RootService()

    /**
     * Prepare a game with three players
     */
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
     * Create a copy of a game, change one instance and then try to move between them
     * using undo and redo
     */
    @Test
    fun undoAndRedo() {

        // create a copy
        rootService.gameService.gameCopy()

        // get references to both entity models
        val secondGame = rootService.currentGame
        checkNotNull(secondGame)
        val firstGame = secondGame.lastTurn
        checkNotNull(firstGame)


        // make changes to secondGame
        firstGame.players[0].points = Pair(1, 42)
        firstGame.turnCount = 10

        // Check if they objects differ
        assertNotEquals(firstGame.turnCount, secondGame.turnCount)
        assertNotEquals(firstGame.players[0].points, secondGame.players[0].points)

        // secondGame is currentGame, so the changes don't apply
        assertNotEquals(secondGame.turnCount, 10)
        assertNotEquals(secondGame.players[0].points, Pair(1, 42))

        // perform undo(). Afterward firstGame is currentGame and changes apply
        rootService.gameService.undo()
        val undoGame = rootService.currentGame
        checkNotNull(undoGame)
        assertEquals(undoGame.turnCount, 10)
        assertEquals(undoGame.players[0].points, Pair(1, 42))

        // trigger redo(). Afterward we should be back in secondGame and the changes do not apply anymore
        rootService.gameService.redo()
        val redoGame = rootService.currentGame
        checkNotNull(redoGame)
        assertNotEquals(redoGame.turnCount, 10)
        assertNotEquals(redoGame.players[0].points, Pair(1, 42))


    }


}
