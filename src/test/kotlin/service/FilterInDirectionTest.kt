package service

import entity.Direction
import Location
import entity.Tile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * [FilterInDirectionTest] tests [PlayerActionService].filterInDirection
 */
class FilterInDirectionTest {
    private val rootService = RootService()
    private val tiles = rootService.gameService.createStacks()

    /**
     * correct test
     */
    @Test
    fun correctTest() {
        // testData
        val board: MutableMap<Location, Tile> = mutableMapOf()
        for (yPos in 0..2) {
            for (xPos in 0..2) {
                board[Pair(xPos, yPos)] = tiles[xPos + yPos * 3]
            }
        }
        val location = Pair(1, 1)
        // tests
        assert(
            tiles[0] in rootService.playerActionService.filterInDirection(board, Direction.DOWN_LEFT, location).values
        )
        assertEquals(
            1,
            rootService.playerActionService.filterInDirection(board, Direction.DOWN_LEFT, location).values.size
        )
        assert(
            tiles[1] in rootService.playerActionService.filterInDirection(board, Direction.DOWN, location).values
        )
        assertEquals(
            1,
            rootService.playerActionService.filterInDirection(board, Direction.DOWN, location).values.size
        )
        assert(
            tiles[2] in rootService.playerActionService.filterInDirection(board, Direction.DOWN_RIGHT, location).values
        )
        assertEquals(
            1,
            rootService.playerActionService.filterInDirection(board, Direction.DOWN_RIGHT, location).values.size
        )
        assert(
            tiles[3] in rootService.playerActionService.filterInDirection(board, Direction.LEFT, location).values
        )
        assertEquals(
            1,
            rootService.playerActionService.filterInDirection(board, Direction.LEFT, location).values.size
        )
        assert(
            tiles[5] in rootService.playerActionService.filterInDirection(board, Direction.RIGHT, location).values
        )
        assertEquals(
            1,
            rootService.playerActionService.filterInDirection(board, Direction.RIGHT, location).values.size
        )
        assert(
            tiles[6] in rootService.playerActionService.filterInDirection(board, Direction.UP_LEFT, location).values
        )
        assertEquals(
            1,
            rootService.playerActionService.filterInDirection(board, Direction.UP_LEFT, location).values.size
        )
        assert(
            tiles[7] in rootService.playerActionService.filterInDirection(board, Direction.UP, location).values
        )
        assertEquals(
            1,
            rootService.playerActionService.filterInDirection(board, Direction.UP, location).values.size
        )
        assert(
            tiles[8] in rootService.playerActionService.filterInDirection(board, Direction.UP_RIGHT, location).values
        )
        assertEquals(
            1,
            rootService.playerActionService.filterInDirection(board, Direction.UP_RIGHT, location).values.size
        )
        for (direction in Direction.values()) {
            assertFalse(
                tiles[4] in rootService.playerActionService.filterInDirection(board, direction, location).values
            )
        }
    }
}
