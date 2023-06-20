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
        kiService = KIService(rootService)
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


        tile1.arrows.forEach({
            when (it.element) {
                Element.FIRE -> {
                    it.disc.add(tile1.discs.last())
                }
                Element.EARTH -> {
                    it.disc.add(tile1.discs.last())
                } else -> {}
            }
        })

        tile3.arrows.forEach({
            if (it.element == Element.EARTH && it.direction == Direction.RIGHT) {
                it.disc.add(tile3.discs.last())
            }
        })

        player.board = board
    }

    @Test
    fun testCalculateBoardScore() {
        val service = KIService(RootService())
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

        val expectedScore = 22.0

        val actualScore = service.calculateBoardScore(scoreMap)

        assertEquals(expectedScore, actualScore)
    }

    @Test
    fun testCalculatePotentialTilePlacements() {
        val service = KIService(RootService())
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

        val scoreMap = kiService.buildScoreMap(player, 1)

        print(scoreMap)
    }

    @Test
    fun testPlaceTile(){
        val scoreMap = kiService.buildScoreMap(player, 1)


        val tile = Tile(4, points = 3, Element.WATER, listOf(
            Arrow(Element.EARTH, Direction.UP),
            Arrow(Element.EARTH, Direction.UP_LEFT)))

        repeat(3) {
            tile.discs.add(player.discs.last())
        }

        val potentialPlacements = kiService.calculatePotentialTilePlacements(tile, scoreMap, player)

        // check if the score on (1,1) is the highest compared to all others
        val highestScore = potentialPlacements.maxByOrNull { it.value }

        assertEquals(Pair(Pair(1, 1), Direction.LEFT), highestScore?.key)

    }
}