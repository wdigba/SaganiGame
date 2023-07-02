package service

import entity.*
import entity.Element.*
import java.util.*

/**
 * [KIService] is responsible for executing the smart AI strategy
 */
class KIService(private val rootService: RootService) {
    // depth of searching for the tile placement
    private val range = 3

    /**
     * [CoordinateInformation] contains specific information about possible tile placement
     */
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

        // this variable represents the number of steps after which a tile could be placed
        var gameDistance = 0

        // is the position occupied by a tile already
        var occupied = false

        /**
         * copy of all coordinate information
         */
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

    /**
     * [TilePlacementInformation] contains general information about tile placement
     */
    class TilePlacementInformation (
        var tile: Tile,
        var direction: Direction,
        var location: Pair<Int, Int>,
        var score: Double
    )

    /**
     * [getAverageScoreForTileWith] calculates possible average score for the tile from stack
     * by subtracting used tiles from remaining.
     * Happens when we decide between card from offer display or card from stack
     */
    private fun getAverageScoreForTileWith(
        element: Element, numberOfArrows: Int,
        player: Player,
        scoreMap: Map<Pair<Int, Int>, CoordinateInformation>) : Double {

       val currentGame = rootService.currentGame
       checkNotNull(currentGame) { "There is no game." }

       val stackOfRemainingCards = currentGame.stacks

       val allPossibleBestScores = mutableListOf<Double>()

       for (tile in stackOfRemainingCards) {
           if (tile.element == element && tile.arrows.size == numberOfArrows) {
                //get best 3 scores for this tile
                val potentialPlacements = calculatePotentialTilePlacements(tile, scoreMap, player)
                val bestPlacements = potentialPlacements.sortedByDescending { it.score }.take(1)

                //get average score of these placements
                val averageScore = bestPlacements.map { it.score }.average()
                allPossibleBestScores.add(averageScore)
           }
       }
       return allPossibleBestScores.average()
   }

    /**
     * [playBestMove] contains the logic of choosing and executing the best move in the turn
     * using [PlayerActionService.placeTile]
     */
    fun playBestMove() {
        val currentGame = rootService.currentGame
        checkNotNull(currentGame) { "There is no game." }

        val player = currentGame.actPlayer

        val board = player.board
        if (board.isEmpty()) {
            //choose the tile with highest tile.arrows.size from the offerDisplay
            val tile = currentGame.offerDisplay.maxByOrNull { it.arrows.size }
            rootService.playerActionService.placeTile(tile!!, Direction.UP, Pair(0, 0))
            return
        }

        // defining thresholds for estimating progress
        val intermezzoScoreThreshold = 0.7

        val scoreMap = buildScoreMap(board)

        // when the game in intermezzo phase
        if (currentGame.intermezzo) {
            val possibleTiles = currentGame.intermezzoStorage

            val highestScoresTop = mutableListOf<TilePlacementInformation>()

            for (tile in possibleTiles) {
                val potentialPlacements = calculatePotentialTilePlacements(tile, scoreMap, player)
                // get a list of top 10 placements
                highestScoresTop.addAll(potentialPlacements.toList().sortedByDescending { it.score }.take(10))
            }
            // choosing the best move among others based on calculated score
            val move = chooseBestMove(highestScoresTop)

            if (move.score >= intermezzoScoreThreshold){ // do move only if it's a good move
                rootService.playerActionService.placeTile(move.tile, move.direction, move.location)
            }
            return
        }

        // when the game is not in intermezzo phase
        val possibleTiles = currentGame.offerDisplay

        val highestScoresTop = mutableListOf<TilePlacementInformation>()

        for (tile in possibleTiles) {
            val potentialPlacements = calculatePotentialTilePlacements(tile, scoreMap, player)
            // get a list of top 10 placements
            highestScoresTop.addAll(potentialPlacements.toList().sortedByDescending { it.score }.take(10))
        }
        val move = chooseBestMove(highestScoresTop)

        // when it is the last tile on the offer display, and we have to decide
        // to pick this tile or the tile from stack
        if ( (possibleTiles.size == 1) &&
            (move.score < getAverageScoreForTileWith(
                currentGame.stacks.first().element, currentGame.stacks.first().arrows.size, player, scoreMap))) {
            val tileFromStack = currentGame.stacks.first()
            val potentialPlacements = calculatePotentialTilePlacements(tileFromStack, scoreMap, player)
            val bestMove = potentialPlacements.maxByOrNull { it.score }

            checkNotNull(bestMove) { "Error best move cannot be empty." }

            rootService.playerActionService.placeTile(tileFromStack, bestMove.direction, bestMove.location)
        } else {
            rootService.playerActionService.placeTile(move.tile, move.direction, move.location)
        }
    }

    /**
     * [chooseBestMove] chooses the best move among all available
     * @return best available move
     */
    private fun chooseBestMove(listOfMoves: List<TilePlacementInformation>) : TilePlacementInformation {
        if (listOfMoves.isEmpty()) {
            throw IllegalArgumentException("There is no best move.")
        }
        // get best move by getting the maximum score
        val bestMove = listOfMoves.maxByOrNull { it.score }
        return bestMove!!
    }

    /**
     * [buildScoreMap] creates score map, fills it with tiles and updates with additional information
     * @param board is the current player´s board without additional information
     * @return updated board
     */
    fun buildScoreMap(board: Map<Pair<Int, Int>, Tile>) : Map<Pair<Int, Int>, CoordinateInformation>{
        val scoreMap = fillScoreMap(board)
        updateScoreMapForArrows(board, scoreMap)
        return scoreMap
    }

    /**
     * [calculateAdjacentPosition] determining the appropriate positions depending on the given direction
     * @return new position
     */
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

    /**
     * [isOnlyUnsatisfiedArrow] whenever the specified arrow is the only one unsatisfied arrow of the given tile
     * @return true if only one arrow is without disc on it
     */
    private fun isOnlyUnsatisfiedArrow(currentArrow: Arrow, tile: Tile): Boolean {
        var unsatisfiedCount = 0
        for (arrow in tile.arrows) {
            if (arrow.disc.size == 0) {
                unsatisfiedCount++
            }
        }
        return unsatisfiedCount == 1 && currentArrow.disc.size == 0
    }

    /**
     * [deepCopyScoreMap] creates copy of a current score map with all additional information
     * @return copied map
     */
    private fun deepCopyScoreMap (scoreMap: Map<Pair<Int, Int>, CoordinateInformation>) :
            MutableMap<Pair<Int, Int>, CoordinateInformation> {
        val newScoreMap = mutableMapOf<Pair<Int, Int>, CoordinateInformation>()
        for ((position, info) in scoreMap) {
            newScoreMap[position] = info.copy()
        }
        return newScoreMap
    }

    /**
     * [calculatePotentialTilePlacements] calculates potential estimates of tile placement
     * based on information about the coordinates and distances on the game board
     * @param tile that we want to place
     * @param scoreMap full score map with additional information about potential placements
     * @param player current player
     * @return calculated position for the tile, its rotation and placement´s score
     */
    fun calculatePotentialTilePlacements(
        tile: Tile,
        scoreMap: Map<Pair<Int, Int>, CoordinateInformation>, player: Player): MutableList<TilePlacementInformation> {

        val arrowWeight = 0.5

        val potentialScores = mutableListOf<TilePlacementInformation>()

        for ((position, coordinateInfo) in scoreMap) {
            // do not calculate for all already placed tiles
            if (coordinateInfo.occupied || coordinateInfo.gameDistance != 0) {
                continue
            }
            // rotate each tile in 4 directions
            for (rotation in Direction.tileDirection()) {
                tile.rotate(rotation)

                // calculate metrics for each position
                val positionMetrics = getMetricsForPosition(position, scoreMap, tile, player.discs.size)
                val satisfiedArrowsMetrics = calculateSatisfiedArrowsFromTileInHand(player, tile, position)

                // get a Map with the same values as player.board
                val newPlayerBoard = player.board.toMutableMap()
                // add the tile to the new board
                newPlayerBoard[position] = tile
                // get new board
                val updatedScoreMap = buildScoreMap(newPlayerBoard)
                // calculate the whole score for the board
                val score = calculateBoardScore(updatedScoreMap)
                // get potential scores based on metrics

                val positionScore = score + arrowWeight * satisfiedArrowsMetrics + positionMetrics
                potentialScores.add(TilePlacementInformation(tile, rotation, position, positionScore))

                if (position == Pair(2, -1) && rotation == Direction.UP) {
                    println("Score: $score")
                    println("Satisfied Arrows: $satisfiedArrowsMetrics")
                }
            }
        }
        return potentialScores
    }

    /**
     * [getMaximumNumberOfArrowsThatCanBeSatisfied] calculates how many arrows of one element
     * could be satisfied by placing tile on the chosen position
     * @return the maximum number of satisfied arrows
     */
    private fun getMaximumNumberOfArrowsThatCanBeSatisfied(scoreMap: Map<Pair<Int, Int>, CoordinateInformation>): Int {
        var maxCount = 0
        //for all positions
        for ((_, coordinateInfo) in scoreMap) {
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

    /**
     * [getMaximumNumberOfFreedDiscs] calculates how many discs of on element could be freed after placing a tile
     * @return maximum amount of discs
     */
    private fun getMaximumNumberOfFreedDiscs(scoreMap: Map<Pair<Int, Int>, CoordinateInformation>): Int {
        var maxCount = 0
        //for all positions
        for ((_, coordinateInfo) in scoreMap) {
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

    /**
     * [getArrowCountForElement] calculates total count of arrows of each element for a given position in a scoreMap
     * @return total amount of arrows
     */
    private fun getArrowCountForElement(
        position: Pair<Int, Int>, scoreMap: Map<Pair<Int, Int>, CoordinateInformation>,
        element: Element) : Int {
        var arrowCount = 0
        if (element == AIR) {
            arrowCount += scoreMap[position]!!.airCount
        }
        if (element == EARTH) {
            arrowCount += scoreMap[position]!!.earthCount
        }
        if (element == WATER) {
            arrowCount += scoreMap[position]!!.waterCount
        }
        if (element == FIRE) {
            arrowCount += scoreMap[position]!!.fireCount
        }
        return arrowCount
    }

    /**
     * [getDiscCountForElement] calculates how many discs of each element would be freed for a given position in a scoreMap
     * @return total amount of discs
     */
    private fun getDiscCountForElement(
        position: Pair<Int, Int>, scoreMap: Map<Pair<Int, Int>, CoordinateInformation>,
        element: Element) : Int {
        var discCount = 0
        if (element == AIR) {
            discCount += scoreMap[position]!!.discsIfAirPlaced
        }
        if (element == EARTH) {
            discCount += scoreMap[position]!!.discsIfEarthPlaced
        }
        if (element == WATER) {
            discCount += scoreMap[position]!!.discsIfWaterPlaced
        }
        if (element == FIRE) {
            discCount += scoreMap[position]!!.discsIfFirePlaced
        }
        return discCount
    }

    /**
     * [getMetricsForPosition] calculates metrics for placing tile on a specific position
     * @param position probable placement
     * @param scoreMap map with all additional information
     * @param tile chosen tile which is about to be placed
     * @return placement´s probability (the higher, the better)
     */
    private fun getMetricsForPosition(
        position: Pair<Int, Int>,
        scoreMap: Map<Pair<Int, Int>, CoordinateInformation>,
        tile: Tile,
        playerDiscsCount: Int
    ): Double {

        val element = tile.element
        // arrow satisfaction for element
        val arrowsThatWouldBeSatisfiedByElement = getArrowCountForElement(position, scoreMap, element)
        // maximum amount of satisfied arrows
        var maxNumberOfSatisfiedArrows = getMaximumNumberOfArrowsThatCanBeSatisfied(scoreMap)

        // otherwise score is NaN
        if(maxNumberOfSatisfiedArrows == 0) {
            maxNumberOfSatisfiedArrows = 1
        }

        // arrows´ satisfaction metrics
        val arrowSatisfiedMetrics = arrowsThatWouldBeSatisfiedByElement.toDouble() / maxNumberOfSatisfiedArrows
        // how many arrows would be blocked by this decision
        val arrowsBlockedMetrics = doubleArrayOf(0.0, 0.0, 0.0)

        // looking ad how many arrows would be blocked by the element
        var i = 0
        for (e in Element.values()){
            if (e == element){
                continue
            }
            val arrowMetrics = getArrowCountForElement(position, scoreMap, element) / maxNumberOfSatisfiedArrows.toDouble()
            arrowsBlockedMetrics[i] = arrowMetrics
            i++
        }


        //the same for discs
        val discsThatWouldBeFreedByElement = getDiscCountForElement(position, scoreMap, element)

        var maxNumberOfFreedDiscs = getMaximumNumberOfFreedDiscs(scoreMap)

        if(maxNumberOfFreedDiscs == 0){
            maxNumberOfFreedDiscs = 1
        }

        val discsFreedMetrics = discsThatWouldBeFreedByElement.toDouble() / maxNumberOfFreedDiscs

        val discsBlockedMetrics = doubleArrayOf(0.0, 0.0, 0.0)

        // looking ad how many discs would be blocked by the element
        i = 0
        for (e in Element.values()){
            if (e == element){
                continue
            }
            val discMetrics = getDiscCountForElement(position, scoreMap, element) / maxNumberOfFreedDiscs.toDouble()
            discsBlockedMetrics[i] = discMetrics

            i++
        }

        // influence rating of each parameter
        val arrowWeight = 0.8
        val discWeight = 2.0-(playerDiscsCount/24)

        val arrowBlockedWeight = 0.4
        val discBlockedWeight = 0.7

        // total calculation based on satisfied metrics
        val resultMetrics = arrowSatisfiedMetrics * arrowWeight - arrowBlockedWeight * (arrowsBlockedMetrics.sum()/3)
        + discsFreedMetrics * discWeight - discBlockedWeight * (discsBlockedMetrics.sum()/3)

        return resultMetrics
    }

    /**
     * [updatePosition] updates the information about a specific position
     * @param newScoreMap current score map
     * @param positionToUpdate exact position
     * @param arrow checking for each arrow
     * @param tile current tile
     */
    private fun updatePosition(
        newScoreMap: MutableMap<Pair<Int, Int>, CoordinateInformation>,
        positionToUpdate: Pair<Int, Int>,
        arrow: Arrow,
        tile: Tile
    ) {
        val info = newScoreMap[positionToUpdate]!!
        val isTheOnlyUnsatisfiedArrow = isOnlyUnsatisfiedArrow(arrow, tile)
        //checking for the singe unsatisfied arrows, which could free the discs
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
     * [getNewScoreMapForTile] adds new tile to an existing score map
     * @param scoreMap current map
     * @param tile to place on the map
     * @param position position on the map
     * @return updated map
     */
    private fun getNewScoreMapForTile(
        scoreMap: Map<Pair<Int, Int>, CoordinateInformation>,
        tile: Tile,
        position: Pair<Int, Int>): MutableMap<Pair<Int, Int>, CoordinateInformation> {
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
     * [calculateBoardScore] calculates the total score for the whole board
     * contains main calculating information
     */
    fun calculateBoardScore(scoreMap: Map<Pair<Int, Int>, CoordinateInformation>): Double {

        //weights
        val arrowDensityWeight = 1 //concentration of discs pointing to a particular position
        val disFreedomWeight = 1 //potential amount of discs that could de freed
        // how much the placement of one element can interfere with the placement of other elements in adjacent positions

        // general count variables for each element of arrows
        var countForAirArrow = 0.0
        var countForEarthArrow = 0.0
        var countForWaterArrow = 0.0
        var countForFireArrow = 0.0

        var countPositionsWithAirArrows = 0.0
        var countPositionsWithEarthArrows = 0.0
        var countPositionsWithWaterArrows = 0.0
        var countPositionsWithFireArrows = 0.0

        // general count variables for each element of freed discs
        var countForAirDiscs = 0.0
        var countForEarthDiscs = 0.0
        var countForWaterDiscs = 0.0
        var countForFireDiscs = 0.0

        var countPositionsWithAirDiscs = 0.0
        var countPositionsWithEarthDiscs = 0.0
        var countPositionsWithWaterDiscs = 0.0
        var countPositionsWithFireDiscs = 0.0

        for (info in scoreMap.values) {
            // when tile placement is already taken
            if (info.occupied || info.gameDistance < 0) {
                continue
            }

            // the closer placement, the better
            countForAirArrow += info.airCount / (info.gameDistance + 1)
            countForEarthArrow += info.earthCount / (info.gameDistance + 1)
            countForWaterArrow += info.waterCount / (info.gameDistance + 1)
            countForFireArrow += info.fireCount / (info.gameDistance + 1)

            // calculating positions based on arrows
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

            //same for discs
            countForAirDiscs += info.discsIfAirPlaced / (info.gameDistance + 1)
            countForEarthDiscs += info.discsIfEarthPlaced / (info.gameDistance + 1)
            countForWaterDiscs += info.discsIfWaterPlaced / (info.gameDistance + 1)
            countForFireDiscs += info.discsIfFirePlaced / (info.gameDistance + 1)

            // calculating positions based on discs
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
        // calculation for comparable results for arrows
        val airArrowCount = 1 - (countPositionsWithAirArrows /( countForAirArrow + 1))
        val earthArrowCount = 1 - (countPositionsWithEarthArrows / (countForEarthArrow + 1))
        val waterArrowCount = 1 - (countPositionsWithWaterArrows / (countForWaterArrow + 1))
        val fireArrowCount = 1 - (countPositionsWithFireArrows / (countForFireArrow + 1))

        val totalArrowDensityScore = (airArrowCount + earthArrowCount + waterArrowCount + fireArrowCount) / 4.0

        // calculation for comparable results for discs
        val airDiscCount = 1 - (countPositionsWithAirDiscs / (countForAirDiscs + 1))
        val earthDiscCount = 1 - (countPositionsWithEarthDiscs / (countForEarthDiscs + 1))
        val waterDiscCount = 1 - (countPositionsWithWaterDiscs / (countForWaterDiscs + 1))
        val fireDiscCount = 1 - (countPositionsWithFireDiscs / (countForFireDiscs + 1))

        val totalDiscFreedomScore = (airDiscCount + earthDiscCount + waterDiscCount + fireDiscCount) / 4.0

        //calculation of the total score of the game board
        return (arrowDensityWeight * totalArrowDensityScore + disFreedomWeight * totalDiscFreedomScore) / 2.0
    }

    /**
     * [calculateSatisfiedArrowsFromTileInHand]  calculates the satisfaction level of arrows
     * from a tile placed on a player's board at a given position
     * @return satisfaction level for arrows of the tile
     */
    private fun calculateSatisfiedArrowsFromTileInHand(player: Player, tile: Tile, position: Pair<Int, Int>): Double {
        //number of satisfied arrows
        var satisfiedArrows = 0.0

        for (arrow in tile.arrows) {
            var adjacentPosition = calculateAdjacentPosition(position, arrow.direction)

            // keep moving in the arrow direction until we reach the end of the board or find a tile with matching element
            while (player.board.containsKey(adjacentPosition)) {
                if (player.board[adjacentPosition]?.element == arrow.element) {
                    satisfiedArrows++
                    //arrow.disc.add(tile.discs.removeAt(tile.discs.size-1))
                    break
                }
                adjacentPosition = calculateAdjacentPosition(adjacentPosition, arrow.direction)
            }
        }
        // maximum level is 1
        return (satisfiedArrows + (satisfiedArrows /tile.arrows.size) * 2.0) / 6.0
    }

    /**
     * [updateScoreMapForArrows] puts all tiles in all position to the score map
     */
    private fun updateScoreMapForArrows(
        board: Map<Pair<Int, Int>, Tile>,
        scoreMap: MutableMap<Pair<Int, Int>, CoordinateInformation>) {
        for ((position, tile) in board) {
            scoreMap.putAll(getNewScoreMapForTile(scoreMap, tile, position))
        }
    }

    /**
     * [getLowestGameDistanceFromNeighbours]is used to determine the smallest game distance
     * among adjacent positions of a given position on the game board
     * @return position number
     */
    private fun getLowestGameDistanceFromNeighbours(
        position: Pair<Int, Int>,
        scoreMap: Map<Pair<Int, Int>, CoordinateInformation>): Int {
        var lowestGameDistance = Int.MAX_VALUE
        for (direction in Direction.tileDirection()) {
            val neighbourPos = calculateAdjacentPosition(position, direction)
            if (scoreMap.containsKey(neighbourPos) && scoreMap[neighbourPos]!!.gameDistance < lowestGameDistance) {
                lowestGameDistance = scoreMap[neighbourPos]!!.gameDistance
            }
        }
        // the lowest position is normally 0
        return lowestGameDistance
    }

    /**
     * [fillScoreMap] is used to fill the score Map for the player in definite range (till definite number).
     * This information can be used to evaluate positions and make decisions in the game
     */
    private fun fillScoreMap(board: Map<Pair<Int, Int>, Tile>): MutableMap<Pair<Int, Int>, CoordinateInformation> {
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
                if (scoreMap.containsKey(adjacentPos)) { // already visited
                    continue
                }
                // filling the board with information
                val info = CoordinateInformation()
                info.gameDistance = getLowestGameDistanceFromNeighbours(adjacentPos, scoreMap) + 1
                if (info.gameDistance <= range) {
                    scoreMap[adjacentPos] = info
                    queue.offer(adjacentPos)
                }
            }
        }
        return scoreMap
    }
}
