package view.scene

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.StandardButton

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
    )
    private val Player2Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 255,
    )
    private val Player3Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 305,
    )
    private val Player4Label = Label(
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
        addComponents(headlineLabel,Player1Label, Player2Label, Player3Label, Player4Label, backButton)
    }

    private fun playersScore(){
       // Player1Label.text = "${this.} scored ${this.collectedCardsStack.size} points."
       // Player2Label.text = "${this.playerName} scored ${this.collectedCardsStack.size} points."
       // Player3Label.text = "${this.playerName} scored ${this.collectedCardsStack.size} points."
        //Player4Label.text = "${this.playerName} scored ${this.collectedCardsStack.size} points."
    }


}