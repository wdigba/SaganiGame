package service

import entity.*
import kotlin.test.*
class KIServiceRandomTest {

    val rootService = RootService()
    private val john = Triple("JohnAI", Color.BLACK, PlayerType.RANDOM_AI)
    private val jo = Triple("JoAI", Color.WHITE, PlayerType.RANDOM_AI)
    private val playerNames = mutableListOf(john, jo)

    @Test
    fun calculateRandomMoveTest() {
        var tile1 = Tile(1, 6, Element.WATER, listOf(
            Arrow(Element.FIRE, Direction.UP),
            Arrow(Element.AIR, Direction.DOWN),
            Arrow(Element.EARTH, Direction.RIGHT)
        ))

        rootService.gameService.startNewGame(playerNames)
        val game = rootService.currentGame
        assertNotNull(game)

        game.players[0].discs.clear()
        repeat(24) {
            game.players[0].discs.add(Disc.SOUND)
        }

        game.offerDisplay.clear()

        game.offerDisplay.add(tile1)


        /*var tile2 = Tile(2, 3, Element.FIRE, listOf(
            Arrow(Element.EARTH, Direction.DOWN),
            Arrow(Element.WATER, Direction.UP)
        ))

        game.offerDisplay.add(tile2)

         */

        rootService.kIServiceRandom.calculateRandomMove()
        println(game.players[0].board)
        rootService.gameService.changeToNextPlayer()
    }
}