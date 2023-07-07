package service

import entity.*
import java.lang.IllegalStateException
import kotlin.test.*

/**
 * [CalculateWinnerTest] tests [GameService].copyGame()
 * Mainly to ensure that the copy is actually a deep copy with identical values
 */

class CalculateWinnerTest {

    private val rootService = RootService()

    /**
     * Prepare a game with four players
     */
    @BeforeTest
    fun prepareGame() {

        val gameSettings = mutableListOf(
            Triple("PlayerOne", Color.BLACK, PlayerType.HUMAN),
            Triple("PlayerTwo", Color.BROWN, PlayerType.BEST_AI),
            Triple("PlayerThree", Color.GREY, PlayerType.RANDOM_AI),
            Triple("PlayerFour", Color.WHITE, PlayerType.HUMAN),
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
     * Test case for three players where two have the same points, but different turn counts
     */
    @Test
    fun checkSorting() {

        val game = rootService.currentGame
        assertNotNull(game)

        // correct order for these players should be 3,1,2,0
        game.players[0].points = Pair(10, 1)
        game.players[1].points = Pair(20, 2)
        game.players[2].points = Pair(20, 5)
        game.players[3].points = Pair(50, 5)

        val playerOneName = game.players[0].name
        val playerTwoName = game.players[1].name
        val playerThreeName = game.players[2].name
        val playerFourName = game.players[3].name


        // trigger function
        rootService.gameService.calculateWinner()


        // check if player objects are in correct order
        assertEquals(playerFourName, game.players[0].name)
        assertEquals(playerTwoName, game.players[1].name)
        assertEquals(playerThreeName, game.players[2].name)
        assertEquals(playerOneName, game.players[3].name)


    }


}
