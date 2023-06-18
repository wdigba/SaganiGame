package service

import entity.*
import kotlin.test.*

class KIServiceTest {
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

        val actualPlacements = service.calculatePotentialTilePlacements(tile, scoreMap)

        assertEquals(expectedPlacements, actualPlacements)
    }

}