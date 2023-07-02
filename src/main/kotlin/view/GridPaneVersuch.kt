package view

import service.RootService
import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.components.layoutviews.*
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual


class GridPaneVersuch(private val rootService: RootService) : BoardGameScene(1920, 1080), Refreshable {

    /**
     * Upper Pane
     */
    //---------------------------------------------------------
    /**
     * defines cardStack
     */
    private val cardStack = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 20,
        posY = 20,
        visual = ColorVisual(255, 255, 255, 50)
    )

    //Offer Display
    private val offer1 = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 450,
        posY = 20,
        visual = ColorVisual(255, 255, 255, 50)
    )
    private val offer2 = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 650,
        posY = 20,
        visual = ColorVisual(255, 255, 255, 50)
    )
    private val offer3 = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 850,
        posY = 20,
        visual = ColorVisual(255, 255, 255, 50)
    )
    private val offer4 = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 1050,
        posY = 20,
        visual = ColorVisual(255, 255, 255, 50)
    )
    private val offer5 = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 1250,
        posY = 20,
        visual = ColorVisual(255, 255, 255, 50)
    )

    //---------------------------------------------------------


    /**
     * Left Pane
     */
    //---------------------------------------------------------

    private val smallCardStack1 = CardStack<CardView>(
        width = 100,
        height = 100,
        posX = 20,
        posY = 20,
        visual = ColorVisual(255, 255, 255, 50)
    )
    private val smallCardStack2 = CardStack<CardView>(
        width = 100,
        height = 100,
        posX = 20,
        posY = 150,
        visual = ColorVisual(255, 255, 255, 50)
    )

    private val soundDiscs = TokenView(
        width = 50,
        height = 50,
        posX = 20,
        posY = 400,
        visual = ColorVisual(255, 255, 255)
    )

    private val cacophonyDiscs = TokenView(
        width = 50,
        height = 50,
        posX = 20,
        posY = 600,
        visual = ColorVisual.RED
    )

    //---------------------------------------------------------


    /**
     * Right Pane
     */
    //----------------------------------
    //Intermezzo
    private val intermezzoLayout: LinearLayout<CardView> = LinearLayout(
        width = 100,
        height = 800,
        posX = 100,
        posY = 20,
        visual = ColorVisual(221, 161, 94)
    )

    private val intermezzoLabel = Label(
        width = 200,
        height = 100,
        posX = 50,
        posY = 400,
        text = "INTERMEZZO",
        font = Font(size = 30)
    ).apply {
        rotation = 90.0
    }


    private val intermezzoOffer1 = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 10,
        posY = 70,
        visual = ColorVisual(GameColor.chaletGreen)
    )
    private val intermezzoOffer2 = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 10,
        posY = 220,
        visual = ColorVisual(GameColor.chaletGreen)
    )
    private val intermezzoOffer3 = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 10,
        posY = 370,
        visual = ColorVisual(GameColor.chaletGreen)
    )
    private val intermezzoOffer4 = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 10,
        posY = 520,
        visual = ColorVisual(GameColor.chaletGreen)
    )
    private val intermezzoOffer5 = CardStack<CardView>(
        width = 120,
        height = 120,
        posX = 10,
        posY = 670,
        visual = ColorVisual(GameColor.chaletGreen)
    )

    //-------------------------------------

    /**
     * Bottom Pane
     */
    //-------------------------------------
    private val undoButton = StandardButton(
        width = 100,
        height = 50,
        posX = 20,
        posY = 20,
        text = "UNDO"
    )

    private val redoButton = StandardButton(
        width = 100,
        height = 50,
        posX = 150,
        posY = 20,
        text = "REDO"
    )

    private val scoreButton = StandardButton(
        width = 100,
        height = 50,
        posX = 280,
        posY = 20,
        text = "SCORE"
    )

    private val rotateButton = StandardButton(
        width = 100,
        height = 50,
        posX = 410,
        posY = 20,
        text = "ROTATE"
    )

    private val confirmButton = StandardButton(
        width = 100,
        height = 50,
        posX = 1780,
        posY = 20,
        text = "CONF"
    )

    //-----------------------------------------------------------------------

    /**
     * Panes
     */
    //-----------------------------------------------------------------------
    private val targetPane = Pane<ComponentView>(width = 1000, height = 1000)
    private val cameraPane = CameraPane(0, 0, 756, 134, target = targetPane)


    private val outerGridPane = GridPane<ComponentView>(0, 0, columns = 1, rows = 3, layoutFromCenter = false)
    private val innerGridPane = GridPane<ComponentView>(columns = 3, rows = 1, layoutFromCenter = false)

    private val upperPane = Pane<ComponentView>(0, 0, 1920, 162, visual = ColorVisual(GameColor.chaletGreen))
    private val bottomPane = Pane<ComponentView>(0, 0, 1920, 80, visual = ColorVisual(221, 161, 94))
    private val leftPane = Pane<ComponentView>(0, 0, 150, 838, visual = ColorVisual(GameColor.chaletGreen))
    private val rightPane = Pane<ComponentView>(0, 0, 170, 838, visual = ColorVisual(GameColor.chaletGreen))

    //-----------------------------------------------------------------------


    init {

        //WIP !


        initViewStructure()

        val upperPaneList = mutableListOf<ComponentView>(cardStack, offer1, offer2, offer3, offer4, offer5)

        val leftPaneList = mutableListOf<ComponentView>(smallCardStack1, smallCardStack2, cacophonyDiscs, soundDiscs)

        val rightPaneList = mutableListOf<ComponentView>(intermezzoLayout,intermezzoLabel,intermezzoOffer1,intermezzoOffer2,intermezzoOffer3,
            intermezzoOffer4,intermezzoOffer5)


        val bottomPaneList =
            mutableListOf<ComponentView>(redoButton, undoButton, scoreButton, confirmButton, rotateButton)



        addComponentsToPane(upperPane, upperPaneList)
        addComponentsToPane(leftPane, leftPaneList)
        addComponentsToPane(rightPane, rightPaneList)
        addComponentsToPane(bottomPane, bottomPaneList)


        background = ColorVisual(GameColor.chaletGreen)
        addComponents(outerGridPane)
    }

    /**
     * Properly initializes the rows and columns of the GridPanes and puts the panes together.
     */
    private fun initViewStructure() {
        outerGridPane.setRowHeight(0, 162)
        outerGridPane.setRowHeight(1, 838)
        outerGridPane.setRowHeight(2, 80)

        outerGridPane[0, 0] = upperPane
        outerGridPane[0, 1] = innerGridPane
        outerGridPane[0, 2] = bottomPane

        innerGridPane.setColumnWidth(0, 150)
        innerGridPane.setColumnWidth(1, 1600)
        innerGridPane.setColumnWidth(2, 170)

        innerGridPane[0, 0] = leftPane
        innerGridPane[1, 0] = cameraPane
        innerGridPane[2, 0] = rightPane
    }

    private fun addComponentsToPane(pane: Pane<ComponentView>, componentList: MutableList<ComponentView>) {
        for (component in componentList) {
            pane.add(component)
        }
    }

}