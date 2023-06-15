package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.components.uicomponents.Button


class SaganiGameScene(private val rootService: RootService) : BoardGameScene(1920, 1080), Refreshable{
    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "GameScene - Comming Soon, Maybe not",
        font = Font(size = 22)
    )

    private val undoButton = Button(
        width = 170, height = 40,
        posX = 50, posY = 980,
        text = "UNDO", font = Font(size = 30)
    ).apply {
        visual = ColorVisual(96,108,56)

    }
    private val redoButton = Button(
        width = 170, height = 40,
        posX = 250, posY = 980,
        text = "REDO", font = Font(size = 30), alignment = Alignment.CENTER_LEFT
    ).apply {
        visual = ColorVisual(96,108,56)
    }


    init {
        background = ColorVisual(96,108,56)
        addComponents(headlineLabel,
       undoButton,redoButton )
    }

}