package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

class ConfigurationScene(private val rootService: RootService) :
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

    val backButton = StandardButton(
        posX = 100, posY = 400,
        text = "Go Back",
    )

    init {
        opacity = 1.0
        background = ColorVisual(GameColor.cornSilk)
        addComponents(
            headlineLabel,
            networkButton, playersButton, backButton
        )

    }

}