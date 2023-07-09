package view.scene

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.StandardButton

/**
 * [EndScene] defines scene after game ends
 */
class EndScene(private val rootService: RootService) :
    MenuScene(400, 1080), Refreshable {

    val winnerLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        font = Font(size = 22)
    )
    val player1Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
    )
    val player2Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 255,
    )
    val player3Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 305,
    )
    val player4Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 355,
    )


    val quitButton = StandardButton(
        posX = 50, posY = 465,
        width = 140,
        text = "Quit",
    )

    //TODO
    val newGameButton = StandardButton(
        posX = 230, posY = 465,
        width = 140,
        text = "New game"
    )

    init {
        opacity = 1.0

        background = ColorVisual(GameColor.cornSilk)
        addComponents(winnerLabel, player4Label, player3Label, player2Label, player1Label, quitButton, newGameButton)
    }


}
