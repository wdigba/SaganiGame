package view

import service.RootService
import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView

import tools.aqua.bgw.components.layoutviews.*
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual


class GridPaneVersuch(private val rootService: RootService) : BoardGameScene(1920, 1080), Refreshable {
    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "GameScene - Comming Soon, Maybe not",
        font = Font(size = 22)
    )

     val undoButton = StandardButton(
        posX = 50, posY = 50,
        width = 100, height = 50,
        text = "UNDO"
    )

    val redoButton = StandardButton(
        posX = 150, posY = 50,
        width = 100, height = 50,
        text = "REDO"
    )

     val scoreButton = StandardButton(
        posX = 350, posY = 50,
        width = 100, height = 50,
        text = "SCORE"
    )

     val rotateButton = StandardButton(
        posX = 500, posY = 50,
        width = 50, height = 50,
        text = "rotate"
    ).apply {
        componentStyle = "-fx-background-color: #606C38; -fx-background-radius: 50%"
    }

    private val confirmButton = StandardButton(
        posX = 800, posY = 50,
        width = 50, height = 50,
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


    private val outerGridPane = GridPane<ComponentView>(0, 0, columns = 1, rows = 3, layoutFromCenter = false)
    private val innerGridPane = GridPane<ComponentView>(columns = 3, rows = 1, layoutFromCenter = false)

    private val upperPane = Pane<ComponentView>(0, 0, 1920, 162, visual = ColorVisual.CYAN)
    private val bottomPane = Pane<ComponentView>(0, 0, 1920, 162, visual = ColorVisual.MAGENTA)
    private val leftPane = Pane<ComponentView>(0, 0, 288, 756, visual = ColorVisual.DARK_GRAY)
    private val rightPane = Pane<ComponentView>(0, 0, 288, 756, visual = ColorVisual.PINK)

    //1920, 1080
    //Pane
    init {

        //WIP !

        initViewStructure()


        val bottomPaneList = mutableListOf<ComponentView>(redoButton,undoButton,scoreButton,confirmButton,rotateButton)


        addComponentsToPane(bottomPane, bottomPaneList)


        background = ColorVisual(GameColor.chaletGreen)
        addComponents(outerGridPane)
    }

    /**
     * Properly initializes the rows and columns of the GridPanes and puts the panes together.
     */
    private fun initViewStructure(){
        outerGridPane.setRowHeight(0, 162)
        outerGridPane.setRowHeight(1, 756)
        outerGridPane.setRowHeight(2, 162)

        outerGridPane[0, 0] = upperPane
        outerGridPane[0, 1] = innerGridPane
        outerGridPane[0, 2] = bottomPane

        innerGridPane.setColumnWidth(0, 288)
        innerGridPane.setColumnWidth(1, 1344)
        innerGridPane.setColumnWidth(2, 288)

        innerGridPane[0, 0] = leftPane
        innerGridPane[1, 0] = targetPane
        innerGridPane[2, 0] = rightPane
    }

    private fun addComponentsToPane(pane: Pane<ComponentView>, componentList:MutableList<ComponentView>){
        for (component in componentList){
            pane.add(component)
        }
    }

}