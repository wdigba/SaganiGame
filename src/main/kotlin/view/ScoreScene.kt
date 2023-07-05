package view


import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

class ScoreScene(private val rootService: RootService) :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Check, what \n" +
                "other’s players do ",
        font = Font(size = 22)
    )
    private val player1Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
    )

    private val player2Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 255,
    )
    private val player3Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 305,
    )
    private val player4Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 355,
    )

    val backButton = StandardButton(
        posX = 50, posY = 465,
        width = 140,
        text = "Go Back")

    init {
        opacity = 1.0
        background = ColorVisual(GameColor.cornSilk)
        addComponents(headlineLabel,player1Label, player2Label, player3Label, player4Label, backButton)
    }





}