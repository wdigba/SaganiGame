package service

import entity.*
import kotlin.test.*

/**
 * [KIServiceRandomTest] tests [KIServiceRandom]
 */
class KIServiceRandomTest {

    //initialize data for tests
    val rootService = RootService()
    private val john = Triple("JohnAI", Color.BLACK, PlayerType.RANDOM_AI)
    private val jo = Triple("JoAI", Color.WHITE, PlayerType.RANDOM_AI)
    private val playerNames = mutableListOf(john, jo)

    /**
     * [calculateRandomMoveTest] tests [KIServiceRandom.calculateRandomMove].
     * General [PlayerActionService.placeTile], which was used in function, was already tested in [PlaceTileTest].
     * All test were proved manually because AI is random
     */
//    @Test
//    fun calculateRandomMoveTest() {
//
//        // phase with one element in offer display
//        val tile1 = Tile(4, 6, Element.FIRE, listOf(
//            Arrow(Element.WATER, Direction.UP),
//            Arrow(Element.AIR, Direction.UP_RIGHT),
//            Arrow(Element.EARTH, Direction.UP_LEFT)
//        ))
//
//        rootService.gameService.startNewGame(playerNames)
//        val game = rootService.currentGame
//        assertNotNull(game)
//
//        game.players[0].discs.clear()
//
//        repeat(24) {
//            game.players[0].discs.add(Disc.SOUND)
//        }
//
//        game.offerDisplay.clear()
//
//        game.offerDisplay.add(tile1)
//
//        // on the board will be placed either tile from offer display or first card from stack
//        // with the same probability
//        println("offer display with one tile" + game.offerDisplay)
//        println("first element of stack" + game.stacks[0])
//        rootService.kIServiceRandom.calculateRandomMove()
//        // what exactly will be placed on the board
//        println("board with first random tile" + game.players[0].board)
//        rootService.gameService.changeToNextPlayer()
//
//        // normal phase
//        // creating more elements for offer display
//        val tile2 = Tile(13, 10, Element.FIRE, listOf(
//            Arrow(Element.FIRE, Direction.UP),
//            Arrow(Element.FIRE, Direction.RIGHT),
//            Arrow(Element.AIR, Direction.LEFT),
//            Arrow(Element.AIR, Direction.UP_LEFT)
//        ))
//
//        val tile3 = Tile(72, 3, Element.AIR, listOf(
//            Arrow(Element.AIR, Direction.UP_RIGHT),
//            Arrow(Element.FIRE, Direction.UP_LEFT)
//        ))
//
//        game.offerDisplay.clear()
//        game.offerDisplay.add(tile2)
//        game.offerDisplay.add(tile3)
//
//        println("offer display with two tiles" + game.offerDisplay)
//        // either tile2 or tile3 will be placed
//        rootService.kIServiceRandom.calculateRandomMove()
//        println("players board with two random tiles" + game.players[0].board)
//        rootService.gameService.changeToNextPlayer()
//
//        // intermezzo phase
//        game.intermezzoStorage.clear()
//        game.intermezzoPlayers.addAll(game.players)
//        println("first element of stack" + game.stacks[0])
//        println("second element of stack" + game.stacks[1])
//        game.intermezzoStorage.add(game.stacks[0])
//        game.intermezzoStorage.add(game.stacks[1])
//        println("intermezzo storage" + game.intermezzoStorage)
//        game.intermezzo = true
//
//        rootService.kIServiceRandom.calculateRandomMove()
//        println("intermezzo placement" + game.players[0].board)
//        rootService.gameService.changeToNextPlayer()
//    }
}