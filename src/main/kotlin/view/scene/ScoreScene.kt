package view.scene

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.StandardButton

/**
 * [ScoreScene] shows each player's current points and when they get them.
 * @property boardButtons connect [ScoreScene] to the board of the corresponding player
 * @property backButton returns to the main scene.
 * @property playerLabels show the players' points
 */
class ScoreScene : MenuScene(600, 1080) {

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
    private val board1Button = Button(
        width = 50, height = 35,
        posX = 500, posY = 205
    )
    private val board2Button = Button(
        width = 50, height = 35,
        posX = 500, posY = 255
    )
    private val board3Button = Button(
        width = 50, height = 35,
        posX = 500, posY = 305
    )
    private val board4Button = Button(
        width = 50, height = 35,
        posX = 500, posY = 355
    )

    val playerLabels = listOf(player1Label, player2Label, player3Label, player4Label)
    val boardButtons = listOf(board1Button, board2Button, board3Button, board4Button)

    init {
        opacity = 1.0
        background = ColorVisual(GameColor.cornSilk)
        addComponents(headlineLabel, backButton)
        playerLabels.forEach { addComponents(it) }
        boardButtons.forEach { addComponents(it) }
    }

}
