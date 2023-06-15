package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

class RuleScene(private val rootService: RootService) :
    BoardGameScene(1920, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Regeln - Comming Soon",
        font = Font(size = 22)
    )

        init {
            background = ColorVisual(158, 181, 91)
            addComponents(headlineLabel)
        }
}