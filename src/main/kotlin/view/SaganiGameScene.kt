package view

import service.RootService
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual


class SaganiGameScene(private val rootService: RootService) : BoardGameScene(1920, 1080), Refreshable {
    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "GameScene - Comming Soon, Maybe not",
        font = Font(size = 22)
    )

    private val undoButton = StandardButton(
        posX = 50, posY = 980,
        width = 100, height = 50,
        text = "UNDO"
    )

    private val redoButton = StandardButton(
        posX = 200, posY = 980,
        width = 100, height = 50,
        text = "REDO"
    )

    private val scoreButton = StandardButton(
        posX = 350, posY = 980,
        width = 100, height = 50,
        text = "SCORE"
    )

    private val rotateButton = StandardButton(
        posX = 500, posY = 980,
        width = 50, height = 50,
        text = "rotate"
    )

    private val confirmButton = StandardButton(
        posX = 800, posY = 980,
        width = 50, height = 50,
        text = "Conf"
    )

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
        posX = 160,
        posY = 160,
        width = 140,
        height = 140,
        visual = ColorVisual(255,255,255,50)

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


    init {

        background = ColorVisual(GameColor.chaletGreen)
        addComponents(
            headlineLabel,
            undoButton, redoButton,
            midCardsView, cardStack, smallcardStack, smallcardStack2,
            rotateButton, scoreButton, confirmButton
        )
    }

}
