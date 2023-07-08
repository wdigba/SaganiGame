package view.scene

import Location
import entity.Color
import entity.Player
import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.StandardButton

/**
 * [ScoreScene] shows each player's current points and when they get them.
 * @property boardButtons connect [ScoreScene] to the board of the corresponding player
 * @property backButton returns to the main scene.
 * @property playerLabels show the players' points
 */
class ScoreScene(private val rootService: RootService) :
    MenuScene(600, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 500, height = 50, posX = 50, posY = 50,
        text = "Score Board",
        font = Font(size = 22)
    )
    private val player1Label = Label(
        width = 400, height = 35,
        posX = 50, posY = 205,
    )
    private val player2Label = Label(
        width = 400, height = 35,
        posX = 50, posY = 255,
    )
    private val player3Label = Label(
        width = 400, height = 35,
        posX = 50, posY = 305,
    )
    private val player4Label = Label(
        width = 400, height = 35,
        posX = 50, posY = 355,
    )
    val backButton = StandardButton(
        posX = 50, posY = 465,
        width = 140,
        text = "Go Back"
    )
    val board1Button = Button(
        width = 50, height = 35,
        posX = 500, posY = 205
    )
    val board2Button = Button(
        width = 50, height = 35,
        posX = 500, posY = 255
    )
    val board3Button = Button(
        width = 50, height = 35,
        posX = 500, posY = 305
    )
    val board4Button = Button(
        width = 50, height = 35,
        posX = 500, posY = 355
    )

    private val playerLabels = listOf(player1Label, player2Label, player3Label, player4Label)
    private val boardButtons = listOf(board1Button, board2Button, board3Button, board4Button)

    init {
        opacity = 1.0
        background = ColorVisual(GameColor.cornSilk)
        addComponents(headlineLabel, backButton)
        playerLabels.forEach { addComponents(it) }
        boardButtons.forEach { addComponents(it) }
    }

    /**
     * [showPlayersScore] sets text fields in playerLabels with current scores
     */
    fun showPlayersScore() {
        // use current game state
        val game = rootService.currentGame
        checkNotNull(game) { "There is no game." }
        for (i in 0 until game.players.size) {
            playerLabels[i].text = "${game.players[i].name} scored " +
                    "${game.players[i].points.first} points on turn ${game.players[i].points.second}."
        }
    }

    fun showBoardButtons() {
        // use current game state
        val game = rootService.currentGame
        checkNotNull(game) { "There is no game." }
        for (i in 0 until game.players.size) {
            boardButtons[i].isVisible = true
            boardButtons[i].isDisabled = false
            boardButtons[i].text = "Visit"
            when (game.players[i].color) {
                Color.BLACK -> boardButtons[i].visual = ColorVisual(GameColor.black)
                Color.BROWN -> boardButtons[i].visual = ColorVisual(GameColor.brown)
                Color.GREY -> boardButtons[i].visual = ColorVisual(GameColor.gray)
                else -> boardButtons[i].visual = ColorVisual(GameColor.white)
            }
        }
    }

    /**
     * [refreshAfterStartNewGame] refreshes score board.
     */
    override fun refreshAfterStartNewGame(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {
        // reset playerLabels
        playerLabels.forEach { it.text = "" }
        // reset boardButtons
        boardButtons.forEach {
            it.text = ""
            it.isVisible = false
            it.isDisabled = true
            // show current score board
            // showPlayersScore()
            // show and enable boardButtons
            // showBoardButtons()
        }
    }

    /**
     * [refreshAfterLoadGame] refreshes score board.
     */
    override fun refreshAfterLoadGame() {
        // reset playerLabels
        playerLabels.forEach { it.text = "" }
        // reset boardButtons
        boardButtons.forEach {
            it.text = ""
            it.isVisible = false
            it.isDisabled = true
        }
        // show current score board
        // showPlayersScore()
        // show and enable boardButtons
        // showBoardButtons()
    }
    /*
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

     */
}
