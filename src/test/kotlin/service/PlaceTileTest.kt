package service

import entity.*
import kotlin.test.*

/**
 * [PlaceTileTest] tests [PlayerActionService].placeTile()
 */
class PlaceTileTest {
    private val rootService = RootService()
    private val alice = Triple("Alice", Color.WHITE, PlayerType.HUMAN)
    private val bob = Triple("Bob", Color.BROWN, PlayerType.HUMAN)
    private val playerNames = mutableListOf(alice, bob)

    /**
     * test with legal arguments
     */
    @Test
    fun correctTest() {
        // use TestRefreshable
        val refreshable = TestRefreshable()
        rootService.addEachRefreshable(refreshable)
        // testData
        var tile = Tile(2, 1, Element.FIRE, listOf(Arrow(Element.FIRE, Direction.UP)))
        var direction = Direction.DOWN
        var location = Pair(0, 0)
        rootService.gameService.startNewGame(playerNames)
        val game = rootService.currentGame
        assertNotNull(game)
        game.offerDisplay.add(tile)
        game.players[0].discs.clear()
        repeat(24) {
            game.players[0].discs.add(Disc.SOUND)
        }
        // before function call
        assertEquals(Direction.UP, tile.direction)
        assert(location !in game.players[0].board.keys)
        assertEquals(0, tile.discs.size)
        assertEquals(24, game.players[0].discs.size)
        assertFalse(refreshable.refreshAfterPlaceTileCalled)
        // function call
        rootService.playerActionService.placeTile(tile, direction, location)
        // tests
        // tile has given direction
        assertEquals(direction, tile.direction)
        // tile is placed in given location
        assertEquals(game.players[0].board[location], tile)
        // disc placed
        assertEquals(1, tile.discs.size)
        // player used a disc
        assertEquals(23, game.players[0].discs.size)
        // refreshAfterPlaceTile() was called
        assert(refreshable.refreshAfterPlaceTileCalled)
        // reset refreshable
        refreshable.reset()

        // testData intermezzo
        tile = Tile(
            10, 3, Element.FIRE, listOf(
                Arrow(Element.FIRE, Direction.UP),
                Arrow(Element.WATER, Direction.UP_RIGHT)
            )
        )
        val previousTile = game.players[0].board[Pair(0, 0)]
        assertNotNull(previousTile)
        direction = Direction.UP
        location = Pair(0, -1)
        game.intermezzoPlayers.addAll(game.players)
        assertEquals(game.intermezzoPlayers, game.players)
        game.intermezzoStorage.add(tile)
        game.intermezzo = true
        // before function call
        assert(location !in game.players[0].board.keys)
        assertEquals(0, tile.discs.size)
        assertFalse(previousTile.flipped)
        assertEquals(0, game.players[0].points.first)
        assertEquals(23, game.players[0].discs.size)
        assertFalse(refreshable.refreshAfterPlaceTileCalled)
        // function call
        rootService.playerActionService.placeTile(tile, direction, location)
        // tests
        // tile has given direction
        assertEquals(direction, tile.direction)
        // tile is placed in given location
        assertEquals(game.players[0].board[location], tile)
        // disc placed
        assertEquals(1, tile.discs.size)
        assertEquals(1, tile.arrows[0].disc.size)
        // firstTile flipped
        assert(previousTile.flipped)
        assertEquals(1, game.players[0].points.first)
        // player gets 1 disc back and uses 2 discs
        assertEquals(22, game.players[0].discs.size)
        // refreshAfterPlaceTile() was called
        assert(refreshable.refreshAfterPlaceTileCalled)
        // reset refreshable
        refreshable.reset()

        // testData tile from stacks without discs
        tile = Tile(
            37, 1, Element.WATER, listOf(Arrow(Element.WATER, Direction.UP_RIGHT))
        )
        direction = Direction.DOWN
        location = Pair(0, 1)
        game.intermezzo = false
        game.actPlayer = game.players[0]
        game.offerDisplay.clear()
        game.offerDisplay.add(previousTile)
        game.stacks.add(0, tile)
        game.actPlayer.discs.clear()
        // before function call
        assert(location !in game.players[0].board.keys)
        assertEquals(0, tile.discs.size)
        assertEquals(0, game.players[0].discs.size)
        assertEquals(1, game.players[0].points.first)
        assertFalse(refreshable.refreshAfterPlaceTileCalled)
        // function call
        rootService.playerActionService.placeTile(tile, direction, location)
        // tests
        // tile has given direction
        assertEquals(direction, tile.direction)
        // tile is placed in given location
        assertEquals(game.players[0].board[location], tile)
        // disc placed
        assertEquals(1, tile.discs.size)
        // player buys cacophony disc and uses it
        assertEquals(0, game.players[0].discs.size)
        // player loses 2 points for cacophony disc
        assertEquals(-1, game.players[0].points.first)
        // refreshAfterPlaceTile() was called
        assert(refreshable.refreshAfterPlaceTileCalled)
        // reset refreshable
        refreshable.reset()

        // testData flip new disc
        tile = Tile(
            40, 3, Element.WATER, listOf(
                Arrow(Element.FIRE, Direction.UP_RIGHT),
                Arrow(Element.WATER, Direction.UP_LEFT)
            )
        )
        direction = Direction.RIGHT
        location = Pair(-1, 0)
        game.actPlayer = game.players[0]
        game.offerDisplay.add(tile)
        // before function call
        assert(location !in game.players[0].board.keys)
        assertFalse(tile.flipped)
        assertEquals(0, game.players[0].discs.size)
        assertEquals(-1, game.players[0].points.first)
        assertFalse(refreshable.refreshAfterPlaceTileCalled)
        // function call
        rootService.playerActionService.placeTile(tile, direction, location)
        // tests
        // tile has given direction
        assertEquals(direction, tile.direction)
        // tile is placed in given location
        assertEquals(game.players[0].board[location], tile)
        // tile flipped
        assert(tile.flipped)
        // player get 1 disc back, buys a cacophony disc, uses 2 discs and get them back
        assertEquals(2, game.players[0].discs.size)
        // player gets 4 points for flipped tiles and loses 2 points for cacophony disc
        assertEquals(1, game.players[0].points.first)
        // refreshAfterPlaceTile() was called
        assert(refreshable.refreshAfterPlaceTileCalled)
    }

    /**
     * test without game
     */
    @Test
    fun noGameTest() {
        // testData
        val tile = Tile(1, 1, Element.FIRE, listOf(Arrow(Element.FIRE, Direction.UP_RIGHT)))
        val direction = Direction.UP
        val location = Pair(0, 0)
        // illegal function call
        assertFailsWith<IllegalStateException>(
            "There is no game."
        ) { rootService.playerActionService.placeTile(tile, direction, location) }
    }

    /**
     * test with illegal locations
     */
    @Test
    fun illegalLocationTest() {
        // testData
        val tile = Tile(1, 1, Element.FIRE, listOf(Arrow(Element.FIRE, Direction.UP_RIGHT)))
        val direction = Direction.UP
        val location = Pair(0, 0)
        val location2 = Pair(2, 2)
        rootService.gameService.startNewGame(playerNames)
        val game = rootService.currentGame
        assertNotNull(game)
        game.intermezzoPlayers.addAll(game.players)
        assertEquals(game.intermezzoPlayers, game.players)
        game.players[0].board[location] = tile
        // illegal function calls
        assertFailsWith<IllegalArgumentException>(
            "Location is already used."
        ) { rootService.playerActionService.placeTile(tile, direction, location) }
        assertFailsWith<IllegalArgumentException>(
            "Location is not adjacent to another tile."
        ) { rootService.playerActionService.placeTile(tile, direction, location2) }
        // use intermezzo player
        game.intermezzo = true
        // illegal function calls
        assertFailsWith<IllegalArgumentException>(
            "Location is already used."
        ) { rootService.playerActionService.placeTile(tile, direction, location) }
        assertFailsWith<IllegalArgumentException>(
            "Location is not adjacent to another tile."
        ) { rootService.playerActionService.placeTile(tile, direction, location2) }
    }

    /**
     * test with not eligible tile
     */
    @Test
    fun illegalTileTest() {
        // testData
        val tile = Tile(1, 1, Element.FIRE, listOf(Arrow(Element.FIRE, Direction.UP_RIGHT)))
        val direction = Direction.UP
        val location = Pair(0, 0)
        rootService.gameService.startNewGame(playerNames)
        val game = rootService.currentGame
        assertNotNull(game)
        game.intermezzoPlayers.addAll(game.players)
        assertEquals(game.intermezzoPlayers, game.players)
        game.offerDisplay.remove(tile)
        // illegal function calls
        assertFailsWith<IllegalStateException>(
            "You can't take this tile. It is not part of the offer display."
        ) { rootService.playerActionService.placeTile(tile, direction, location) }
        // offerDisplay with not more than 1 tile
        game.offerDisplay.clear()
        while (game.stacks[0] == tile) {
            game.stacks.removeFirst()
        }
        // illegal function calls
        assertFailsWith<IllegalStateException>(
            "You can't take this tile. It is not part of the offer display or the top card of the stacks."
        ) { rootService.playerActionService.placeTile(tile, direction, location) }
        // intermezzo
        game.intermezzo = true
        // illegal function calls
        assertFailsWith<IllegalStateException>(
            "You can't take this tile. It is not part of the intermezzo storage."
        ) { rootService.playerActionService.placeTile(tile, direction, location) }
    }
}
