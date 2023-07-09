package view.controllers

import Location
import entity.Color
import entity.Player
import service.RootService
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.scene.ScoreScene

class ScoreSceneController(private val scoreScene: ScoreScene, private val rootService: RootService) : Refreshable {
    /**
     * [showPlayersScore] sets text fields in playerLabels with current scores
     */
    private fun showPlayersScore() {
        // use current game state
        val game = rootService.currentGame
        checkNotNull(game) { "There is no game." }
        for (i in 0 until game.players.size) {
            scoreScene.playerLabels[i].text = "${game.players[i].name} scored " +
                    "${game.players[i].points.first} points on turn ${game.players[i].points.second}."
        }
    }

    private fun showBoardButtons() {
        // use current game state
        val game = rootService.currentGame
        checkNotNull(game) { "There is no game." }
        for (i in 0 until game.players.size) {
            scoreScene.boardButtons[i].isVisible = true
            scoreScene.boardButtons[i].isDisabled = false
            scoreScene.boardButtons[i].text = "Visit"
            when (game.players[i].color) {
                Color.BLACK -> scoreScene.boardButtons[i].visual = ColorVisual(GameColor.black)
                Color.BROWN -> scoreScene.boardButtons[i].visual = ColorVisual(GameColor.brown)
                Color.GREY -> scoreScene.boardButtons[i].visual = ColorVisual(GameColor.gray)
                else -> scoreScene.boardButtons[i].visual = ColorVisual(GameColor.white)
            }
        }
    }

    /**
     * [refreshAfterStartNewGame] refreshes score board.
     */
    override fun refreshAfterStartNewGame(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {
        // reset playerLabels
        scoreScene.playerLabels.forEach { it.text = "" }
        // reset boardButtons
        scoreScene.boardButtons.forEach {
            it.text = ""
            it.isVisible = false
            it.isDisabled = true
            // show current score board
            showPlayersScore()
            // show and enable boardButtons
            showBoardButtons()
        }
    }

    /**
     * [refreshAfterLoadGame] refreshes score board.
     */
    override fun refreshAfterLoadGame() {
        // reset playerLabels
        scoreScene.playerLabels.forEach { it.text = "" }
        // reset boardButtons
        scoreScene.boardButtons.forEach {
            it.text = ""
            it.isVisible = false
            it.isDisabled = true
        }
        // show current score board
        showPlayersScore()
        // show and enable boardButtons
        showBoardButtons()
    }

    /**
     * [refreshAfterChangeToNextPlayer] refreshes score board.
     */
    override fun refreshAfterChangeToNextPlayer(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {
        // show current score board
        showPlayersScore()
    }

    /**
     * [refreshAfterRedo] refreshes score board.
     */
    override fun refreshAfterRedo() {
        // show current score board
        showPlayersScore()
    }

    /**
     * [refreshAfterUndo] refreshes score board.
     */
    override fun refreshAfterUndo() {
        // show current score board
        showPlayersScore()
    }

    /**
     * [refreshAfterCalculateWinner] refreshes score board.
     */
    override fun refreshAfterCalculateWinner() {
        // show final score board
        showPlayersScore()
        showBoardButtons()
    }
}
