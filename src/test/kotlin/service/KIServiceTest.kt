package service

import entity.*
import kotlin.random.Random
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
    fun initialize() {
        rootService = RootService()
        kiService = KIService(rootService)
        player = Player("Alice", Color.WHITE)

        // creating tiles
        val tile1 = Tile(
            1, points = 6, Element.EARTH, listOf(
                Arrow(Element.FIRE, Direction.UP_LEFT),
                Arrow(Element.EARTH, Direction.UP),
                Arrow(Element.WATER, Direction.UP_RIGHT)
            )
        )

        val tile2 = Tile(
            2, points = 3, Element.EARTH, listOf(
                Arrow(Element.FIRE, Direction.UP_LEFT),
                Arrow(Element.FIRE, Direction.UP)
            )
        )

        val tile3 = Tile(
            3, points = 6, Element.FIRE, listOf(
                Arrow(Element.FIRE, Direction.UP),
                Arrow(Element.EARTH, Direction.RIGHT),
                Arrow(Element.EARTH, Direction.DOWN)
            )
        )

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
    private fun popLastElement(list: MutableList<Disc>): Disc {
        return list.removeAt(list.size - 1)
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
    fun testCalculateBoardScore2() {
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
        val tile = Tile(
            4, points = 3, Element.WATER, listOf(
                Arrow(Element.EARTH, Direction.UP),
                Arrow(Element.EARTH, Direction.UP_LEFT)
            )
        )
        // filling it with discs
        repeat(2) {
            tile.discs.add(popLastElement(player.discs))
        }
        // calculate potential placements for this tile
        val potentialPlacements = kiService.calculatePotentialTilePlacements(tile, scoreMap, player)

        // check if the score on (1,1) is the highest compared to all others
        val highestScore = potentialPlacements.maxByOrNull { it.score }
        //val highestScores = potentialPlacements.toList().sortedByDescending { it.score}.take(10)

        assertEquals(Pair(1, 1), highestScore?.location)
        assertEquals(Direction.LEFT, highestScore?.direction)
    }

    /**
     * test for [KIService.playBestMove] calculates best move based on board
     */
    @Test
    fun playBestMoveTest() {
        val rootService = RootService()

        val john = Triple("JohnAI", Color.BLACK, PlayerType.BEST_AI)
        val jo = Triple("JoAI", Color.WHITE, PlayerType.BEST_AI)
        val players = mutableListOf(john, jo)

        rootService.gameService.startNewGame(players)
        var game = rootService.currentGame
        assertNotNull(game)

        repeat(24) {
            game!!.players[0].discs.add(Disc.SOUND)
        }

        // placing the very first tile

        game.offerDisplay.clear()

        // tiles to place
        val tile1 = Tile(
            1, points = 3, Element.WATER, listOf(
                Arrow(Element.EARTH, Direction.UP),
                Arrow(Element.AIR, Direction.LEFT)
            )
        )
        game.offerDisplay.add(tile1)

        val tile2 = Tile(
            2, points = 3, Element.FIRE, listOf(
                Arrow(Element.FIRE, Direction.DOWN),
                Arrow(Element.WATER, Direction.UP)
            )
        )
        game.offerDisplay.add(tile2)

        //tile with the biggest amount of arrows should be placed
        assertEquals(game.actPlayer.board, emptyMap())
        println("expected empty : " + game.actPlayer.board)
        rootService.kIService.playBestMove()

        assertNotSame(game, rootService.currentGame)

        rootService.gameService.changeToNextPlayer()
        game = rootService.currentGame!!
        println("expected tile1 : " + game.actPlayer.board)

        println("expected random tile : " + game.stacks[0])
        // looking for best tile when only one left in offer display
        rootService.kIService.playBestMove()
        rootService.gameService.changeToNextPlayer()
        game = rootService.currentGame!!
        println("expected tile1 + tile2/stacks[0] : " + game.actPlayer.board)

        game.offerDisplay.clear()
        // new tiles for offer display
        val tile3 = Tile(
            3, points = 3, Element.AIR, listOf(
                Arrow(Element.FIRE, Direction.DOWN_LEFT),
                Arrow(Element.EARTH, Direction.UP_RIGHT)
            )
        )
        game.offerDisplay.add(tile3)

        val tile4 = Tile(
            4, points = 3, Element.FIRE, listOf(
                Arrow(Element.AIR, Direction.LEFT),
                Arrow(Element.WATER, Direction.UP)
            )
        )
        game.offerDisplay.add(tile4)

        val tile5 = Tile(
            5, points = 6, Element.EARTH, listOf(
                Arrow(Element.WATER, Direction.UP),
                Arrow(Element.FIRE, Direction.UP_LEFT),
                Arrow(Element.FIRE, Direction.DOWN)
            )
        )
        game.offerDisplay.add(tile5)

        println("expected tile3, tile4, tile5 : " + game.offerDisplay)

        println("expected tile1 + tile2/stacks[0] : " + game.actPlayer.board)
        assert(game.actPlayer.name == "JohnAI")
        rootService.kIService.playBestMove()
        game = rootService.currentGame!!
        assert(game.actPlayer.name == "JoAI")
        rootService.gameService.changeToNextPlayer()
        game = rootService.currentGame!!
        println("expected tile1 + tile2/stacks[0] + tile3/tile4/tile5 : " + game.actPlayer.board)
        assert(game.actPlayer.board.isNotEmpty())

        // intermezzo phase

        val tile6 = Tile(
            6, points = 1, Element.WATER, listOf(
                Arrow(Element.WATER, Direction.UP)
            )
        )
        val tile7 = Tile(
            7, points = 10, Element.AIR, listOf(
                Arrow(Element.FIRE, Direction.DOWN_RIGHT),
                Arrow(Element.EARTH, Direction.UP_LEFT),
                Arrow(Element.FIRE, Direction.UP),
                Arrow(Element.AIR, Direction.LEFT)
            )
        )

        game.intermezzoStorage.clear()
        println("expected empty intermezzo storage : " + game.intermezzoStorage)
        game.intermezzoPlayers.addAll(game.players)
        game.intermezzoStorage.add(tile6)
        game.intermezzoStorage.add(tile7)
        println("expected filled intermezzo storage : " + game.intermezzoStorage)
        game.intermezzo = true

        assert(game.actPlayer.name == "JohnAI")
        println("expected filled board : " + game.actPlayer.board)
        rootService.kIService.playBestMove()
        game = rootService.currentGame!!
        game.intermezzoPlayers.clear()
        game.intermezzo = false
        game = rootService.currentGame!!
        assert(game.actPlayer.name == "JohnAI")
        println("expected previous board + tile6/tile7 : " + game.actPlayer.board)
    }

    /**
     * test for [KIService.calculatePotentialTilePlacements] for board with 11 placed tiles
     */
    @Test
    fun placeTileBiggerScoreMap() {
        val rootService = RootService()
        kiService = KIService(rootService)
        player = Player("Alice", Color.WHITE)

        repeat(24) {
            player.discs.add(Disc.SOUND)
        }


        // tiles for player´s board
        val tile1 = Tile(
            1, points = 3, Element.WATER, listOf(
                Arrow(Element.FIRE, Direction.RIGHT),
                Arrow(Element.WATER, Direction.DOWN)
            )
        )

        val tile2 = Tile(
            2, points = 3, Element.FIRE, listOf(
                Arrow(Element.WATER, Direction.DOWN_LEFT),
                Arrow(Element.AIR, Direction.UP)
            )
        )

        val tile3 = Tile(
            3, points = 3, Element.EARTH, listOf(
                Arrow(Element.AIR, Direction.UP_LEFT),
                Arrow(Element.EARTH, Direction.UP_RIGHT)
            )
        )

        val tile4 = Tile(
            4, points = 6, Element.EARTH, listOf(
                Arrow(Element.FIRE, Direction.DOWN_RIGHT),
                Arrow(Element.WATER, Direction.LEFT),
                Arrow(Element.EARTH, Direction.UP)
            )
        )

        val tile5 = Tile(
            5, points = 3, Element.AIR, listOf(
                Arrow(Element.EARTH, Direction.LEFT),
                Arrow(Element.FIRE, Direction.DOWN)
            )
        )

        val tile6 = Tile(
            6, points = 1, Element.FIRE, listOf(
                Arrow(Element.WATER, Direction.DOWN)
            )
        )

        val tile7 = Tile(
            7, points = 3, Element.FIRE, listOf(
                Arrow(Element.FIRE, Direction.UP_RIGHT),
                Arrow(Element.EARTH, Direction.DOWN)
            )
        )

        val tile8 = Tile(
            8, points = 1, Element.EARTH, listOf(
                Arrow(Element.EARTH, Direction.UP_RIGHT)
            )
        )

        val tile9 = Tile(
            9, points = 10, Element.AIR, listOf(
                Arrow(Element.FIRE, Direction.UP),
                Arrow(Element.WATER, Direction.UP_LEFT),
                Arrow(Element.EARTH, Direction.UP_RIGHT),
                Arrow(Element.AIR, Direction.RIGHT)
            )
        )

        val tile10 = Tile(
            10, points = 1, Element.WATER, listOf(
                Arrow(Element.FIRE, Direction.UP)
            )
        )

        val tile11 = Tile(
            11, points = 3, Element.FIRE, listOf(
                Arrow(Element.AIR, Direction.UP_LEFT),
                Arrow(Element.AIR, Direction.DOWN_RIGHT)
            )
        )
        // filling the board with those tiles

        player.board[Pair(0, 0)] = tile1
        player.board[Pair(1, 0)] = tile2
        player.board[Pair(2, 0)] = tile3
        player.board[Pair(0, 1)] = tile4
        player.board[Pair(1, 1)] = tile5
        player.board[Pair(0, 2)] = tile6
        player.board[Pair(-1, -1)] = tile7
        player.board[Pair(-1, 0)] = tile8
        player.board[Pair(1, -1)] = tile9
        player.board[Pair(1, -2)] = tile10
        player.board[Pair(2, -2)] = tile11


        // filling tiles with discs
        repeat(2) {
            tile1.discs.add(popLastElement(player.discs))
        }
        repeat(2) {
            tile2.discs.add(popLastElement(player.discs))
        }
        repeat(2) {
            tile3.discs.add(popLastElement(player.discs))
        }
        repeat(3) {
            tile4.discs.add(popLastElement(player.discs))
        }
        repeat(2) {
            tile5.discs.add(popLastElement(player.discs))
        }
        repeat(1) {
            tile6.discs.add(popLastElement(player.discs))
        }
        repeat(2) {
            tile7.discs.add(popLastElement(player.discs))
        }
        repeat(1) {
            tile8.discs.add(popLastElement(player.discs))
        }
        repeat(4) {
            tile9.discs.add(popLastElement(player.discs))
        }
        repeat(1) {
            tile10.discs.add(popLastElement(player.discs))
        }
        repeat(2) {
            tile11.discs.add(popLastElement(player.discs))
        }
        // filling available arrows with discs
        tile1.arrows.forEach {
            when (it.element) {
                Element.FIRE -> {
                    it.disc.add(popLastElement(tile1.discs))
                }

                else -> {}
            }
        }

        tile2.arrows.forEach {
            when (it.element) {
                Element.AIR -> it.disc.add(popLastElement(tile2.discs))
                else -> {}
            }
        }

        tile3.arrows.forEach {
            if (it.element == Element.AIR && it.direction == Direction.UP_LEFT) {
                it.disc.add(popLastElement(tile3.discs))
            }
        }

        tile4.arrows.forEach {
            if (it.element == Element.FIRE && it.direction == Direction.DOWN_RIGHT) {
                it.disc.add(popLastElement(tile4.discs))
            }
        }

        tile5.arrows.forEach {
            when (it.element) {
                Element.FIRE -> {
                    it.disc.add(popLastElement(tile5.discs))
                }

                Element.EARTH -> it.disc.add(popLastElement(tile5.discs))
                else -> {}
            }
        }

        tile6.arrows.forEach {
            if (it.element == Element.WATER && it.direction == Direction.DOWN) {
                it.disc.add(popLastElement(tile6.discs))
            }
        }

        tile7.arrows.forEach {
            when (it.element) {
                Element.FIRE -> {
                    it.disc.add(popLastElement(tile7.discs))
                }

                Element.EARTH -> it.disc.add(popLastElement(tile7.discs))
                else -> {}
            }
        }

        tile8.arrows.forEach {
            if (it.element == Element.EARTH && it.direction == Direction.UP_RIGHT) {
                it.disc.add(popLastElement(tile8.discs))
            }
        }

        tile9.arrows.forEach {
            when (it.element) {
                Element.FIRE -> it.disc.add(popLastElement(tile9.discs))
                Element.EARTH -> it.disc.add(popLastElement(tile9.discs))
                Element.WATER -> it.disc.add(popLastElement(tile9.discs))
                else -> {}
            }
        }

        tile10.arrows.forEach {
            if (it.element == Element.FIRE && it.direction == Direction.UP) {
                it.disc.add(popLastElement(tile10.discs))
            }
        }

        tile11.arrows.forEach {
            if (it.element == Element.AIR && it.direction == Direction.UP_LEFT) {
                it.disc.add(popLastElement(tile11.discs))
            }
            if (it.element == Element.AIR && it.direction == Direction.DOWN_RIGHT) {
                it.disc.add(popLastElement(tile11.discs))
            }
        }

        // building the score map for player
        val scoreMap = kiService.buildScoreMap(player.board)
        print(scoreMap)
        // tile we want to place
        val tile12 = Tile(
            12, points = 1, Element.WATER, listOf(
                Arrow(Element.AIR, Direction.LEFT)
            )
        )
        // discs for this tile
        repeat(1) {
            tile12.discs.add(popLastElement(player.discs))
        }

        // save timestamp
        //val start = System.currentTimeMillis()
        // calculating all possible placements for that tile
        val potentialPlacements = kiService.calculatePotentialTilePlacements(tile12, scoreMap, player)

        // get timestamp after calculation
        //val end = System.currentTimeMillis()
        //val differenceInSeconds = (end - start) / 1000.0

        // get a list of top 10 placements
        //val highestScoresTop = potentialPlacements.toList().sortedByDescending { it.score }.take(10)
        // the best position for the tile
        val highestScore = potentialPlacements.maxByOrNull { it.score }

        potentialPlacements.forEach {
            assert(this.rootService.playerActionService.validLocations(player.board).contains(it.location))
        }

        assertEquals(Pair(0, -1), highestScore?.location)
        assertEquals(Direction.DOWN, highestScore?.direction)
    }

    @Test
    fun `test AI performance`() {
        val rootService = RootService()
        val playerNames: MutableList<Triple<String, Color, PlayerType>> = mutableListOf()
        val alice = Triple("Alice", Color.WHITE, PlayerType.BEST_AI)
        val bob = Triple("Bob", Color.BROWN, PlayerType.BEST_AI)

        playerNames.add(alice)
        playerNames.add(bob)


        // Initialize Alice's parameters

        var aliceParameters = GameParams()


        // Initialize Bob's parameters to be the same as Alice's
        var bestParameters = GameParams()

        // Keep track of best score and best parameters
        var bestScore = Int.MIN_VALUE

        // List of parameters to try for Alice
        var i = 0
        var currentParameterIndex = 0
        val last = 10
        repeat(last + 1) {
            println("trial $i")

            var adjustweight = 0.1


            if (Random.nextBoolean()) {
                adjustweight = -0.1
            }

            if (i % 4 == 0) {
                aliceParameters.discBlockedWeight = aliceParameters.discBlockedWeight + adjustweight
            } else if (i % 4 == 1) {
                aliceParameters.arrowWeight = aliceParameters.arrowWeight + adjustweight
            } else if (i % 4 == 2) {
                aliceParameters.discWeight = aliceParameters.discWeight + adjustweight
            } else if (i % 4 == 3) {
                aliceParameters.arrowBlockedWeight = aliceParameters.arrowBlockedWeight + adjustweight

            }



            i++

            if (i == last) {
                aliceParameters = GameParams()
            }



            val aliceAndBobsWins = playSomeGames(rootService, aliceParameters, bestParameters, i, playerNames)

            if (i != last) {

                if (aliceAndBobsWins.first -2 > aliceAndBobsWins.second) {
                    bestParameters = aliceParameters.getCopy()
                    print("learning new params form alice after she won ${aliceAndBobsWins.first} times\n")

                    println("new params: ")
                    println("discBlockedWeight: ${bestParameters.discBlockedWeight}")
                    println("arrowWeight: ${bestParameters.arrowWeight}")
                    println("discWeight: ${bestParameters.discWeight}")
                    println("arrowBlockedWeight: ${bestParameters.arrowBlockedWeight}")
                    println("")

                } else {
                    // If Alice's score is not better, revert the parameter change
                    aliceParameters = bestParameters.getCopy()
                    print("reverting params to best params after bob won $aliceAndBobsWins.second times\n")
                }
            } else {
                print("Alice (init) has ${rootService.currentGame!!.players[0].discs.size} discs left\n")
                println("Bob (best) has ${rootService.currentGame!!.players[1].discs.size} discs left\n")


                //print points
                println("Alice (init) has ${rootService.currentGame!!.players[0].points.first} points")
                println("Bob (best) has ${rootService.currentGame!!.players[1].points.first} points")


                println("Alice (init) has won ${aliceAndBobsWins.first} times")
                println("Bob (best) has won ${aliceAndBobsWins.second} times")

                print("best config: \n")
                print("checkDiscleft: ${bestParameters.checkDiscleftWeight}\n")
                print("arrowWeight: ${bestParameters.arrowWeight}\n")
                print("discWeight: ${bestParameters.discWeight}\n")
                print("arrowBlockedWeight: ${bestParameters.arrowBlockedWeight}\n")
                print("discBlockedWeight: ${bestParameters.discBlockedWeight}\n")


            }


        }


    }

    @Test
    fun `optimize AI Random search`() {
        val rootService = RootService()
        val playerNames: MutableList<Triple<String, Color, PlayerType>> = mutableListOf()
        val alice = Triple("Alice", Color.WHITE, PlayerType.BEST_AI)
        val bob = Triple("Bob", Color.BROWN, PlayerType.BEST_AI)


        playerNames.add(alice)
        playerNames.add(bob)

        val trainingRounds = 200

        // Initialize Alice's parameters

        var aliceParameters = GameParams()


        // Initialize Bob's parameters to be the same as Alice's
        var bestParameters = GameParams()


        val initConfigForBob = GameParams()


        var i = 0

        repeat(trainingRounds + 1) {
            println("trial $i")

            var adjustweight = 0.1


            // set one random parameter to a random value
            when (Random.nextInt(5)) {
                0 -> aliceParameters.discBlockedWeight = Random.nextDouble(0.0, 6.0)
                1 -> aliceParameters.arrowWeight = Random.nextDouble(0.0, 6.0)
                2 -> aliceParameters.discWeight = Random.nextDouble(0.0, 6.0)
                3 -> aliceParameters.arrowBlockedWeight = Random.nextDouble(0.0, 6.0)
                4 -> aliceParameters.checkDiscleftWeight = Random.nextDouble(0.0, 6.0)
            }




            i++

            if (i == trainingRounds) {
                aliceParameters = GameParams()
            }



            val aliceAndBobsWins = playSomeGames(rootService, aliceParameters, bestParameters, i, playerNames, 15)

            val initAliceAndBobsWins = playSomeGames(rootService, aliceParameters, initConfigForBob, i, playerNames, 10)

            if (i != trainingRounds) {

                if ((aliceAndBobsWins.first -2 > aliceAndBobsWins.second) && (initAliceAndBobsWins.first  > initAliceAndBobsWins.second)) {
                    bestParameters = aliceParameters.getCopy()
                    print("learning new params form alice after she won ${aliceAndBobsWins.first} times\n")

                    println("new params: ")
                    println("discBlockedWeight: ${bestParameters.discBlockedWeight}")
                    println("arrowWeight: ${bestParameters.arrowWeight}")
                    println("discWeight: ${bestParameters.discWeight}")
                    println("arrowBlockedWeight: ${bestParameters.arrowBlockedWeight}")
                    println("")

                } else {
                    // If Alice's score is not better, revert the parameter change
                    aliceParameters = bestParameters.getCopy()
                    print("reverting params to best params after bob won ${aliceAndBobsWins.second} times\n")
                }
            } else {
                print("Alice (init) has ${rootService.currentGame!!.players[0].discs.size} discs left\n")
                println("Bob (best) has ${rootService.currentGame!!.players[1].discs.size} discs left\n")


                //print points
                println("Alice (init) has ${rootService.currentGame!!.players[0].points.first} points")
                println("Bob (best) has ${rootService.currentGame!!.players[1].points.first} points")


                println("Alice (init) has won ${aliceAndBobsWins.first} times")
                println("Bob (best) has won ${aliceAndBobsWins.second} times")

                print("best config: \n")
                print("checkDiscleft: ${bestParameters.checkDiscleftWeight}\n")
                print("arrowWeight: ${bestParameters.arrowWeight}\n")
                print("discWeight: ${bestParameters.discWeight}\n")
                print("arrowBlockedWeight: ${bestParameters.arrowBlockedWeight}\n")
                print("discBlockedWeight: ${bestParameters.discBlockedWeight}\n")


            }


        }


    }


    fun playSomeGames(
        rootService: RootService,
        aliceParameters: GameParams,
        bestParameters: GameParams,
        i: Int,
        playerNames: MutableList<Triple<String, Color, PlayerType>>,
        numberGames: Int = 10
    ): Pair<Int, Int> {
        val aliceWins = mutableListOf<Int>()
        val bobWins = mutableListOf<Int>()

        var j = 0
        repeat(numberGames) {
            print("trial $i.$j\n")
            j++
            rootService.gameService.startNewGame(playerNames)

            var pointsP1 = rootService.currentGame!!.players[0].points.first
            var pointsP2 = rootService.currentGame!!.players[1].points.first


            var player1Minus = 0
            var player2Minus = 0
            val player1Points = mutableListOf<Int>()
            val player2Points = mutableListOf<Int>()


            repeat(50) {
                if (rootService.currentGame!!.actPlayer.name == "Alice") {
                    rootService.kIService.checkDiscleftWeight = aliceParameters.checkDiscleftWeight
                    rootService.kIService.arrowWeight = aliceParameters.arrowWeight
                    rootService.kIService.discWeight = aliceParameters.discWeight
                    rootService.kIService.arrowBlockedWeight = aliceParameters.arrowBlockedWeight
                    rootService.kIService.discBlockedWeight = aliceParameters.discBlockedWeight
                } else {
                    rootService.kIService.checkDiscleftWeight = bestParameters.checkDiscleftWeight
                    rootService.kIService.arrowWeight = bestParameters.arrowWeight
                    rootService.kIService.discWeight = bestParameters.discWeight
                    rootService.kIService.arrowBlockedWeight = bestParameters.arrowBlockedWeight
                    rootService.kIService.discBlockedWeight = bestParameters.discBlockedWeight
                }

                rootService.kIService.playBestMove()
                //println("current round: $i")
                //println("current player: ${rootService.currentGame!!.actPlayer.name}\n")

                //print("Alice has ${rootService.currentGame!!.players[0].discs.size } discs left\n")
                //println("Bob has ${rootService.currentGame!!.players[1].discs.size } discs left\n")

                val pointsP1Now = rootService.currentGame!!.players[0].points.first
                val pointsP2Now = rootService.currentGame!!.players[1].points.first

                var pointsP1Diff = pointsP1Now - pointsP1
                var pointsP2Diff = pointsP2Now - pointsP2

                pointsP1 = pointsP1Now
                pointsP2 = pointsP2Now

                if (pointsP1Diff >= 0) {
                    player1Points.add(pointsP1Now)
                } else {
                    player1Minus++
                }

                if (pointsP2Diff >= 0) {
                    player2Points.add(pointsP2Now)
                } else {
                    player2Minus++
                }


            }

            if (rootService.currentGame!!.players[0].points.first > rootService.currentGame!!.players[1].points.first) {
                aliceWins.add(1)
            } else if (rootService.currentGame!!.players[0].points.first < rootService.currentGame!!.players[1].points.first) {
                bobWins.add(1)
            }
        }
        return Pair(aliceWins.size, bobWins.size)
    }



    @Test
    fun `compare two parameter sets`(){




        val aliceParameters = GameParams()

        val bobParameters = GameParams()

        bobParameters.arrowWeight = 2.1
        bobParameters.discWeight = 4.0
        bobParameters.arrowBlockedWeight = 1.8
        bobParameters.discBlockedWeight = 0.4
        bobParameters.checkDiscleftWeight = 1.0


        val i = 0

        val playerNames: MutableList<Triple<String, Color, PlayerType>> = mutableListOf()
        val alice = Triple("Alice", Color.WHITE, PlayerType.BEST_AI)
        val bob = Triple("Bob", Color.BROWN, PlayerType.BEST_AI)

        playerNames.add(alice)
        playerNames.add(bob)

        val aliceAndBobsWins = playSomeGames(rootService, aliceParameters, bobParameters, i, playerNames, 30)


        //print("Alice has ${rootService.currentGame!!.players[0].discs.size} discs left\n")
        //println("Bob  has ${rootService.currentGame!!.players[1].discs.size} discs left\n")

        println("Alice  has won ${aliceAndBobsWins.first} times")
        println("Bob  has won ${aliceAndBobsWins.second} times")

    }


    class GameParams {
        var checkDiscleftWeight = 1.0
        var arrowWeight = 2.0
        var discWeight = 3.0
        var arrowBlockedWeight = 1.0
        var discBlockedWeight = 0.4


        fun getCopy(): GameParams {
            val copy = GameParams()
            copy.checkDiscleftWeight = this.checkDiscleftWeight
            copy.arrowWeight = this.arrowWeight
            copy.discWeight = this.discWeight
            copy.arrowBlockedWeight = this.arrowBlockedWeight
            copy.discBlockedWeight = this.discBlockedWeight
            return copy
        }
    }
}
