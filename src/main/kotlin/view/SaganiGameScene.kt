package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual


class SaganiGameScene(private val rootService: RootService) : BoardGameScene(1920, 1080), Refreshable{
    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "GameScene - Comming Soon, Maybe not",
        font = Font(size = 22)
    )

    private val undoButton = StandardButton(
        posX = 50, posY = 980,
        text = "UNDO"
    ).apply {
    }
    private val redoButton = StandardButton(
        posX = 250, posY = 980,
        text = "REDO", alignment = Alignment.CENTER_LEFT
    ).apply {
    }


    init {
        background = ColorVisual( Color.chaletGreen)
        addComponents(headlineLabel,
       undoButton,redoButton )
    }

}