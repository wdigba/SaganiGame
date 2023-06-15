package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
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

    val networkButton = Button(
        width = 200, height = 35,
        posX = 100, posY = 200,
        text = "Network game",
        font = Font(size = 18)
    ).apply {
        visual = ColorVisual(96,108,56)
    }

    val playersButton = Button(
        width = 200, height = 35,
        posX = 100, posY = 300,
        text = "Add players",
        font = Font(size = 18)
    ).apply {
        visual = ColorVisual(96,108,56)
    }

    val backButton = Button(
        width = 200, height = 35,
        posX = 100, posY = 400,
        text = "Go Back",
        font = Font(size = 18)
    ).apply {
        visual = ColorVisual(96,108,56)

    }

    init {
        background = ColorVisual(254,250,224)
        addComponents(
            headlineLabel,
            networkButton, playersButton, backButton
        )

    }
}