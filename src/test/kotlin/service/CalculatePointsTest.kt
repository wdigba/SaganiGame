package service
import kotlin.test.*
import entity.*
import Location

/**
 * Tests the calculatePoints function of [GameService]
 */
class CalculatePointsTest {
    private val rootService = RootService()
    private val alice = Triple("Alice", Color.WHITE, PlayerType.HUMAN)
    private val bob = Triple("Bob", Color.BROWN, PlayerType.HUMAN)
    private val playerNames = mutableListOf(alice, bob)

    private val coordinates = mutableListOf<Location>(
            Pair(0,0), Pair(0,1), Pair(0,-1),
            Pair(0,2), Pair(-1,0), Pair(1,1),
            Pair(1,0), Pair(-2,0), Pair(-1,-1),
            Pair(-2,-1), Pair(2,0), Pair(1,-1)
            )
    private val directions = mutableListOf(
            Direction.UP,
            Direction.UP,
            Direction.UP,
            Direction.DOWN,
            Direction.RIGHT,
            Direction.DOWN,
            Direction.DOWN,
            Direction.RIGHT,
            Direction.UP,
            Direction.UP,
            Direction.UP,
            Direction.UP
    )

    /**
     * Simulates a board by adding tiles one by one
     */
    @Test
    fun testOne(){
        rootService.gameService.startNewGame(playerNames)
        val game = checkNotNull(rootService.currentGame)
        //get tiles manually for testing
        game.stacks.addAll(game.offerDisplay)
        game.stacks.sortBy { it.id }
        val presetTiles = mutableListOf<Tile>()
        presetTiles.add(game.stacks[1])
        presetTiles.add(game.stacks[9])
        presetTiles.add(game.stacks[51])
        presetTiles.add(game.stacks[22])
        presetTiles.add(game.stacks[40])
        presetTiles.add(game.stacks[27])
        presetTiles.add(game.stacks[63])
        presetTiles.add(game.stacks[61])
        presetTiles.add(game.stacks[32])
        presetTiles.add(game.stacks[40])
        presetTiles.add(game.stacks[20])
        presetTiles.add(game.stacks[70])
        presetTiles.add(game.stacks[11])
        presetTiles.add(game.stacks[35])
        // create a scenario
        val board = mutableMapOf<Location, Tile>(
            Pair(coordinates[0], presetTiles[0]),
            Pair(coordinates[1], presetTiles[1]),
            Pair(coordinates[2], presetTiles[2]),
            Pair(coordinates[3], presetTiles[3]),
            Pair(coordinates[4], presetTiles[4]),
            Pair(coordinates[5], presetTiles[5]),
            Pair(coordinates[6], presetTiles[6]),
            Pair(coordinates[7], presetTiles[7]),
            Pair(coordinates[8], presetTiles[8]),
            Pair(coordinates[9], presetTiles[9]),
            Pair(coordinates[10], presetTiles[10]),
        )
        //rotate the Tiles on the board
        for (i in 0..10) {
            board.forEach { it.value.rotate(directions[presetTiles.indexOf(it.value)]) }
        }
        assertEquals(directions[7], presetTiles[7].direction)

        //relocate the discs on the Tiles
        //ID: 2
        presetTiles[0].flipped = true
        //ID: 10
        presetTiles[1].discs.add(Disc.SOUND)
        presetTiles[1].discs.add(Disc.SOUND)
        //ID: 52
        presetTiles[2].arrows.first().disc.add(Disc.SOUND)
        presetTiles[2].discs.add(Disc.SOUND)
        presetTiles[2].discs.add(Disc.SOUND)
        presetTiles[2].discs.add(Disc.SOUND)
        //ID: 23
        presetTiles[3].discs.add(Disc.SOUND)
        presetTiles[3].discs.add(Disc.SOUND)
        presetTiles[3].discs.add(Disc.SOUND)
        //ID: 41
        presetTiles[4].flipped = true
        //ID: 28
        presetTiles[5].discs.add(Disc.SOUND)
        presetTiles[5].discs.add(Disc.SOUND)
        presetTiles[5].arrows[0].disc.add(Disc.SOUND)
        presetTiles[5].arrows[1].disc.add(Disc.SOUND)
        //ID: 64
        presetTiles[6].discs.add(Disc.SOUND)
        presetTiles[6].discs.add(Disc.SOUND)
        presetTiles[6].arrows[1].disc.add(Disc.SOUND)
        presetTiles[6].arrows[3].disc.add(Disc.SOUND)
        //ID: 62
        presetTiles[7].discs.add(Disc.SOUND)
        presetTiles[7].arrows[0].disc.add(Disc.SOUND)
        presetTiles[7].arrows[1].disc.add(Disc.SOUND)
        //ID: 33
        presetTiles[8].flipped = true
        //ID: 41
        presetTiles[9].flipped = true
        //ID: 21
        presetTiles[10].discs.add(Disc.SOUND)
        presetTiles[10].arrows[1].disc.add(Disc.SOUND)
        //ID: 71

        // add other stats
        game.actPlayer.board.clear()
        game.actPlayer.board.putAll(board)
        game.actPlayer.points = Pair(13,13)
        game.actPlayer.discs.clear()
        game.actPlayer.discs.add(Disc.SOUND)
        game.actPlayer.discs.add(Disc.SOUND)

        var tile = presetTiles[11]
        game.offerDisplay.add(tile)

        //Test, if the calculated stats are correct
        var output = rootService.gameService.calculatePoints(game.actPlayer, tile, Direction.UP, coordinates[11])
        assertEquals(mutableListOf(9,-4,2,4),output)
        output = rootService.gameService.calculatePoints(game.actPlayer, tile, Direction.RIGHT, coordinates[11])
        assertEquals(mutableListOf(9,-4,2,2),output)
        output = rootService.gameService.calculatePoints(game.actPlayer, tile, Direction.DOWN, coordinates[11])
        assertEquals(mutableListOf(9,-4,2,1),output)
        output = rootService.gameService.calculatePoints(game.actPlayer, tile, Direction.LEFT, coordinates[11])
        assertEquals(mutableListOf(9,-4,2,1),output)

        //Test another Tile
        tile = presetTiles[12]
        game.offerDisplay.add(tile)

        output = rootService.gameService.calculatePoints(game.actPlayer, tile, Direction.UP, Pair(-1,1))
        assertEquals(mutableListOf(19,1,0,1),output)
        output = rootService.gameService.calculatePoints(game.actPlayer, tile, Direction.RIGHT, Pair(-1,1))
        assertEquals(mutableListOf(22,3,0,3),output)
        //Same scenario, but the player has only one sound disc left
        game.actPlayer.discs.removeFirst()
        output = rootService.gameService.calculatePoints(game.actPlayer, tile, Direction.UP, Pair(-1,1))
        assertEquals(mutableListOf(19,1,0,1),output)
        output = rootService.gameService.calculatePoints(game.actPlayer, tile, Direction.RIGHT, Pair(-1,1))
        assertEquals(mutableListOf(22,3,0,3),output)
        //Same scenario, but the placed tile will free sound discs, but not before having to buy caco discs
        tile = presetTiles[13]
        output = rootService.gameService.calculatePoints(game.actPlayer, tile, Direction.DOWN, Pair(-1,1))
        assertEquals(mutableListOf(15,0,2,3),output)
    }
}