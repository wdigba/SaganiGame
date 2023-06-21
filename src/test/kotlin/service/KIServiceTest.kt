package service

import entity.*
import kotlin.test.*

class KIServiceTest {
    lateinit var player: Player
    lateinit var kiService: KIService
    @BeforeTest
    fun initialize(){
        // Given
        val rootService = RootService() // Initialize with appropriate data
        kiService = KIService(rootService, 10)
        // Prepare a player with a specific game state
        player = Player("Alice", Color.WHITE)

        // Create tiles and place them on the board
        val tile1 = Tile(1, points = 6, Element.EARTH, listOf(
            Arrow(Element.FIRE, Direction.UP_LEFT),
            Arrow(Element.EARTH, Direction.UP),
            Arrow(Element.WATER, Direction.UP_RIGHT)))
        //add tile to player.board


        val tile2 = Tile(2, points = 3, Element.EARTH, listOf(
            Arrow(Element.FIRE, Direction.UP_LEFT),
            Arrow(Element.FIRE, Direction.UP)))

        val tile3 = Tile(3, points = 6, Element.FIRE, listOf(
            Arrow(Element.FIRE, Direction.UP),
            Arrow(Element.EARTH, Direction.RIGHT),
            Arrow(Element.EARTH, Direction.DOWN)))

        val board = mutableMapOf(
            Pair(0, 0) to tile1,
            Pair(0, 1) to tile2,
            Pair(-1, 1) to tile3
        )

        repeat(3) {
            tile1.discs.add(player.discs.last())
        }
        repeat(2) {
            tile2.discs.add(player.discs.last())
        }
        repeat(3) {
            tile3.discs.add(player.discs.last())
        }

        // Set up the arrows


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

        player.board = board
    }

    @Test
    fun testCalculateBoardScore() {
        val service = KIService(RootService(), 10)
        val scoreMap = mutableMapOf<Pair<Int, Int>, KIService.CoordinateInformation>()

        // Set up the score map with some sample data
        scoreMap[Pair(0, 0)] = KIService.CoordinateInformation().apply {
            occupied = false
            airCount = 2
            earthCount = 1
            waterCount = 0
            fireCount = 0
            discsIfAirPlaced = 2
            discsIfEarthPlaced = 1
            discsIfWaterPlaced = 0
            discsIfFirePlaced = 0
            gameDistance = 2
        }
        scoreMap[Pair(1, 1)] = KIService.CoordinateInformation().apply {
            occupied = true
        }

        val expectedScore = 4.0  // Calculate the expected score based on the sample data

        val actualScore = service.calculateBoardScore(scoreMap)

        assertEquals(expectedScore, actualScore)
    }
    @Test
    fun testCalculateBoardScore2 () {
        val service = KIService(RootService(), 10)
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

        val expectedScore = 22.0

        val actualScore = service.calculateBoardScore(scoreMap)

        assertEquals(expectedScore, actualScore)
    }

    @Test
    fun testCalculatePotentialTilePlacements() {
        val service = KIService(RootService(), 10)
        val arrows = listOf(
            Arrow(Element.WATER, Direction.UP),
            Arrow(Element.AIR, Direction.RIGHT),
            Arrow(Element.FIRE, Direction.LEFT),
            Arrow(Element.EARTH, Direction.DOWN)
        )
        val tile = Tile(1, 10, Element.AIR, arrows) //existing tile for which we will determine possible positions
        val scoreMap = mutableMapOf<Pair<Int, Int>, KIService.CoordinateInformation>()

        // position of definite tile
        scoreMap[Pair(1, 0)] = KIService.CoordinateInformation().apply {
            occupied = true
            fireCount = 4
            discsIfFirePlaced = 4
            gameDistance = 3
        }

        // Set up the score map with some sample data, where every next position is free
        scoreMap[Pair(0, 0)] = KIService.CoordinateInformation().apply {
            occupied = false
            gameDistance = 0
        }
        scoreMap[Pair(0, 1)] = KIService.CoordinateInformation().apply {
            occupied = false
            gameDistance = 0
        }
        scoreMap[Pair(1, 1)] = KIService.CoordinateInformation().apply {
            occupied = false
            gameDistance = 0
        }
        scoreMap[Pair(2,0)] = KIService.CoordinateInformation().apply {
            occupied = false
            gameDistance = 0
        }
        scoreMap[Pair(2,1)] = KIService.CoordinateInformation().apply {
            occupied = false
            gameDistance = 0
        }

        val expectedPlacements = mapOf(
            Pair(0, 0) to 2.0,
            Pair(0, 1) to 3.0,
            Pair(1, 1) to 2.0,
            Pair(2, 0) to 2.0,
            Pair(2, 1) to 3.0
        )  // Expected placements and their scores based on the sample data

        val player = Player("test", Color.BLACK)

        val actualPlacements = service.calculatePotentialTilePlacements(tile, scoreMap, player )

        //assertEquals(expectedPlacements, actualPlacements)
    }

    @Test
    fun testBuildScoreMap() {

        val scoreMap = kiService.buildScoreMap(player.board)

        print(scoreMap)
    }

    @Test
    fun testPlaceTile(){
        val scoreMap = kiService.buildScoreMap(player.board)


        val tile = Tile(4, points = 3, Element.WATER, listOf(
            Arrow(Element.EARTH, Direction.UP),
            Arrow(Element.EARTH, Direction.UP_LEFT)))

        repeat(2) {
            tile.discs.add(player.discs.last())
        }

        val potentialPlacements = kiService.calculatePotentialTilePlacements(tile, scoreMap, player)

        // check if the score on (1,1) is the highest compared to all others
        val highestScore = potentialPlacements.maxByOrNull { it.value }
        val highestScores = potentialPlacements.toList().sortedByDescending { (_, value) -> value }.take(10)

        assertEquals(Pair(Pair(1, 1), Direction.LEFT), highestScore?.key)

    }
    @Test
    fun placeTileBiggerScoreMap() {
        val rootService = RootService()
        kiService = KIService(rootService, 10)
        player = Player("Alice", Color.WHITE)

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

        val board = mutableMapOf(
            Pair(0, 0) to tile1,
            Pair(1, 0) to tile2,
            Pair(2, 0) to tile3,
            Pair(0, 1) to tile4,
            Pair(1, 1) to tile5,
            Pair(0, 2) to tile6
        )

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

        tile5.arrows.forEach(){
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
        player.board = board

        val scoreMap = kiService.buildScoreMap(player.board)
        print(scoreMap)

        val tile = Tile(7, points = 6, Element.AIR, listOf(
            Arrow(Element.EARTH, Direction.UP_RIGHT),
            Arrow(Element.FIRE, Direction.UP),
            Arrow(Element.WATER, Direction.UP_LEFT)))

        repeat(2) {
            tile.discs.add(player.discs.last())
        }

        // save timestamp
        val start = System.currentTimeMillis()

        val potentialPlacements = kiService.calculatePotentialTilePlacements(tile, scoreMap, player)

        // get timestamp after calculation
        val end = System.currentTimeMillis()
        val differneceInSeconds = (end - start) / 1000.0

        // get a list of top 10 placements
        val highestScores = potentialPlacements.toList().sortedByDescending { (_, value) -> value }.take(10)

        val highestScore = potentialPlacements.maxByOrNull { it.value }
        assertEquals(Pair(Pair(-1, 0), Direction.DOWN), highestScore?.key)
    }
}