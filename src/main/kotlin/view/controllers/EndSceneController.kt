package view.controllers

import service.RootService
import view.scene.EndScene

class EndSceneController(private val endScene: EndScene, private val rootService: RootService) {

    init {
        showPlayerPoints()
    }

    private fun showPlayerPoints() {
        val game = rootService.currentGame
        checkNotNull(game) { "No started game found."}

        val playerLabels =
            listOf(endScene.player1Label, endScene.player2Label, endScene.player3Label, endScene.player4Label)

        val sortedPlayers = game.players.sortedByDescending { it.points.first }

        endScene.winnerLabel.text = "${sortedPlayers[0].name.uppercase()} WON!"
        for (i in 0 until sortedPlayers.size) {
            playerLabels[i].text = "${sortedPlayers[i].name}:         " +
                    "${sortedPlayers[i].points} points"

        }
        for (i in 0 until 4) {
            playerLabels[i].isVisible = i < sortedPlayers.size
        }

    }
}