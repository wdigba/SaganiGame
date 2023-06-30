package service

import Location
import entity.*
import kotlin.random.Random.Default.nextInt
import kotlin.test.Test
import kotlin.test.assertFalse

/**
 * [ValidLocationTest] tests [PlayerActionService].validLocation
 */
class ValidLocationTest {
    private val rootService = RootService()

    /**
     * test with board
     */
    @Test
    fun correctTest() {
        // testData
        val tile = Tile(2, 1, Element.FIRE, listOf(Arrow(Element.FIRE, Direction.UP)))
        val board: MutableMap<Location, Tile> = mutableMapOf()
        repeat(100) {
            val xRandom = nextInt(100)
            val yRandom = nextInt(100)
            board[Pair(xRandom, yRandom)] = tile
        }
        // function call
        val validLocation = rootService.playerActionService.validLocations(board)
        // tests
        board.keys.forEach {
            assertFalse(it in validLocation)
        }
        validLocation.forEach {
            assert(
                Pair(it.first - 1, it.second) in board.keys
                        || Pair(it.first + 1, it.second) in board.keys
                        || Pair(it.first, it.second - 1) in board.keys
                        || Pair(it.first, it.second + 1) in board.keys
            )
        }
    }

    /**
     * test with empty board
     */
    @Test
    fun emptyBoardTest() {
        // testData
        val board: MutableMap<Location, Tile> = mutableMapOf()
        // function call
        val validLocation = rootService.playerActionService.validLocations(board)
        // test with empty board
        assert(Pair(0, 0) in validLocation)
    }
}
