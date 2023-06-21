package service

import Location
import entity.*

/**
 * [PlayerActionService] provides player functions for the game.
 */
class PlayerActionService(private val rootService: RootService) : AbstractRefreshingService() {
    /**
     * [placeTile] is called when a player places a tile on their board.
     * It checks if the move is legal.
     * Chosen tile is placed in location in given direction.
     * Discs are relocated to arrows with the same element that points to the new card. Player may get points and discs.
     * New tile gets discs and its discs get relocated if possible.
     */
    fun placeTile(tile: Tile, direction: Direction, location: Location) {
        var tileFrom: String

        // check if game exists
        val currentGame = rootService.currentGame
        checkNotNull(currentGame) { "There is no game." }

        // identify player
        val player = if (currentGame.intermezzo) {
            currentGame.intermezzoPlayers[0]
        } else {
            currentGame.actPlayer
        }

        // check if position is free
        require(!player.board.contains(location)) { "Location is already used." }

        // check if position is available
        require(location in validLocation(player.board)) { "Location is not adjacent to another tile." }

        // check if tile is legal to choose and remove it
        if (currentGame.intermezzo) {
            check(tile in currentGame.intermezzoStorage) {
                "You can't take this tile. It is not part of the intermezzo storage."
            }
            currentGame.intermezzoStorage.remove(tile)
            tileFrom = "intermezzoStorage"
        } else if (currentGame.offerDisplay.size > 1) {
            check(tile in currentGame.offerDisplay) {
                "You can't take this tile. It is not part of the offer display."
            }
            currentGame.offerDisplay.remove(tile)
            tileFrom = "offerDisplay"
        } else {
            check(tile in currentGame.offerDisplay || tile == currentGame.stacks[0]) {
                "You can't take this tile. It is not part of the offer display or the top card of the stacks."
            }
            tileFrom = "offerDisplay"
            if (!currentGame.offerDisplay.remove(tile)) {
                currentGame.stacks.removeFirst()
                tileFrom = "stacks"
            }
        }

        // rotate tile
        tile.rotate(direction)

        // place tile
        player.board[location] = tile

        // relocate discs, flip solved tiles and get points (other tiles)
        relocateDiscs(player, tile, location, currentGame.turnCount)

        // place discs if confirmed and use cacophony discs if necessary
        repeat(tile.arrows.size) {
            if (player.discs.isEmpty()) {
                player.discs.add(Disc.CACOPHONY)
                player.points = Pair(player.points.first - 2, currentGame.turnCount)
            }
            tile.discs.add(player.discs.removeFirst())
        }

        // relocate discs on new tile
        var fulfilledArrowCount = 0
        tile.arrows.forEach { arrow ->
            var filteredBoard = filterInDirection(player.board, arrow.direction, location)
            filteredBoard = filteredBoard.filterValues { it.element == arrow.element }
            if (filteredBoard.isNotEmpty()) {
                arrow.disc.add(tile.discs.removeFirst())
                fulfilledArrowCount++
            }
        }

        // if solved, flip it and get points
        if (fulfilledArrowCount == tile.arrows.size) {
            tile.arrows.forEach { player.discs.add(it.disc.removeFirst()) }
            player.points = Pair(player.points.first + tile.points, currentGame.turnCount)
            tile.flipped = true
        }

        // refresh GUI
        onAllRefreshables { refreshAfterPlaceTile(player, tile, location) }

        // change to next player
        rootService.gameService.changeToNextPlayer()
    }

    /**
     * [validLocation] returns all valid locations for the given board
     */
    fun validLocation(board: MutableMap<Location, Tile>): Set<Location> {
        if (board.isEmpty()) {
            return mutableSetOf(Pair(0,0))
        }
        val validLocation: MutableSet<Location> = mutableSetOf()
        // add all location adjacent to given tiles
        board.keys.forEach {
            validLocation.add(Pair(it.first - 1, it.second))
            validLocation.add(Pair(it.first + 1, it.second))
            validLocation.add(Pair(it.first, it.second - 1))
            validLocation.add(Pair(it.first, it.second + 1))
        }
        // remove all location of given tiles
        validLocation.removeAll(board.keys)
        return validLocation
    }

    private fun relocateDiscs(player: Player, tile: Tile, location: Location, turnCount: Int) {
        // relocate discs to fulfilled arrows
        for (direction in Direction.values()) {
            var filteredBoard = filterInDirection(player.board, direction, location)
            filteredBoard = filteredBoard.filterValues {
                it.arrows.contains(Arrow(tile.element, Direction.values()[(direction.ordinal + 4) % 8]))
                        && !it.flipped
            }
            filteredBoard.values.forEach {
                var flip = true
                it.arrows.forEach { arrow ->
                    if (arrow.direction == Direction.values()[(direction.ordinal + 4) % 8] && arrow.disc.isEmpty()) {
                        arrow.disc.add(it.discs.removeFirst())
                    }
                    if (flip) {
                        flip = (arrow.disc.size == 1)
                    }
                }
                if (flip) {
                    it.arrows.forEach { arrow ->
                        player.discs.add(arrow.disc.removeFirst())
                    }
                    player.points = Pair(player.points.first + it.points, turnCount)
                    it.flipped = true
                }
            }
        }
    }

    /**
     * [filterInDirection] returns a filtered Map of all tiles in the given direction from the given tile
     */
    fun filterInDirection(
        board: MutableMap<Location, Tile>,
        direction: Direction,
        location: Location
    ): Map<Location, Tile> {
        return when (direction) {
            Direction.UP -> board.filterKeys {
                it.first == location.first && it.second > location.second
            }

            Direction.UP_RIGHT -> board.filterKeys {
                it.first - location.first == it.second - location.second && it.first > location.first
            }

            Direction.RIGHT -> board.filterKeys {
                it.first > location.first && it.second == location.second
            }

            Direction.DOWN_RIGHT -> board.filterKeys {
                it.first - location.first == location.second - it.second && it.first > location.first
            }

            Direction.DOWN -> board.filterKeys {
                it.first == location.first && it.second < location.second
            }

            Direction.DOWN_LEFT -> board.filterKeys {
                it.first - location.first == it.second - location.second && it.first < location.first
            }

            Direction.LEFT -> board.filterKeys {
                it.first < location.first && it.second == location.second
            }

            else -> board.filterKeys {
                it.first - location.first == location.second - it.second && it.first < location.first
            }
        }
    }
}
