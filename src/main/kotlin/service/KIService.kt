package service

import entity.Element.*
import entity.Direction
import entity.*
import java.util.*

/**
 * [KIService] is responsible for executing the AI strategies
 */
class KIService(private val rootService: RootService) {
    class CoordinateInformation {

        // variables to count how many open arrows show on this coordinate
        var airCount = 0
        var earthCount = 0
        var waterCount  = 0
        var fireCount = 0

        // the number of disc that would be freed if a card of the corresponding color would be placed
        var discsIfAirPlaced    = 0
        var discsIfEarthPlaced  = 0
        var discsIfWaterPlaced  = 0
        var discsIfFirePlaced   = 0

        // this variable represents the number of steps after which a tile could be places
        var gameDistance = 0

        // is the position occupied by a tile already
        var occupied = false

        fun copy(): CoordinateInformation {
            val copy = CoordinateInformation()
            copy.airCount = this.airCount
            copy.earthCount = this.earthCount
            copy.waterCount = this.waterCount
            copy.fireCount = this.fireCount
            copy.discsIfAirPlaced = this.discsIfAirPlaced
            copy.discsIfEarthPlaced = this.discsIfEarthPlaced
            copy.discsIfWaterPlaced = this.discsIfWaterPlaced
            copy.discsIfFirePlaced = this.discsIfFirePlaced
            copy.gameDistance = this.gameDistance
            copy.occupied = this.occupied
            return copy
        }
    }

    fun checkArrowAlignmentMetrix(player: Player){
        val scoreMap = buildScoreMap(player, 3)
    }

    fun buildScoreMap(player: Player, range: Int) : Map<Pair<Int, Int>, CoordinateInformation>{
        val scoreMap = fillScoreMap(player, range)
        updateScoreMapForArrows(player, scoreMap)
        return scoreMap
    }

    // determining the appropriate position depending on the given direction
    private fun calculateAdjacentPosition(pos: Pair<Int, Int>, direction: Direction): Pair<Int, Int> {
        return when (direction) {
            Direction.UP -> Pair(pos.first, pos.second + 1)
            Direction.DOWN -> Pair(pos.first, pos.second - 1)
            Direction.LEFT -> Pair(pos.first - 1, pos.second)
            Direction.RIGHT -> Pair(pos.first + 1, pos.second)
            else -> throw Exception("Unsupported direction")
        }
    }

    // whether the specified arrow is the only unsatisfied arrow in the given tile
    private fun isOnlyUnsatisfiedArrow(currentArrow: Arrow, tile: Tile): Boolean {
        var unsatisfiedCount = 0
        for (arrow in tile.arrows) {
            if (arrow.disc.size == 0) {
                unsatisfiedCount++
            }
        }
        return unsatisfiedCount == 1 && currentArrow.disc.size == 0
    }

    private fun deepCopyScoreMap(scoreMap: MutableMap<Pair<Int, Int>, CoordinateInformation>): MutableMap<Pair<Int, Int>, CoordinateInformation> {
        val newScoreMap = mutableMapOf<Pair<Int, Int>, CoordinateInformation>()
        for ((position, info) in scoreMap) {
            newScoreMap[position] = info.copy()
        }
        return newScoreMap
    }

    /**
     * [calculatePotentialTilePlacements] calculates potential estimates of tile placement
     * based on information about the coordinates and distances on the game board
     */
    fun calculatePotentialTilePlacements(tile: Tile, scoreMap: MutableMap<Pair<Int, Int>, CoordinateInformation>): Map<Pair<Int, Int>, Double> {
        val potentialScores = mutableMapOf<Pair<Int, Int>, Double>()

        for ((position, coordinateInfo) in scoreMap) {
            if (coordinateInfo.occupied || coordinateInfo.gameDistance != 0) {
                continue
            }

            for (rotation in Direction.tileDirection()) {
                tile.rotate(rotation)
                val updatedScoreMap = getNewScoreMapForTile(scoreMap, tile, position)
                val score = calculateBoardScore(updatedScoreMap)
                potentialScores[position] = score
            }

        }

        return potentialScores
    }

    /**
     * [updatePosition] updates the information about a specific position in newScoreMap
     * depending on the type of arrow and tile, incrementing the corresponding counters and the number of discs
     * if the given arrow is the only unsatisfied arrow.
     */
    private fun updatePosition(
        newScoreMap: MutableMap<Pair<Int, Int>, CoordinateInformation>,
        positionToUpdate: Pair<Int, Int>,
        arrow: Arrow,
        tile: Tile
    ) {
        val info = newScoreMap[positionToUpdate]!!
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
    }

    /**
     * [getNewScoreMapForTile] used to get a new copy of the scoreMap with updated values based on the placement of the tile at a specific position
     */
    private fun getNewScoreMapForTile(scoreMap: MutableMap<Pair<Int, Int>, CoordinateInformation>, tile: Tile, position: Pair<Int, Int>): MutableMap<Pair<Int, Int>, CoordinateInformation> {
        val newScoreMap = deepCopyScoreMap(scoreMap)
        val tileInfo = newScoreMap[position] ?: CoordinateInformation()
        tileInfo.occupied = true
        newScoreMap[position] = tileInfo
        for (arrow in tile.arrows) {
            if (arrow.disc.size == 1) {
                continue
            }
            var adjacentPos = calculateAdjacentPosition(position, arrow.direction)
            while (newScoreMap.containsKey(adjacentPos)) {
                updatePosition(newScoreMap, adjacentPos, arrow, tile)
                adjacentPos = calculateAdjacentPosition(adjacentPos, arrow.direction)
            }
        }
        return newScoreMap
    }

    /**
     * [calculateBoardScore] used to calculate the overall score of the game board based on the information contained in the scoreMap
     */
    fun calculateBoardScore(scoreMap: MutableMap<Pair<Int, Int>, CoordinateInformation>): Double {

        //weights
        val arrowDensityWeight = 1 //concentration of discs pointing to a particular position
        val disFreedomWeight = 1 //potential amount of discs that could de freed
        val interferenceWeight = 1 // how much the placement of one element can interfere with the placement of other elements in adjacent positions

        var totalArrowDensityScore = 0.0
        var totalDiscFreedomScore = 0.0
        var totalInteferenceScore = 0.0

        for (info in scoreMap.values) {
            if (!info.occupied) {
                // Increase score based on arrow density and proximity
                val arrowDensityScore =
                    (info.airCount * info.airCount + info.earthCount * info.earthCount + info.waterCount * info.waterCount + info.fireCount * info.fireCount) / (info.gameDistance + 1.0)
                totalArrowDensityScore += arrowDensityScore

                // Increase score based on potential disc freedom and proximity
                val discFreedomScore =
                    (info.discsIfAirPlaced * info.discsIfAirPlaced + info.discsIfEarthPlaced * info.discsIfEarthPlaced + info.discsIfWaterPlaced * info.discsIfWaterPlaced + info.discsIfFirePlaced * info.discsIfFirePlaced) / (info.gameDistance + 1.0)
                totalDiscFreedomScore += discFreedomScore

                // Decrease score if placing one element interferes with the placement of others
                val interferenceScore =
                    ((info.airCount * info.earthCount + info.airCount * info.waterCount + info.airCount * info.fireCount
                            + info.earthCount * info.waterCount + info.earthCount * info.fireCount
                            + info.waterCount * info.fireCount) / (info.gameDistance + 1.0))
                totalInteferenceScore += interferenceScore
            }
        }
        //calculation of the total score of the game board
        return arrowDensityWeight * totalArrowDensityScore + disFreedomWeight * totalDiscFreedomScore + interferenceWeight * totalInteferenceScore
    }




    private fun updateScoreMapForArrows(player: Player, scoreMap: MutableMap<Pair<Int, Int>, CoordinateInformation>) {
        for ((position, tile) in player.board) {
            scoreMap.putAll(getNewScoreMapForTile(scoreMap, tile, position))
        }
    }

    /**
     * [getLowestGameDistanceFromNeighbours]is used to determine the smallest game distance
     * among adjacent positions of a given position on the game board
     */
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


    /**
     * [fillScoreMap] is used to fill the score Map for the player in definite range
     * This information can be used to evaluate positions and make decisions in the game
     */
    fun fillScoreMap(player: Player, range: Int): MutableMap<Pair<Int, Int>, CoordinateInformation> {
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
