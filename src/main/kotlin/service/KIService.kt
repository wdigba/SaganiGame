package service

import entity.Element.*
import entity.Direction
import entity.*
import java.util.*

/**
 * The service responsible for executing the AI strategies
 */
class KIService(private val rootService: RootService) {
    private class CoordinateInformation {

        // variables to count how many open arrows show on this coordinate
        var airCount = 0
        var earthCount = 0
        var waterCount  = 0
        var fireCount = 0

        // the number of disc that would be freed if a card of the color corresponing color
        var discsIfAirPlaced    = 0
        var discsIfEarthPlaced  = 0
        var discsIfWaterPlaced  = 0
        var discsIfFirePlaced   = 0

        // this variable represents the number of steps after which a tile could be places
        var gameDistance = 0

        // is the position occupied by a tile already
        var occupied = false
    }

    fun checkArrowAlignmentMetrix(player: Player){
        var board = player.board

    }

    fun buildScoreMap(player: Player, range: Int) {
        val scoreMap = fillScoreMap(player, range)
        updateScoreMapForArrows(player, scoreMap)

    }


    private fun calculateAdjacentPosition(pos: Pair<Int, Int>, direction: Direction): Pair<Int, Int> {
        return when (direction) {
            Direction.UP -> Pair(pos.first, pos.second + 1)
            Direction.DOWN -> Pair(pos.first, pos.second - 1)
            Direction.LEFT -> Pair(pos.first - 1, pos.second)
            Direction.RIGHT -> Pair(pos.first + 1, pos.second)
            else -> throw Exception("Unsupported direction")
        }
    }


    private fun isOnlyUnsatisfiedArrow(currentArrow: Arrow, tile: Tile): Boolean {
        var unsatisfiedCount = 0
        for (arrow in tile.arrows) {
            if (arrow.disc.size == 0) {
                unsatisfiedCount++
            }
        }
        return unsatisfiedCount == 1 && currentArrow.disc.size == 0
    }



    private fun updateScoreMapForArrows(player: Player, scoreMap: MutableMap<Pair<Int, Int>, CoordinateInformation>) {
        for ((position, tile) in player.board) {
            for (arrow in tile.arrows) {
                var adjacentPos = calculateAdjacentPosition(position, arrow.direction)
                while (scoreMap.containsKey(adjacentPos)) {
                    val info = scoreMap[adjacentPos]!!
                    val isTheOnlyUnsatisfiedArrow = isOnlyUnsatisfiedArrow(arrow, tile)
                    when (arrow.element) {
                        AIR -> {
                            info.airCount++
                            if (isTheOnlyUnsatisfiedArrow) {
                                info.discsIfAirPlaced += tile.discs.size
                            }
                        }
                        EARTH -> {
                            info.earthCount++
                            if (isTheOnlyUnsatisfiedArrow) {
                                info.discsIfEarthPlaced += tile.discs.size
                            }
                        }
                        WATER -> {
                            info.waterCount++
                            if (isTheOnlyUnsatisfiedArrow) {
                                info.discsIfWaterPlaced += tile.discs.size
                            }
                        }
                        FIRE -> {
                            info.fireCount++
                            if (isTheOnlyUnsatisfiedArrow) {
                                info.discsIfFirePlaced += tile.discs.size
                            }
                        }
                    }
                    adjacentPos = calculateAdjacentPosition(adjacentPos, arrow.direction)
                }
            }
        }
    }
    private fun getLowestGameDistanceFromNeighbours(position: Pair<Int, Int>, scoreMap: Map<Pair<Int, Int>, CoordinateInformation>): Int {
        var lowestGameDistance = Int.MAX_VALUE
        for (direction in Direction.values()) {
            val neighbourPos = calculateAdjacentPosition(position, direction)
            if (scoreMap.containsKey(neighbourPos) && scoreMap[neighbourPos]!!.gameDistance < lowestGameDistance) {
                lowestGameDistance = scoreMap[neighbourPos]!!.gameDistance
            }
        }
        return lowestGameDistance
    }



    private fun fillScoreMap(player: Player, range: Int): MutableMap<Pair<Int, Int>, CoordinateInformation> {
        val scoreMap = mutableMapOf<Pair<Int, Int>, CoordinateInformation>()
        val queue: Queue<Pair<Int, Int>> = LinkedList()

        // Initialize scoreMap for all tiles on the board
        for ((position, _) in player.board) {
            val info = CoordinateInformation()
            info.occupied       = true
            info.gameDistance   = -1
            scoreMap[position]  = info
            queue.offer(position)
        }

        // Expand from each tile on the board until the game distance exceeds range
        while (queue.isNotEmpty()) {
            val position = queue.poll()
            for (direction in Direction.values()) {
                val adjacentPos = calculateAdjacentPosition(position, direction)
                if (!scoreMap.containsKey(adjacentPos)) {
                    val info = CoordinateInformation()
                    info.gameDistance = getLowestGameDistanceFromNeighbours(adjacentPos, scoreMap) + 1
                    if (info.gameDistance <= range) {
                        scoreMap[adjacentPos] = info
                        queue.offer(adjacentPos)
                    }
                }
            }
        }

        return scoreMap
    }

}
