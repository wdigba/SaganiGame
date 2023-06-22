package service

import entity.*
import kotlin.test.*

class KIServiceTest {
    lateinit var player: Player
    lateinit var kiService: KIService

    /**
     * [initialize] creates initial data before testing other functions
     */
    @BeforeTest
    fun initialize(){
        val rootService = RootService()
        kiService = KIService(rootService)
        player = Player("Alice", Color.WHITE)

        // creating tiles
        val tile1 = Tile(1, points = 6, Element.EARTH, listOf(
            Arrow(Element.FIRE, Direction.UP_LEFT),
            Arrow(Element.EARTH, Direction.UP),
            Arrow(Element.WATER, Direction.UP_RIGHT)))

        val tile2 = Tile(2, points = 3, Element.EARTH, listOf(
            Arrow(Element.FIRE, Direction.UP_LEFT),
            Arrow(Element.FIRE, Direction.UP)))

        val tile3 = Tile(3, points = 6, Element.FIRE, listOf(
            Arrow(Element.FIRE, Direction.UP),
            Arrow(Element.EARTH, Direction.RIGHT),
            Arrow(Element.EARTH, Direction.DOWN)))

        //placing tiles on the board
        val board = mutableMapOf(
            Pair(0, 0) to tile1,
            Pair(0, 1) to tile2,
            Pair(-1, 1) to tile3
        )

        //adding discs to every tile
        repeat(3) {
            tile1.discs.add(player.discs.last())
        }
        repeat(2) {
            tile2.discs.add(player.discs.last())
        }
        repeat(3) {
            tile3.discs.add(player.discs.last())
        }

        // placing discs on the arrows when needed
        tile1.arrows.forEach {
            when (it.element) {
                Element.FIRE -> {
                    it.disc.add(tile1.discs.last())
                }
                Element.EARTH -> {
                    it.disc.add(tile1.discs.last())
                }
                else -> {}
            }
        }

        tile3.arrows.forEach {
            if (it.element == Element.EARTH && it.direction == Direction.RIGHT) {
                it.disc.add(tile3.discs.last())
            }
        }
        // fill the board for player
        player.board = board
    }

    /**
     * test for [KIService.calculateBoardScore]
     */
    @Test
    fun testCalculateBoardScore() {
        val service = KIService(RootService())
        val scoreMap = mutableMapOf<Pair<Int, Int>, KIService.CoordinateInformation>()
        // set up the score map with sample data
        scoreMap[Pair(0, 0)] = KIService.CoordinateInformation().apply {
            occupied = false
            airCount = 2
            earthCount = 2
            waterCount = 0
            fireCount = 0
            discsIfAirPlaced = 3
            discsIfEarthPlaced = 4
            discsIfWaterPlaced = 0
            discsIfFirePlaced = 0
            gameDistance = 2
        }
        val expectedScore = 0.875  // calculated expected score
        val actualScore = service.calculateBoardScore(scoreMap) //the score calculated by function
        assertEquals(expectedScore, actualScore)
    }

    /**
     * second test for [KIService.calculateBoardScore]
     */
    @Test
    fun testCalculateBoardScore2 () {
        val service = KIService(RootService())
        val scoreMap = mutableMapOf(
            Pair(0, 0) to KIService.CoordinateInformation().apply {
                airCount = 2
                earthCount = 1
                waterCount = 0
                fireCount = 1
                discsIfAirPlaced = 2
                discsIfEarthPlaced = 1
                discsIfWaterPlaced = 0
                discsIfFirePlaced = 1
                gameDistance = 0
                occupied = false
            },
            Pair(0, 1) to KIService.CoordinateInformation().apply {
                airCount = 1
                earthCount = 0
                waterCount = 1
                fireCount = 0
                discsIfAirPlaced = 1
                discsIfEarthPlaced = 0
                discsIfWaterPlaced = 1
                discsIfFirePlaced = 0
                gameDistance = 0
                occupied = false
            }
        )

        val expectedScore = 0.5 // calculated expected score
        val actualScore = service.calculateBoardScore(scoreMap) //the score calculated by function
        assertEquals(expectedScore, actualScore)
    }

    /**
     * test for [KIService.buildScoreMap]
     */
    @Test
    fun testBuildScoreMap() {
        val scoreMap = kiService.buildScoreMap(player.board)
        print(scoreMap)
    }

    /**
     * test for [KIService.calculatePotentialTilePlacements] for board with 3 placed tiles
     */
    @Test
    fun testPlaceTile() {
        val scoreMap = kiService.buildScoreMap(player.board)
        // the tile we want to place
        val tile = Tile(4, points = 3, Element.WATER, listOf(
            Arrow(Element.EARTH, Direction.UP),
            Arrow(Element.EARTH, Direction.UP_LEFT)))
        // filling it with discs
        repeat(2) {
            tile.discs.add(player.discs.last())
        }
        // calculate potential placements for this tile
        val potentialPlacements = kiService.calculatePotentialTilePlacements(tile, scoreMap, player)

        // check if the score on (1,1) is the highest compared to all others
        val highestScore = potentialPlacements.maxByOrNull { it.value }
        val highestScores = potentialPlacements.toList().sortedByDescending { (_, value) -> value }.take(10)

        assertEquals(Pair(Pair(1, 1), Direction.LEFT), highestScore?.key)

    }
    /**
     * test for [KIService.calculatePotentialTilePlacements] for board with 11 placed tiles
     */
    @Test
    fun placeTileBiggerScoreMap() {
        val rootService = RootService()
        kiService = KIService(rootService)
        player = Player("Alice", Color.WHITE)
        // tiles for player´s board
        val tile1 = Tile(1, points = 3, Element.WATER, listOf(
            Arrow(Element.FIRE, Direction.RIGHT),
            Arrow(Element.WATER, Direction.DOWN)))

        val tile2 = Tile(2, points = 3, Element.FIRE, listOf(
            Arrow(Element.WATER, Direction.DOWN_LEFT),
            Arrow(Element.AIR, Direction.UP)))

        val tile3 = Tile(3, points = 3, Element.EARTH, listOf(
            Arrow(Element.AIR, Direction.UP_LEFT),
            Arrow(Element.EARTH, Direction.UP_RIGHT)))

        val tile4 = Tile(4, points = 6, Element.EARTH, listOf(
            Arrow(Element.FIRE, Direction.DOWN_RIGHT),
            Arrow(Element.WATER, Direction.LEFT),
            Arrow(Element.EARTH, Direction.UP)))

        val tile5 = Tile(5, points = 3, Element.AIR, listOf(
            Arrow(Element.EARTH, Direction.LEFT),
            Arrow(Element.FIRE, Direction.DOWN)))

        val tile6 = Tile(6, points = 1, Element.FIRE, listOf(
            Arrow(Element.WATER, Direction.DOWN)))

        val tile7 = Tile(7, points = 3, Element.FIRE, listOf(
            Arrow(Element.FIRE, Direction.UP_RIGHT),
            Arrow(Element.EARTH, Direction.DOWN)))

        val tile8 = Tile(8, points = 1, Element.EARTH, listOf(
            Arrow(Element.EARTH, Direction.UP_RIGHT)))

        val tile9 = Tile(9, points = 10, Element.AIR, listOf(
            Arrow(Element.FIRE, Direction.UP),
            Arrow(Element.WATER, Direction.UP_LEFT),
            Arrow(Element.EARTH, Direction.UP_RIGHT),
            Arrow(Element.AIR, Direction.RIGHT)))

        val tile10 = Tile(10, points = 1, Element.WATER, listOf(
            Arrow(Element.FIRE, Direction.UP)))

        val tile11 = Tile(11, points = 3, Element.FIRE, listOf(
            Arrow(Element.AIR, Direction.UP_LEFT),
            Arrow(Element.AIR, Direction.DOWN_RIGHT)))
        // filling the board with those tiles
        val board = mutableMapOf(
            Pair(0, 0) to tile1,
            Pair(1, 0) to tile2,
            Pair(2, 0) to tile3,
            Pair(0, 1) to tile4,
            Pair(1, 1) to tile5,
            Pair(0, 2) to tile6,
            Pair(-1, -1) to tile7,
            Pair(-1, 0) to tile8,
            Pair(1, -1) to tile9,
            Pair(1, -2) to tile10,
            Pair(2, -2) to tile11
        )
        // filling tiles with discs
        repeat(2) {
            tile1.discs.add(player.discs.last())
        }
        repeat(2) {
            tile2.discs.add(player.discs.last())
        }
        repeat(2) {
            tile3.discs.add(player.discs.last())
        }
        repeat(3) {
            tile4.discs.add(player.discs.last())
        }
        repeat(2) {
            tile5.discs.add(player.discs.last())
        }
        repeat(1) {
            tile6.discs.add(player.discs.last())
        }
        repeat(2) {
            tile7.discs.add(player.discs.last())
        }
        repeat(1) {
            tile8.discs.add(player.discs.last())
        }
        repeat(4) {
            tile9.discs.add(player.discs.last())
        }
        repeat(1) {
            tile10.discs.add(player.discs.last())
        }
        repeat(2) {
            tile11.discs.add(player.discs.last())
        }
        // filling available arrows with discs
        tile1.arrows.forEach {
            when (it.element) {
                Element.FIRE -> {
                    it.disc.add(tile1.discs.last())
                }
                else -> {}
            }
        }

        tile2.arrows.forEach{
            when (it.element) {
                Element.AIR -> it.disc.add(tile2.discs.last())
                else -> {}
            }
        }

        tile3.arrows.forEach {
            if (it.element == Element.AIR && it.direction == Direction.UP_LEFT) {
                it.disc.add(tile3.discs.last())
            }
        }

        tile4.arrows.forEach {
            if (it.element == Element.FIRE && it.direction == Direction.DOWN_RIGHT) {
                it.disc.add(tile4.discs.last())
            }
        }

        tile5.arrows.forEach{
            when (it.element) {
                Element.FIRE -> {
                    it.disc.add(tile5.discs.last())
                }
                Element.EARTH -> it.disc.add(tile5.discs.last())
                else -> {}
            }
        }

        tile6.arrows.forEach {
            if (it.element == Element.WATER && it.direction == Direction.DOWN) {
                it.disc.add(tile6.discs.last())
            }
        }

        tile7.arrows.forEach{
            when (it.element) {
                Element.FIRE -> {
                    it.disc.add(tile7.discs.last())
                }
                Element.EARTH -> it.disc.add(tile7.discs.last())
                else -> {}
            }
        }

        tile8.arrows.forEach {
            if (it.element == Element.EARTH && it.direction == Direction.UP_RIGHT) {
                it.disc.add(tile8.discs.last())
            }
        }

        tile9.arrows.forEach{
            when (it.element) {
                Element.FIRE -> it.disc.add(tile9.discs.last())
                Element.EARTH -> it.disc.add(tile9.discs.last())
                Element.WATER -> it.disc.add(tile9.discs.last())
                else -> {}
            }
        }

        tile10.arrows.forEach {
            if (it.element == Element.FIRE && it.direction == Direction.UP) {
                it.disc.add(tile10.discs.last())
            }
        }

        tile11.arrows.forEach {
            if (it.element == Element.AIR && it.direction == Direction.UP_LEFT) {
                it.disc.add(tile11.discs.last())
            }
            if (it.element == Element.AIR && it.direction == Direction.DOWN_RIGHT) {
                it.disc.add(tile11.discs.last())
            }
        }

        player.board = board
        // building the score map for player
        val scoreMap = kiService.buildScoreMap(player.board)
        print(scoreMap)
        // tile we want to place
        val tile12 = Tile(12, points = 1, Element.WATER, listOf(
            Arrow(Element.AIR, Direction.LEFT)))
        // discs for this tile
        repeat(2) {
            tile12.discs.add(player.discs.last())
        }

        // save timestamp
        val start = System.currentTimeMillis()
        // calculating all possible placements for that tile
        val potentialPlacements = kiService.calculatePotentialTilePlacements(tile12, scoreMap, player)

        // get timestamp after calculation
        val end = System.currentTimeMillis()
        val differenceInSeconds = (end - start) / 1000.0

        // get a list of top 10 placements
        val highestScoresTop = potentialPlacements.toList().sortedByDescending { (_, value) -> value }.take(10)
        // the best position for the tile
        val highestScore = potentialPlacements.maxByOrNull { it.value }

        assertEquals(Pair(Pair(2, -1), Direction.UP), highestScore?.key)
    }
}