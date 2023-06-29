package service

import entity.*
import kotlin.random.Random

class KIServiceRandom (private val rootService: RootService) {
    // check offer display and get any tile
    // check the board for available places
    // place the tile in the random direction
    // give a turn to the next player

    fun calculateRandomMove () {
        val currentGame = rootService.currentGame
        checkNotNull(currentGame) { "There is no game." }

        val player = if (currentGame.intermezzo) {
            currentGame.players.find { it.name == currentGame.intermezzoPlayers[0].name }!!
        } else {
            currentGame.players.find { it.name == currentGame.actPlayer.name }!!
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