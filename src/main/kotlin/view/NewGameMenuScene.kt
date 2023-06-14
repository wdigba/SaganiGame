package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

class NewGameMenuScene(private val rootService: RootService) :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Welcome to Sagani",
        font = Font(size = 22)
    )

    val kIButton = Button(
        width = 200, height = 35,
        posX = 100, posY = 200,
        text = "Play with KI",
        font = Font(size = 18)
    ).apply {
        visual = ColorVisual(158, 181, 91)
    }

    val playButton = Button(
        width = 200, height = 35,
        posX = 100, posY = 300,
        text = "Play with others",
        font = Font(size = 18)
    ).apply {
        visual = ColorVisual(158, 181, 91)
    }

    val ruleButton = Button(
        width = 200, height = 35,
        posX = 100, posY = 400,
        text = "Rules",
        font = Font(size = 18)
    ).apply {
        visual = ColorVisual(158, 181, 91)
    }

    val quitButton = Button(
        width = 200, height = 35,
        posX = 100, posY = 500,
        text = "Quit",
        font = Font(size = 18)
    ).apply {
        visual = ColorVisual(158, 181, 91)
    }

    init {
        background = ColorVisual(255,249,222)
        addComponents(
            headlineLabel,
            kIButton, playButton, ruleButton,quitButton
        )
    }


}