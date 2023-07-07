package view.scene

import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.StandardButton

class ConfigurationScene :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Welcome to Sagani",
        font = Font(size = 22)
    )

    val networkButton = StandardButton(
        posX = 100, posY = 200,
        text = "Network game"
    )

    val playersButton = StandardButton(
        posX = 100, posY = 300,
        text = "Add players",
    )

    val loadGameButton = StandardButton(
        posX = 100, posY = 400,
        text = "Load game",
    )

    val backButton = StandardButton(
        posX = 100, posY = 500,
        text = "Go Back",
    )

    init {
        opacity = 1.0
        background = ColorVisual(GameColor.cornSilk)
        addComponents(
            headlineLabel,
            networkButton, playersButton, backButton, loadGameButton
        )

    }

}