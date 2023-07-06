package view.scene

import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.components.layoutviews.*
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.*
import java.awt.Color


class SaganiGameScene : BoardGameScene(1920, 1080), Refreshable {
    /**
     * Upper Pane
     */
    //---------------------------------------------------------
    /**
     * defines cardStack
     */
    val cardStack = CardView(
        width = 120,
        height = 120,
        posX = 20,
        posY = 20,
        front = ColorVisual(255, 255, 255, 50)
    )

    //Offer Display
    val offer1 = CardView(
        width = 120,
        height = 120,
        posX = 450,
        posY = 20,
        front = ColorVisual(255, 255, 255, 50)
    )
    val offer2 = CardView(
        width = 120,
        height = 120,
        posX = 650,
        posY = 20,
        front = ColorVisual(255, 255, 255, 50)
    )
    val offer3 = CardView(
        width = 120,
        height = 120,
        posX = 850,
        posY = 20,
        front = ColorVisual(255, 255, 255, 50)
    )
    val offer4 = CardView(
        width = 120,
        height = 120,
        posX = 1050,
        posY = 20,
        front = ColorVisual(255, 255, 255, 50)
    )
    val offer5 = CardView(
        width = 120,
        height = 120,
        posX = 1250,
        posY = 20,
        front = ColorVisual(255, 255, 255, 50)
    )

    //---------------------------------------------------------


    /**
     * Left Pane
     */
    //---------------------------------------------------------

    val smallCardStack1 = CardView(
        width = 100,
        height = 100,
        posX = 20,
        posY = 20,
        front = ColorVisual(255, 255, 255, 50)
    ).apply {
        opacity = 0.3
    }
    val smallCardStack2 = CardView(
        width = 100,
        height = 100,
        posX = 20,
        posY = 150,
        front = ColorVisual(255, 255, 255, 50)
    ).apply {
        opacity = 0.3
    }

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
     * Tile Pane
     */
    //----------------------------------------------------------
    private val upperPane = Pane<ComponentView>(
        0,
        0,
        upperPaneWidth,
        upperPaneHeight,
        visual = ColorVisual(Color.CYAN))

    private val bottomPane = Pane<ComponentView>(
        0,
        0,
        bottomPaneWidth,
        bottomPaneHeight,
        visual = ColorVisual(Color.MAGENTA))

    private val leftPane = Pane<ComponentView>(
        0,
        0,
        leftPaneWidth,
        leftPaneHeight,
        visual = ColorVisual(Color.ORANGE))

    private val rightPane = Pane<ComponentView>(
        0,
        0,
        rightPaneWidth,
        rightPaneHeight,
        visual = ColorVisual(Color.PINK))

    val tilePane = Pane<ComponentView>(
        width = 4440,
        height = 4440,
        posY = centerTilePanePosY,
        posX = centerTilePanePosX,
        visual = ColorVisual(Color.GRAY)
    )

    //--> 1600
    // 838


    //TODO
    var sampleTile = CardView(
        width = standardTileViewWidth,
        height = standardTileViewHeight,
        posX = centerPosInTilePaneX - 100,
        posY = centerPosInTilePaneY - 100,
        front = ColorVisual(255, 255, 255, 50)
    )


    //---------------------------------------------------------


    /**
     * Right Pane
     */
    //----------------------------------
    //Intermezzo

    // TODO: Intermezzo Offer abstände schön

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


    val intermezzoOffer1 = CardView(
        width = 120,
        height = 120,
        posX = 10,
        posY = 50,
        front = ColorVisual(GameColor.chaletGreen)
    )
    val intermezzoOffer2 = CardView(
        width = 120,
        height = 120,
        posX = 10,
        posY = 256,
        front = ColorVisual(GameColor.chaletGreen)
    )
    val intermezzoOffer3 = CardView(
        width = 120,
        height = 120,
        posX = 10,
        posY = 462,
        front = ColorVisual(GameColor.chaletGreen)
    )
    val intermezzoOffer4 = CardView(
        width = 120,
        height = 120,
        posX = 10,
        posY = 670,
        front = ColorVisual(GameColor.chaletGreen)
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
        posX = 130,
        posY = 20,
        text = "REDO"
    )

    private val scoreButton = StandardButton(
        width = 100,
        height = 50,
        posX = 240,
        posY = 20,
        text = "SCORE"
    )

    private val rotateButton = StandardButton(
        width = 100,
        height = 50,
        posX = 350,
        posY = 20,
        text = "ROTATE"
    )

    val testButton = StandardButton(
        width = 100,
        height = 50,
        posX = 460,
        posY = 20,
        text = "TEST"
    )

    val confirmButton = StandardButton(
        width = 100,
        height = 50,
        posX = 1780,
        posY = 20,
        text = "CONF"
    )

    val zoomInButton = StandardButton(
        width = 50,
        height = 50,
        posX = 1500,
        posY = 20,
        text = "+"
    )

    val zoomOutButton = StandardButton(
        width = 50,
        height = 50,
        posX = 1560,
        posY = 20,
        text = "-"
    )

    val moveLeftButton = StandardButton(
        width = 80,
        height = 50,
        posX = 700,
        posY = 20,
        text = "LEFT"
    )

    val moveUpButton = StandardButton(
        width = 80,
        height = 50,
        posX = 790,
        posY = 20,
        text = "UP"
    )
    val moveDownButton = StandardButton(
        width = 90,
        height = 50,
        posX = 880,
        posY = 20,
        text = "DOWN"
    )

    val moveRightButton = StandardButton(
        width = 80,
        height = 50,
        posX = 980,
        posY = 20,
        text = "RIGHT"
    )

    val homeButton = StandardButton(
        width = 80,
        height = 50,
        posX = 1200,
        posY = 20,
        text = "HOME"
    )


    //-----------------------------------------------------------------------

    /**
     * Panes
     */
    //-----------------------------------------------------------------------

    //private val cameraPane = CameraPane(0, 0, 756, 134, target = targetPane)


    private val outerGridPane = GridPane<ComponentView>(columns = 1, rows = 3, layoutFromCenter = false)
     val innerGridPane = GridPane<ComponentView>(columns = 3, rows = 1, layoutFromCenter = false)


    //-----------------------------------------------------------------------


    init {
        //WIP !

//        val game = rootService.currentGame
//        checkNotNull(game)
//        game.stacks.size

        initViewStructure()

        val upperPaneList = mutableListOf<ComponentView>(cardStack, offer1, offer2, offer3, offer4, offer5)

        val leftPaneList = mutableListOf<ComponentView>(smallCardStack1, smallCardStack2, cacophonyDiscs, soundDiscs)

        val rightPaneList = mutableListOf(
            intermezzoLayout, intermezzoLabel, intermezzoOffer1, intermezzoOffer2, intermezzoOffer3,
            intermezzoOffer4
        )

       // innerGridPane.set(1,0,sampleTile)

        val bottomPaneList =
            mutableListOf<ComponentView>(
                redoButton,
                undoButton,
                scoreButton,
                confirmButton,
                rotateButton,
                testButton,
                zoomInButton,
                zoomOutButton,
                moveLeftButton,
                moveUpButton,
                moveDownButton,
                moveRightButton,
                homeButton
            )

       // tilePane.add(sampleTile)

        sampleTile.apply {
            onMouseClicked = {
                println("HEELLOO")
            }
        }

        addComponentsToPane(upperPane, upperPaneList)
        addComponentsToPane(leftPane, leftPaneList)
        addComponentsToPane(rightPane, rightPaneList)
        addComponentsToPane(bottomPane, bottomPaneList)


        background = ColorVisual(GameColor.chaletGreen)
        addComponents(tilePane, outerGridPane)
    }

    /**
     * Properly initializes the rows and columns of the GridPanes and puts the panes together.
     */
    private fun initViewStructure() {
        outerGridPane.setRowHeight(0, 162)
        outerGridPane.setRowHeight(1, 838)
        outerGridPane.setRowHeight(2, 80)


        outerGridPane[0, 2] = bottomPane
        outerGridPane[0, 1] = innerGridPane
        outerGridPane[0, 0] = upperPane


        innerGridPane.setColumnWidth(0, 150)
        innerGridPane.setColumnWidth(1, 1600)
        innerGridPane.setColumnWidth(2, 170)

        innerGridPane[0, 0] = leftPane
        innerGridPane[2, 0] = rightPane


    }

    private fun addComponentsToPane(pane: Pane<ComponentView>, componentList: MutableList<ComponentView>) {
        for (component in componentList) {
            pane.add(component)
        }
    }

}