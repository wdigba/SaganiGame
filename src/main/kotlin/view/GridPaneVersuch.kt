package view

import service.RootService
import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual


class GridPaneVersuch(private val rootService: RootService) : BoardGameScene(1920, 1080), Refreshable{
    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "GameScene - Comming Soon, Maybe not",
        font = Font(size = 22)
    )

    private val undoButton = StandardButton(
        posX = 50, posY = 980,
        width =100, height = 50,
        text = "UNDO"
    ).apply {
    }
    private val redoButton = StandardButton(
        posX = 200, posY = 980,
        width =100,height =50,
        text = "REDO"
    ).apply {
    }

    private val scoreButton = StandardButton(
        posX = 350, posY = 980,
        width =100,height =50,
        text = "SCORE"
    ).apply {
    }

    private val rotateButton = StandardButton(
        posX = 500, posY= 980,
        width=50, height =50,
        text = "rotate"
    ).apply {
        componentStyle = "-fx-background-color: #606C38; -fx-background-radius: 50%"
    }

    private val confirmButton = StandardButton(
        posX = 800, posY= 980,
        width=50, height =50,
        text = "Conf"
    ).apply {
        componentStyle = "-fx-background-color: #606C38; -fx-background-radius: 50%"
    }

    //defines an exact zone where midCards are displayed
    private val midCardsView: LinearLayout<CardView> = LinearLayout(
        width = 950,
        height = 140,
        posX = 500,
        posY = 78,
        spacing = 50,
        alignment = Alignment.CENTER,
        visual = ColorVisual(255, 255, 255, 50)
    )
    /**
     * defines cardStack
     */

    private val cardStack = CardStack<CardView>(
        width = 140,
        height = 140,
        posX = 160,
        posY = 160,
        visual = ColorVisual(255, 255, 255, 50)
    )
    private val smallcardStack = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 160,
        posY = 320,
        visual = ColorVisual(255, 255, 255, 50)
    )
    private val smallcardStack2 = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 160,
        posY = 480,
        visual = ColorVisual(255, 255, 255, 50)
    )


    private val targetPane = Pane<ComponentView>(width = 1000, height = 1000)
   //private val cameraPane = CameraPane<ComponentView>(width = 500, height = 500, targetPane)

    private val outerGridPane = GridPane<ComponentView>(columns = 3, rows = 1)
    private val innerGridPane = GridPane<ComponentView>(columns = 1, rows = 3)


    init {
        outerGridPane.set(1,0,innerGridPane)
        innerGridPane.set(0,1,redoButton)


        background = ColorVisual( Color.chaletGreen)
        addComponents(
            outerGridPane
        )
    }

}