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
import tools.aqua.bgw.components.uicomponents.ComboBox
import tools.aqua.bgw.core.Alignment


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
        posY = 200,
        front = ColorVisual(255, 255, 255, 50)
    ).apply {
        opacity = 0.3
    }
    val smallCardStack2 = CardView(
        width = 100,
        height = 100,
        posX = 20,
        posY = 330,
        front = ColorVisual(255, 255, 255, 50)
    ).apply {
        opacity = 0.3
    }

     val soundDiscs = TokenView(
        width = 50,
        height = 50,
        posX = 20,
        posY = 450,
        visual = ColorVisual(255, 255, 255)
    )

   val cacophonyDiscs = TokenView(
        width = 50,
        height = 50,
        posX = 20,
        posY = 650,
        visual = ColorVisual.RED
    )

    //---------------------------------------------------------

    /**
     * Tile Pane
     */
    //----------------------------------------------------------


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


    private val intermezzoLayout: LinearLayout<CardView> = LinearLayout(
        width = 100,
        height = 630,
        posX = 1820,
        posY = 170,
        visual = ColorVisual(221, 161, 94)
    )

    private val intermezzoLabel = Label(
        width = 200,
        height = 100,
        posX = 1800,
        posY = 450,
        text = "INTERMEZZO",
        font = Font(size = 30)
    ).apply {
        rotation = 90.0
    }


    val intermezzoOffer1 = CardView(
        width = 120,
        height = 120,
        posX = 1750,
        posY = 200,
        front = ColorVisual(GameColor.chaletGreen)
    )
    val intermezzoOffer2 = CardView(
        width = 120,
        height = 120,
        posX = 1750,
        posY = 350,
        front = ColorVisual(GameColor.chaletGreen)
    )
    val intermezzoOffer3 = CardView(
        width = 120,
        height = 120,
        posX = 1750,
        posY = 500,
        front = ColorVisual(GameColor.chaletGreen)
    )
    val intermezzoOffer4 = CardView(
        width = 120,
        height = 120,
        posX = 1750,
        posY = 650,
        front = ColorVisual(GameColor.chaletGreen)
    )


    //-------------------------------------

    /**
     * Bottom Pane
     */
    //-------------------------------------
    private val bottomPaneLayout: LinearLayout<CardView> = LinearLayout(
        width = 1920,
        height = 80,
        posX = 0,
        posY = 1000,
        visual = ColorVisual(221, 161, 94)
    )

     val undoButton = StandardButton(
        width = 100,
        height = 50,
        posX = 20,
        posY = 1020,
        text = "UNDO"
    )

    val redoButton = StandardButton(
        width = 100,
        height = 50,
        posX = 130,
        posY = 1020,
        text = "REDO"
    )

     val scoreButton = StandardButton(
        width = 100,
        height = 50,
        posX = 240,
        posY = 1020,
        text = "SCORE"
    )

     val rotateButton = StandardButton(
        width = 100,
        height = 50,
        posX = 350,
        posY = 1020,
        text = "ROTATE"
    )

    val testButton = StandardButton(
        width = 100,
        height = 50,
        posX = 460,
        posY = 1020,
        text = "TEST"
    )

    val confirmButton = StandardButton(
        width = 100,
        height = 50,
        posX = 1780,
        posY = 1020,
        text = "CONF"
    )

    val zoomInButton = StandardButton(
        width = 50,
        height = 50,
        posX = 1500,
        posY = 1020,
        text = "+"
    )

    val zoomOutButton = StandardButton(
        width = 50,
        height = 50,
        posX = 1560,
        posY = 1020,
        text = "-"
    )

    val moveLeftButton = StandardButton(
        width = 80,
        height = 50,
        posX = 700,
        posY = 1020,
        text = "LEFT"
    )

    val moveUpButton = StandardButton(
        width = 80,
        height = 50,
        posX = 790,
        posY = 1020,
        text = "UP"
    )
    val moveDownButton = StandardButton(
        width = 90,
        height = 50,
        posX = 880,
        posY = 1020,
        text = "DOWN"
    )

    val moveRightButton = StandardButton(
        width = 80,
        height = 50,
        posX = 980,
        posY = 1020,
        text = "RIGHT"
    )

    val homeButton = StandardButton(
        width = 80,
        height = 50,
        posX = 1120,
        posY = 1020,
        text = "HOME"
    )

    val simulationSpeedLabel = Label(
        width = 70,
        height = 50,
        posX = 1210,
        posY = 1020,
        text = "Speed:"
    )

    val simulationSpeedDropDown = ComboBox(
        width = 100,
        height = 50,
        posX = 1270,
        posY = 1020,
        items = listOf("Fast", "Normal", "Slow", "Slowest")
    )

    val saveGameButton = StandardButton(
        width = 80,
        height = 50,
        posX = 1400,
        posY = 1020,
        text = "Save"
    )

    val playerName = Label (
        width = 200,
        height = 50,
        posX = 1400,
        posY = 40,
        text = "Active Player: ${""}",
        font = Font(size = 18, color = GameColor.cornSilk),
        alignment = Alignment.CENTER,
        isWrapText = true,
        visual = ColorVisual.TRANSPARENT,
    )

    //-----------------------------------------------------------------------

    init {

        tilePane.add(sampleTile)

        sampleTile.apply {
            onMouseClicked = {
                println("HELLO")
            }
        }




        background = ColorVisual(GameColor.chaletGreen)
        addComponents(
            tilePane,
            bottomPaneLayout,
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
            homeButton,
            intermezzoLayout, intermezzoLabel, intermezzoOffer1, intermezzoOffer2, intermezzoOffer3,
            intermezzoOffer4,
            smallCardStack1, smallCardStack2, cacophonyDiscs, soundDiscs,
            cardStack, offer1, offer2, offer3, offer4, offer5,
            simulationSpeedLabel, simulationSpeedDropDown,saveGameButton,
            playerName
        )
    }

    /**
     * Properly initializes the rows and columns of the GridPanes and puts the panes together.
     */


}