package service

import entity.*
import kotlin.test.*

/**
 * [KIServiceTest] tests main methods of [KIService]
 */
class KIServiceTest {
    private lateinit var player: Player
    private lateinit var kiService: KIService
    lateinit var rootService: RootService

    /**
     * [initialize] creates initial data before testing other functions
     */
    @BeforeTest
    fun initialize(){
        rootService = RootService()
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
        player.board[Pair(0, 0)] = tile1
        player.board[Pair(0, 1)] = tile2
        player.board[Pair(-1, 1)] = tile3

        repeat(24) {
            player.discs.add(Disc.SOUND)
        }

        //adding discs to every tile
        repeat(3) {
            tile1.discs.add(popLastElement(player.discs))
        }
        repeat(2) {
            tile2.discs.add(popLastElement(player.discs))
        }
        repeat(3) {
            tile3.discs.add(popLastElement(player.discs))
        }

        // placing discs on the arrows when needed
        tile1.arrows.forEach {
            when (it.element) {
                Element.FIRE -> {
                    it.disc.add(popLastElement(tile1.discs))
                }
                Element.EARTH -> {
                    it.disc.add(popLastElement(tile1.discs))
                }
                else -> {}
            }
        }

        tile3.arrows.forEach {
            if (it.element == Element.EARTH && it.direction == Direction.RIGHT) {
                it.disc.add(popLastElement(tile3.discs))
            }
        }
    }

    /**
     * [popLastElement] takes the last element from list of discs
     * @return last disc from list of discs
     */
    private fun popLastElement(list: MutableList<Disc>) : Disc {
        return list.removeAt(list.size-1)
    }

    /**
     * test for [KIService.calculateBoardScore]
     */
//    @Test
//    fun testCalculateBoardScore() {
//        val service = KIService(RootService())
//        val scoreMap = mutableMapOf<Pair<Int, Int>, KIService.CoordinateInformation>()
//        // set up the score map with sample data
//        scoreMap[Pair(0, 0)] = KIService.CoordinateInformation().apply {
//            occupied = false
//            airCount = 2
//            earthCount = 2
//            waterCount = 0
//            fireCount = 0
//            discsIfAirPlaced = 3
//            discsIfEarthPlaced = 4
//            discsIfWaterPlaced = 0
//            discsIfFirePlaced = 0
//            gameDistance = 2
//        }
//        val expectedScore = 0.875  // calculated expected score
//        val actualScore = service.calculateBoardScore(scoreMap) //the score calculated by function
//        assertEquals(expectedScore, actualScore)
//    }
//
//    /**
//     * second test for [KIService.calculateBoardScore]
//     */
//    @Test
//    fun testCalculateBoardScore2 () {
//        val service = KIService(RootService())
//        val scoreMap = mutableMapOf(
//            Pair(0, 0) to KIService.CoordinateInformation().apply {
//                airCount = 2
//                earthCount = 1
//                waterCount = 0
//                fireCount = 1
//                discsIfAirPlaced = 2
//                discsIfEarthPlaced = 1
//                discsIfWaterPlaced = 0
//                discsIfFirePlaced = 1
//                gameDistance = 0
//                occupied = false
//            },
//            Pair(0, 1) to KIService.CoordinateInformation().apply {
//                airCount = 1
//                earthCount = 0
//                waterCount = 1
//                fireCount = 0
//                discsIfAirPlaced = 1
//                discsIfEarthPlaced = 0
//                discsIfWaterPlaced = 1
//                discsIfFirePlaced = 0
//                gameDistance = 0
//                occupied = false
//            }
//        )
//
//        val expectedScore = 0.5 // calculated expected score
//        val actualScore = service.calculateBoardScore(scoreMap) //the score calculated by function
//        assertEquals(expectedScore, actualScore)
//    }
//
//    /**
//     * test for [KIService.buildScoreMap]
//     */
//    @Test
//    fun testBuildScoreMap() {
//        val scoreMap = kiService.buildScoreMap(player.board)
//        print(scoreMap)
//    }
//
//    /**
//     * test for [KIService.calculatePotentialTilePlacements] for board with 3 placed tiles
//     */
//    @Test
//    fun testPlaceTile() {
//        val scoreMap = kiService.buildScoreMap(player.board)
//        // the tile we want to place
//        val tile = Tile(4, points = 3, Element.WATER, listOf(
//            Arrow(Element.EARTH, Direction.UP),
//            Arrow(Element.EARTH, Direction.UP_LEFT)))
//        // filling it with discs
//        repeat(2) {
//            tile.discs.add(popLastElement(player.discs))
//        }
//        // calculate potential placements for this tile
//        val potentialPlacements = kiService.calculatePotentialTilePlacements(tile, scoreMap, player)
//
//        // check if the score on (1,1) is the highest compared to all others
//        val highestScore = potentialPlacements.maxByOrNull { it.score }
//        //val highestScores = potentialPlacements.toList().sortedByDescending { it.score}.take(10)
//
//        assertEquals(Pair(1, 1), highestScore?.location)
//        assertEquals(Direction.LEFT, highestScore?.direction)
//    }
//
//    /**
//     * test for [KIService.playBestMove] calculates best move based on board
//     */
//    @Test
//    fun playBestMoveTest() {
//        val rootService = RootService()
//
//        val john = Triple("JohnAI", Color.BLACK, PlayerType.BEST_AI)
//        val jo = Triple("JoAI", Color.WHITE, PlayerType.BEST_AI)
//        val players = mutableListOf(john, jo)
//
//        rootService.gameService.startNewGame(players)
//        var game = rootService.currentGame
//        assertNotNull(game)
//
//        repeat(24) {
//            game!!.players[0].discs.add(Disc.SOUND)
//        }
//
//        // placing the very first tile
//
//        game.offerDisplay.clear()
//
//        // tiles to place
//        val tile1 = Tile(1, points = 3, Element.WATER, listOf(
//            Arrow(Element.EARTH, Direction.UP),
//            Arrow(Element.AIR, Direction.LEFT)))
//        game.offerDisplay.add(tile1)
//
//        val tile2 = Tile(2, points = 3, Element.FIRE, listOf(
//            Arrow(Element.FIRE, Direction.DOWN),
//            Arrow(Element.WATER, Direction.UP)))
//        game.offerDisplay.add(tile2)
//
//        //tile with the biggest amount of arrows should be placed
//        assertEquals(game.actPlayer.board, emptyMap())
//        println("expected empty : " + game.actPlayer.board)
//        rootService.kIService.playBestMove()
//
//        assertNotSame(game, rootService.currentGame)
//
//        rootService.gameService.changeToNextPlayer()
//        game = rootService.currentGame!!
//        println("expected tile1 : " + game.actPlayer.board)
//
//        println("expected random tile : " + game.stacks[0])
//        // looking for best tile when only one left in offer display
//        rootService.kIService.playBestMove()
//        rootService.gameService.changeToNextPlayer()
//        game = rootService.currentGame!!
//        println("expected tile1 + tile2/stacks[0] : " + game.actPlayer.board)
//
//        game.offerDisplay.clear()
//        // new tiles for offer display
//        val tile3 = Tile(3, points = 3, Element.AIR, listOf(
//            Arrow(Element.FIRE, Direction.DOWN_LEFT),
//            Arrow(Element.EARTH, Direction.UP_RIGHT)))
//        game.offerDisplay.add(tile3)
//
//        val tile4 = Tile(4, points = 3, Element.FIRE, listOf(
//            Arrow(Element.AIR, Direction.LEFT),
//            Arrow(Element.WATER, Direction.UP)))
//        game.offerDisplay.add(tile4)
//
//        val tile5 = Tile(5, points = 6, Element.EARTH, listOf(
//            Arrow(Element.WATER, Direction.UP),
//            Arrow(Element.FIRE, Direction.UP_LEFT),
//            Arrow(Element.FIRE, Direction.DOWN)))
//        game.offerDisplay.add(tile5)
//
//        println("expected tile3, tile4, tile5 : " + game.offerDisplay)
//
//        println("expected tile1 + tile2/stacks[0] : " + game.actPlayer.board)
//        assert(game.actPlayer.name == "JohnAI")
//        rootService.kIService.playBestMove()
//        game = rootService.currentGame!!
//        assert(game.actPlayer.name == "JoAI")
//        rootService.gameService.changeToNextPlayer()
//        game = rootService.currentGame!!
//        println("expected tile1 + tile2/stacks[0] + tile3/tile4/tile5 : " + game.actPlayer.board)
//        assert(game.actPlayer.board.isNotEmpty())
//
//        // intermezzo phase
//
//        val tile6 = Tile(6, points = 1, Element.WATER, listOf(
//            Arrow(Element.WATER, Direction.UP)))
//        val tile7 = Tile(7, points = 10, Element.AIR, listOf(
//            Arrow(Element.FIRE, Direction.DOWN_RIGHT),
//            Arrow(Element.EARTH, Direction.UP_LEFT),
//            Arrow(Element.FIRE, Direction.UP),
//            Arrow(Element.AIR, Direction.LEFT)
//        ))
//
//        game.intermezzoStorage.clear()
//        println("expected empty intermezzo storage : " + game.intermezzoStorage)
//        game.intermezzoPlayers.addAll(game.players)
//        game.intermezzoStorage.add(tile6)
//        game.intermezzoStorage.add(tile7)
//        println("expected filled intermezzo storage : " + game.intermezzoStorage)
//        game.intermezzo = true
//
//        assert(game.actPlayer.name == "JohnAI")
//        println("expected filled board : " + game.actPlayer.board)
//        rootService.kIService.playBestMove()
//        game = rootService.currentGame!!
//        game.intermezzoPlayers.clear()
//        game.intermezzo = false
//        game = rootService.currentGame!!
//        assert(game.actPlayer.name == "JohnAI")
//        println("expected previous board + tile6/tile7 : " + game.actPlayer.board)
//    }
//
//    /**
//     * test for [KIService.calculatePotentialTilePlacements] for board with 11 placed tiles
//     */
//    @Test
//    fun placeTileBiggerScoreMap() {
//        val rootService = RootService()
//        kiService = KIService(rootService)
//        player = Player("Alice", Color.WHITE)
//
//        repeat(24) {
//            player.discs.add(Disc.SOUND)
//        }
//
//
//        // tiles for player´s board
//        val tile1 = Tile(1, points = 3, Element.WATER, listOf(
//            Arrow(Element.FIRE, Direction.RIGHT),
//            Arrow(Element.WATER, Direction.DOWN)))
//
//        val tile2 = Tile(2, points = 3, Element.FIRE, listOf(
//            Arrow(Element.WATER, Direction.DOWN_LEFT),
//            Arrow(Element.AIR, Direction.UP)))
//
//        val tile3 = Tile(3, points = 3, Element.EARTH, listOf(
//            Arrow(Element.AIR, Direction.UP_LEFT),
//            Arrow(Element.EARTH, Direction.UP_RIGHT)))
//
//        val tile4 = Tile(4, points = 6, Element.EARTH, listOf(
//            Arrow(Element.FIRE, Direction.DOWN_RIGHT),
//            Arrow(Element.WATER, Direction.LEFT),
//            Arrow(Element.EARTH, Direction.UP)))
//
//        val tile5 = Tile(5, points = 3, Element.AIR, listOf(
//            Arrow(Element.EARTH, Direction.LEFT),
//            Arrow(Element.FIRE, Direction.DOWN)))
//
//        val tile6 = Tile(6, points = 1, Element.FIRE, listOf(
//            Arrow(Element.WATER, Direction.DOWN)))
//
//        val tile7 = Tile(7, points = 3, Element.FIRE, listOf(
//            Arrow(Element.FIRE, Direction.UP_RIGHT),
//            Arrow(Element.EARTH, Direction.DOWN)))
//
//        val tile8 = Tile(8, points = 1, Element.EARTH, listOf(
//            Arrow(Element.EARTH, Direction.UP_RIGHT)))
//
//        val tile9 = Tile(9, points = 10, Element.AIR, listOf(
//            Arrow(Element.FIRE, Direction.UP),
//            Arrow(Element.WATER, Direction.UP_LEFT),
//            Arrow(Element.EARTH, Direction.UP_RIGHT),
//            Arrow(Element.AIR, Direction.RIGHT)))
//
//        val tile10 = Tile(10, points = 1, Element.WATER, listOf(
//            Arrow(Element.FIRE, Direction.UP)))
//
//        val tile11 = Tile(11, points = 3, Element.FIRE, listOf(
//            Arrow(Element.AIR, Direction.UP_LEFT),
//            Arrow(Element.AIR, Direction.DOWN_RIGHT)))
//        // filling the board with those tiles
//
//        player.board[Pair(0, 0)] = tile1
//        player.board[Pair(1, 0)] = tile2
//        player.board[Pair(2, 0)] = tile3
//        player.board[Pair(0, 1)] = tile4
//        player.board[Pair(1, 1)] = tile5
//        player.board[Pair(0, 2)] = tile6
//        player.board[Pair(-1, -1)] = tile7
//        player.board[Pair(-1, 0)] = tile8
//        player.board[Pair(1, -1)] = tile9
//        player.board[Pair(1, -2)] = tile10
//        player.board[Pair(2, -2)] = tile11
//
//
//        // filling tiles with discs
//        repeat(2) {
//            tile1.discs.add(popLastElement(player.discs))
//        }
//        repeat(2) {
//            tile2.discs.add(popLastElement(player.discs))
//        }
//        repeat(2) {
//            tile3.discs.add(popLastElement(player.discs))
//        }
//        repeat(3) {
//            tile4.discs.add(popLastElement(player.discs))
//        }
//        repeat(2) {
//            tile5.discs.add(popLastElement(player.discs))
//        }
//        repeat(1) {
//            tile6.discs.add(popLastElement(player.discs))
//        }
//        repeat(2) {
//            tile7.discs.add(popLastElement(player.discs))
//        }
//        repeat(1) {
//            tile8.discs.add(popLastElement(player.discs))
//        }
//        repeat(4) {
//            tile9.discs.add(popLastElement(player.discs))
//        }
//        repeat(1) {
//            tile10.discs.add(popLastElement(player.discs))
//        }
//        repeat(2) {
//            tile11.discs.add(popLastElement(player.discs))
//        }
//        // filling available arrows with discs
//        tile1.arrows.forEach {
//            when (it.element) {
//                Element.FIRE -> {
//                    it.disc.add(popLastElement(tile1.discs))
//                }
//                else -> {}
//            }
//        }
//
//        tile2.arrows.forEach{
//            when (it.element) {
//                Element.AIR -> it.disc.add(popLastElement(tile2.discs))
//                else -> {}
//            }
//        }
//
//        tile3.arrows.forEach {
//            if (it.element == Element.AIR && it.direction == Direction.UP_LEFT) {
//                it.disc.add(popLastElement(tile3.discs))
//            }
//        }
//
//        tile4.arrows.forEach {
//            if (it.element == Element.FIRE && it.direction == Direction.DOWN_RIGHT) {
//                it.disc.add(popLastElement(tile4.discs))
//            }
//        }
//
//        tile5.arrows.forEach{
//            when (it.element) {
//                Element.FIRE -> {
//                    it.disc.add(popLastElement(tile5.discs))
//                }
//                Element.EARTH -> it.disc.add(popLastElement(tile5.discs))
//                else -> {}
//            }
//        }
//
//        tile6.arrows.forEach {
//            if (it.element == Element.WATER && it.direction == Direction.DOWN) {
//                it.disc.add(popLastElement(tile6.discs))
//            }
//        }
//
//        tile7.arrows.forEach{
//            when (it.element) {
//                Element.FIRE -> {
//                    it.disc.add(popLastElement(tile7.discs))
//                }
//                Element.EARTH -> it.disc.add(popLastElement(tile7.discs))
//                else -> {}
//            }
//        }
//
//        tile8.arrows.forEach {
//            if (it.element == Element.EARTH && it.direction == Direction.UP_RIGHT) {
//                it.disc.add(popLastElement(tile8.discs))
//            }
//        }
//
//        tile9.arrows.forEach{
//            when (it.element) {
//                Element.FIRE -> it.disc.add(popLastElement(tile9.discs))
//                Element.EARTH -> it.disc.add(popLastElement(tile9.discs))
//                Element.WATER -> it.disc.add(popLastElement(tile9.discs))
//                else -> {}
//            }
//        }
//
//        tile10.arrows.forEach {
//            if (it.element == Element.FIRE && it.direction == Direction.UP) {
//                it.disc.add(popLastElement(tile10.discs))
//            }
//        }
//
//        tile11.arrows.forEach {
//            if (it.element == Element.AIR && it.direction == Direction.UP_LEFT) {
//                it.disc.add(popLastElement(tile11.discs))
//            }
//            if (it.element == Element.AIR && it.direction == Direction.DOWN_RIGHT) {
//                it.disc.add(popLastElement(tile11.discs))
//            }
//        }
//
//        // building the score map for player
//        val scoreMap = kiService.buildScoreMap(player.board)
//        print(scoreMap)
//        // tile we want to place
//        val tile12 = Tile(12, points = 1, Element.WATER, listOf(
//            Arrow(Element.AIR, Direction.LEFT)))
//        // discs for this tile
//        repeat(1) {
//            tile12.discs.add(popLastElement(player.discs))
//        }
//
//        // save timestamp
//        //val start = System.currentTimeMillis()
//        // calculating all possible placements for that tile
//        val potentialPlacements = kiService.calculatePotentialTilePlacements(tile12, scoreMap, player)
//
//        // get timestamp after calculation
//        //val end = System.currentTimeMillis()
//        //val differenceInSeconds = (end - start) / 1000.0
//
//        // get a list of top 10 placements
//        //val highestScoresTop = potentialPlacements.toList().sortedByDescending { it.score }.take(10)
//        // the best position for the tile
//        val highestScore = potentialPlacements.maxByOrNull { it.score }
//
//        potentialPlacements.forEach {
//            assert(this.rootService.playerActionService.validLocations(player.board).contains(it.location))
//        }
//
//        assertEquals(Pair(0, -1), highestScore?.location)
//        assertEquals(Direction.DOWN, highestScore?.direction)
//    }

}