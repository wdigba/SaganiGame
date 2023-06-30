package view

import service.GameService
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
    private val Player1Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
    ).apply { playersScore() }
    private val Player2Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 255,
    ).apply { playersScore() }
    private val Player3Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 305,
    ).apply { playersScore() }
    private val Player4Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 355,
    ).apply { playersScore() }
    val backButton = StandardButton(
        posX = 50, posY = 465,
        width = 140,
        text = "Go Back")

    init {
        opacity = 1.0
        background = ColorVisual(GameColor.cornSilk)
        addComponents(headlineLabel,Player1Label, Player2Label, Player3Label, Player4Label, backButton)
    }

    private fun playersScore(){
        val GameService = rootService.currentGame
        checkNotNull(GameService)


        Player1Label.text = "${GameService.players[0].name} scored ${GameService.players[0].points} points."
        Player2Label.text = "${GameService.players[1].name} scored ${GameService.players[1].points} points."
        Player3Label.text = "${GameService.players[2].name} scored ${GameService.players[2].points} points."
        Player4Label.text = "${GameService.players[3].name} scored ${GameService.players[3].points} points."
    }


}