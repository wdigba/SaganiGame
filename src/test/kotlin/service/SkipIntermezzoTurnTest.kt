package service

import entity.Color
import entity.PlayerType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 * Test Cases for [PlayerActionService.skipIntermezzoTurn]
 */
class SkipIntermezzoTurnTest {

    /**
     * Test if [PlayerActionService.skipIntermezzoTurn] throws an exception if no game is active.
     */
    @Test
    fun `calling skipIntermezzoTurn() while not having a game active`() {
        assertFails { RootService().playerActionService.skipIntermezzoTurn() }
    }

    /**
     * Test if [PlayerActionService.skipIntermezzoTurn] throws an exception if the game is not in the intermezzo phase.
     */
    @Test
    fun `calling skipIntermezzoTurn() while not being in the intermezzo phase`() {
        val rootService = RootService()
        val players = listOf(
            Triple("John", Color.WHITE, PlayerType.HUMAN),
            Triple("Jo", Color.BLACK, PlayerType.HUMAN)
        )
        rootService.gameService.startNewGame(players)
        assertFails { rootService.playerActionService.skipIntermezzoTurn() }
    }

    /**
     * Test if [PlayerActionService.skipIntermezzoTurn] works correctly in an intermezzo turn.
     */
    @Test
    fun `correct Test`() {
        val rootService = RootService()
        val players = listOf(
            Triple("John", Color.WHITE, PlayerType.HUMAN),
            Triple("Jo", Color.BLACK, PlayerType.HUMAN)
        )
        rootService.gameService.startNewGame(players)
        val game = rootService.currentGame!!
        repeat(4) {
            game.intermezzoStorage += game.stacks.removeFirst()
        }
        rootService.gameService.changeToNextPlayer()
        assertEquals(rootService.currentGame!!.intermezzo, true)
        rootService.playerActionService.skipIntermezzoTurn()
    }
}
