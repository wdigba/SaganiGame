package view

import service.RootService
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

    val kIButton = StandardButton(
        posX = 100, posY = 200,
        text = "Play with KI",
    )

    val standardButton = StandardButton(
        posX = 100, posY = 300,
        text = "Play with others",
    )

    val ruleButton = StandardButton(
        posX = 100, posY = 400,
        text = "Rules",
    )

    val quitButton = StandardButton(
        posX = 100, posY = 500,
        text = "Quit",
    )

    init {
        opacity = 1.0
        background = ColorVisual(GameColor.cornSilk)
        addComponents(
            headlineLabel,
            kIButton, standardButton, ruleButton,quitButton
        )
    }


}