package service

import entity.*
import kotlin.random.Random

/**
 * [KIServiceRandom] is responsible for AI strategy, which implements random behavior
 */
class KIServiceRandom (private val rootService: RootService) {
    /**
     * [calculateRandomMove] implements random placement calculation using [PlayerActionService.placeTile]
     */
    fun calculateRandomMove() {
        val currentGame = rootService.currentGame
        checkNotNull(currentGame) { "There is no game." }

        // identify player
        val player = if (currentGame.intermezzo) {
            currentGame.players.find { it.color == currentGame.intermezzoPlayers[0].color }!!
        } else {
            currentGame.players.find { it.color == currentGame.actPlayer.color }!!
        }
        // random available location for the tile
        val randomLocation = rootService.playerActionService.validLocations(player.board).random()

        // random direction for the tile
        val randomDirection = Direction.tileDirection().random()

        // random tile based on game situation
        val randomTile = if (currentGame.intermezzo) {
            // choosing random tile from intermezzo storage
            currentGame.intermezzoStorage.random()
        } else if (currentGame.offerDisplay.size > 1) {
            // choosing random tile from the display
            currentGame.offerDisplay.random()
        } else if (Random.nextBoolean()) { // random decision when only one tile left
            currentGame.offerDisplay.random()
        }
        else {
            currentGame.stacks[0]
        }

        // placing process
        rootService.playerActionService.placeTile(randomTile, randomDirection, randomLocation)
    }
}