package service

import entity.Element.*
import entity.Direction
import entity.*
import java.util.*

/**
 * [KIService] is responsible for executing the AI strategies
 */
class KIService(private val rootService: RootService, val range: Int) {
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
        val scoreMap = buildScoreMap(player.board)
    }

    fun buildScoreMap(borad: Map<Pair<Int, Int>, Tile>) : Map<Pair<Int, Int>, CoordinateInformation>{
        val scoreMap = fillScoreMap(borad)
        updateScoreMapForArrows(borad, scoreMap)
        return scoreMap
    }

    // determining the appropriate position depending on the given direction
    private fun calculateAdjacentPosition(pos: Pair<Int, Int>, direction: Direction): Pair<Int, Int> {
        return when (direction) {
            Direction.UP -> Pair(pos.first, pos.second + 1)
            Direction.DOWN -> Pair(pos.first, pos.second - 1)
            Direction.LEFT -> Pair(pos.first - 1, pos.second)
            Direction.RIGHT -> Pair(pos.first + 1, pos.second)
            Direction.UP_LEFT -> Pair(pos.first - 1, pos.second + 1)
            Direction.UP_RIGHT -> Pair(pos.first + 1, pos.second + 1)
            Direction.DOWN_LEFT -> Pair(pos.first - 1, pos.second - 1)
            Direction.DOWN_RIGHT -> Pair(pos.first + 1, pos.second - 1)
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

    private fun deepCopyScoreMap(scoreMap: Map<Pair<Int, Int>, CoordinateInformation>): MutableMap<Pair<Int, Int>, CoordinateInformation> {
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
    fun calculatePotentialTilePlacements(tile: Tile, scoreMap: Map<Pair<Int, Int>, CoordinateInformation>, player: Player): Map<Pair<Pair<Int, Int>, Direction>, Double> {

        val arrowWeight = 1.0

        val potentialScores = mutableMapOf<Pair<Pair<Int, Int>, Direction>, Double>()

        for ((position, coordinateInfo) in scoreMap) {
            if (coordinateInfo.occupied || coordinateInfo.gameDistance != 0) {
                continue
            }

            for (rotation in Direction.tileDirection()) {
                tile.rotate(rotation)

                if (position == Pair(1, -1) && rotation == Direction.UP) {
                   print("breakpoint")
                }
                val positionMetrics = getMetricsForPosition(position, scoreMap, tile)

                val satisfiedArrowsMetrics = calculateSatisfiedArrowsFromTileInHand(player, tile, position)

                // get a Map with the same values as player.board
                val newPlayerBoard = player.board.toMutableMap()
                // add the tile to the new board
                newPlayerBoard[position] = tile

                val updatedScoreMap = buildScoreMap(newPlayerBoard)

                val score = calculateBoardScore(updatedScoreMap)

                potentialScores[Pair(position, rotation)] = score + arrowWeight * satisfiedArrowsMetrics + positionMetrics

                if (position == Pair(1, -1) && rotation == Direction.UP) {
                    println("Score: $score")
                    println("Satisfied Arrows: $satisfiedArrowsMetrics")
                }

                for(arrow in tile.arrows){
                    tile.discs.addAll(arrow.disc)
                }
            }

        }

        return potentialScores
    }

    // returns the maximum number of arrows that can be satisfied by placing an element on the given position based on the scoremap
    private fun getMaximumNumberOfArrowsThatCanBeSatisfied(scoreMap: Map<Pair<Int, Int>, CoordinateInformation>): Int {

        var maxCount = 0

        //for all positions
        for ((position, coordinateInfo) in scoreMap) {
            // if the position is occupied or the game distance is not 0
            if (coordinateInfo.occupied || coordinateInfo.gameDistance != 0) {
                continue
            }

            // if any count is bigger than maxCount replace maxCount by it

            if (coordinateInfo.airCount > maxCount) {
                maxCount = coordinateInfo.airCount
            }
            if (coordinateInfo.earthCount > maxCount) {
                maxCount = coordinateInfo.earthCount
            }
            if (coordinateInfo.waterCount > maxCount) {
                maxCount = coordinateInfo.waterCount
            }
            if (coordinateInfo.fireCount > maxCount) {
                maxCount = coordinateInfo.fireCount
            }
        }
        return maxCount
    }


    private fun getMaximumNumberOfFreedDiscs(scoreMap: Map<Pair<Int, Int>, CoordinateInformation>): Int {

        var maxCount = 0

        //for all positions
        for ((position, coordinateInfo) in scoreMap) {
            // if the position is occupied or the game distance is not 0
            if (coordinateInfo.occupied || coordinateInfo.gameDistance != 0) {
                continue
            }

            // if any count is bigger than maxCount replace maxCount by it

            if (coordinateInfo.discsIfAirPlaced > maxCount) {
                maxCount = coordinateInfo.discsIfAirPlaced
            }
            if (coordinateInfo.discsIfEarthPlaced > maxCount) {
                maxCount = coordinateInfo.discsIfEarthPlaced
            }
            if (coordinateInfo.discsIfWaterPlaced > maxCount) {
                maxCount = coordinateInfo.discsIfWaterPlaced
            }
            if (coordinateInfo.discsIfFirePlaced > maxCount) {
                maxCount = coordinateInfo.discsIfFirePlaced
            }
        }
        return maxCount
    }

    private fun getArrowCountForElement(position: Pair<Int, Int>, scoreMap: Map<Pair<Int, Int>, CoordinateInformation>) : Int{
        var arrowCount = 0
        if (scoreMap[position]!!.airCount > 0) {
            arrowCount += scoreMap[position]!!.airCount
        }
        if (scoreMap[position]!!.earthCount > 0) {
            arrowCount += scoreMap[position]!!.earthCount
        }
        if (scoreMap[position]!!.waterCount > 0) {
            arrowCount += scoreMap[position]!!.waterCount
        }
        if (scoreMap[position]!!.fireCount > 0) {
            arrowCount += scoreMap[position]!!.fireCount
        }
        return arrowCount
    }

    private fun getDiscCountForElement(position: Pair<Int, Int>, scoreMap: Map<Pair<Int, Int>, CoordinateInformation>) : Int{
        var discCount = 0
        if (scoreMap[position]!!.airCount > 0) {
            discCount += scoreMap[position]!!.airCount
        }
        if (scoreMap[position]!!.earthCount > 0) {
            discCount += scoreMap[position]!!.earthCount
        }
        if (scoreMap[position]!!.waterCount > 0) {
            discCount += scoreMap[position]!!.waterCount
        }
        if (scoreMap[position]!!.fireCount > 0) {
            discCount += scoreMap[position]!!.fireCount
        }
        return discCount
    }



    private fun getMetricsForPosition(position: Pair<Int, Int>, scoreMap: Map<Pair<Int, Int>, CoordinateInformation>, tile: Tile): Double {

        val element = tile.element

        var arrowsThatWouldBeSatisfiedByElement = getArrowCountForElement(position, scoreMap)

        val maxNumberOfSatisfiedArrows = getMaximumNumberOfArrowsThatCanBeSatisfied(scoreMap)

        val arrowSatisfiedMetrics = arrowsThatWouldBeSatisfiedByElement.toDouble() / maxNumberOfSatisfiedArrows

        val arrowsBlockedMetrix = doubleArrayOf(0.0, 0.0, 0.0)

        // looking ad how many arrows would be blocked by the element
        var i = 0
        for (e in Element.values()){
            if (e == element){
                continue
            }
            val arrowMetrics = getArrowCountForElement(position, scoreMap) / maxNumberOfSatisfiedArrows.toDouble()
            arrowsBlockedMetrix[i] = arrowMetrics

            i++
        }


        //the same for discs
        val discsThatWouldBeFreedByElement = scoreMap[position]!!.discsIfAirPlaced
        val maxNumberOfFreedDiscs = getMaximumNumberOfFreedDiscs(scoreMap)

        val discsFreedMetrics = discsThatWouldBeFreedByElement.toDouble() / maxNumberOfFreedDiscs

        val discsBlockedMetrix = doubleArrayOf(0.0, 0.0, 0.0)

        // looking ad how many discs would be blocked by the element
        i = 0
        for (e in Element.values()){
            if (e == element){
                continue
            }
            val discMetrics = getDiscCountForElement(position, scoreMap) / maxNumberOfFreedDiscs.toDouble()
            discsBlockedMetrix[i] = discMetrics

            i++
        }

        val arrowWeight = 0.8
        val discWeight = 1.0

        val arrowBlockedWeight = 0.4
        val discBlockedWeight = 0.7

        val resultMetrics = arrowSatisfiedMetrics * arrowWeight - arrowBlockedWeight * (arrowsBlockedMetrix.sum()/3) + discsFreedMetrics * discWeight - discBlockedWeight * (discsBlockedMetrix.sum()/3)




        return resultMetrics
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
    private fun getNewScoreMapForTile(scoreMap: Map<Pair<Int, Int>, CoordinateInformation>, tile: Tile, position: Pair<Int, Int>): MutableMap<Pair<Int, Int>, CoordinateInformation> {
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

    fun calculateBoardScore(scoreMap: Map<Pair<Int, Int>, CoordinateInformation>): Double {

        //weights
        val arrowDensityWeight = 1 //concentration of discs pointing to a particular position
        val disFreedomWeight = 1 //potential amount of discs that could de freed
        val interferenceWeight = 1 // how much the placement of one element can interfere with the placement of other elements in adjacent positions

        var countForAirArrow = 0.0
        var countForEarthArrow = 0.0
        var countForWaterArrow = 0.0
        var countForFireArrow = 0.0

        var countPositionsWithAirArrows = 0.0
        var countPositionsWithEarthArrows = 0.0
        var countPositionsWithWaterArrows = 0.0
        var countPositionsWithFireArrows = 0.0

        //same for discs freed
        var countForAirDiscs = 0.0
        var countForEarthDiscs = 0.0
        var countForWaterDiscs = 0.0
        var countForFireDiscs = 0.0

        var countPositionsWithAirDiscs = 0.0
        var countPositionsWithEarthDiscs = 0.0
        var countPositionsWithWaterDiscs = 0.0
        var countPositionsWithFireDiscs = 0.0


        for (info in scoreMap.values) {
            if (!info.occupied) {

                countForAirArrow += info.airCount / (info.gameDistance + 1)
                countForEarthArrow += info.earthCount / (info.gameDistance + 1)
                countForWaterArrow += info.waterCount / (info.gameDistance + 1)
                countForFireArrow += info.fireCount / (info.gameDistance + 1)

                if (info.airCount > 0) {
                    countPositionsWithAirArrows += (1.0 / (info.gameDistance + 1))
                }
                if (info.earthCount > 0) {
                    countPositionsWithEarthArrows += (1.0 / (info.gameDistance + 1))
                }
                if (info.waterCount > 0) {
                    countPositionsWithWaterArrows += (1.0 / (info.gameDistance + 1))
                }
                if (info.fireCount > 0) {
                    countPositionsWithFireArrows += (1.0 / (info.gameDistance + 1))
                }


                //discs
                countForAirDiscs += info.discsIfAirPlaced / (info.gameDistance + 1)
                countForEarthDiscs += info.discsIfEarthPlaced / (info.gameDistance + 1)
                countForWaterDiscs += info.discsIfWaterPlaced / (info.gameDistance + 1)
                countForFireDiscs += info.discsIfFirePlaced / (info.gameDistance + 1)

                if (info.discsIfAirPlaced > 0) {
                    countPositionsWithAirDiscs += (1.0 / (info.gameDistance + 1))
                }
                if (info.discsIfEarthPlaced > 0) {
                    countPositionsWithEarthDiscs += (1.0 / (info.gameDistance + 1))
                }
                if (info.discsIfWaterPlaced > 0) {
                    countPositionsWithWaterDiscs += (1.0 / (info.gameDistance + 1))
                }
                if (info.discsIfFirePlaced > 0) {
                    countPositionsWithFireDiscs += (1.0 / (info.gameDistance + 1))
                }

            }




        }

        val airArrowCount = 1 - (countPositionsWithAirArrows /( countForAirArrow + 1))
        val earthArrowCount = 1 - (countPositionsWithEarthArrows / (countForEarthArrow + 1))
        val waterArrowCount = 1 - (countPositionsWithWaterArrows / (countForWaterArrow + 1))
        val fireArrowCount = 1 - (countPositionsWithFireArrows / (countForFireArrow + 1))

        val totalArrowDensityScore = (airArrowCount + earthArrowCount + waterArrowCount + fireArrowCount) / 4.0

        //discs
        val airDiscCount = 1 - (countPositionsWithAirDiscs / (countForAirDiscs + 1))
        val earthDiscCount = 1 - (countPositionsWithEarthDiscs / (countForEarthDiscs + 1))
        val waterDiscCount = 1 - (countPositionsWithWaterDiscs / (countForWaterDiscs + 1))
        val fireDiscCount = 1 - (countPositionsWithFireDiscs / (countForFireDiscs + 1))

        val totalDiscFreedomScore = (airDiscCount + earthDiscCount + waterDiscCount + fireDiscCount) / 4.0


        //calculation of the total score of the game board
        return (arrowDensityWeight * totalArrowDensityScore + disFreedomWeight * totalDiscFreedomScore) / 2.0
    }




    fun calculateSatisfiedArrowsFromTileInHand(player: Player, tile: Tile, position: Pair<Int, Int>): Double {
        var satisfiedArrows = 0.0

        for (arrow in tile.arrows) {
            var adjacentPosition = calculateAdjacentPosition(position, arrow.direction)

            // Keep moving in the arrow direction until we reach the end of the board or find a tile with matching element
            while (player.board.containsKey(adjacentPosition)) {
                if (player.board[adjacentPosition]?.element == arrow.element) {
                    satisfiedArrows++
                    arrow.disc.add(tile.discs.last())
                    break
                }
                adjacentPosition = calculateAdjacentPosition(adjacentPosition, arrow.direction)
            }
        }

        return (satisfiedArrows + (satisfiedArrows /tile.arrows.size)*2.0) / 6.0
    }




    private fun updateScoreMapForArrows(board: Map<Pair<Int, Int>, Tile>, scoreMap: MutableMap<Pair<Int, Int>, CoordinateInformation>) {
        for ((position, tile) in board) {
            scoreMap.putAll(getNewScoreMapForTile(scoreMap, tile, position))
        }
    }

    /**
     * [getLowestGameDistanceFromNeighbours]is used to determine the smallest game distance
     * among adjacent positions of a given position on the game board
     */
    private fun getLowestGameDistanceFromNeighbours(position: Pair<Int, Int>, scoreMap: Map<Pair<Int, Int>, CoordinateInformation>): Int {
        var lowestGameDistance = Int.MAX_VALUE
        for (direction in Direction.tileDirection()) {
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
    fun fillScoreMap(board: Map<Pair<Int, Int>, Tile>): MutableMap<Pair<Int, Int>, CoordinateInformation> {
        val scoreMap = mutableMapOf<Pair<Int, Int>, CoordinateInformation>()
        val queue: Queue<Pair<Int, Int>> = LinkedList()

        // Initialize scoreMap for all tiles on the board
        for ((position, _) in board) {
            val info = CoordinateInformation()
            info.occupied       = true
            info.gameDistance   = -1
            scoreMap[position]  = info
            queue.offer(position)
        }

        // Expand from each tile on the board until the game distance exceeds range
        while (queue.isNotEmpty()) {
            val position = queue.poll()
            for (direction in Direction.tileDirection()) {
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
