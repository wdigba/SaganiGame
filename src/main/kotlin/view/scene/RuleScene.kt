package view.scene

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.StandardButton

/**
 * [RuleScene] describes generally the idea of the game
 */

class RuleScene(private val rootService: RootService) :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        posX = 25, posY = 50,
        width = 380, height = 500,
        text = "Welcome to Sagani\n" +
                "───────────────────\n" +
                "Sagani is a strategy game\n"
                + "played on a square grid board.\n" +
                "Each turn, you will choose a Spirit\n" +
                "of Nature tile and place\n" +
                "it in your personal display.\n" +
                "\n" +
                "The arrows on the tiles placed\n" +
                "indicate the needed\n" +
                "alignment of other tiles so that\n" +
                "they match in color.\n" +
                "\n" +
                "As soon as you have aligned toge-\n" +
                "ther all the arrows on the tiles \n" +
                "with the proper colored tiles,\n" +
                "you will be rewarded with points for\n" +
                "completing the task.",
        font = Font(size = 22)
    )
    val backButton = StandardButton(
        posX = 125, posY = 650,
        width = 140,
        text = "Go Back",
    )


    init {
        opacity = 1.0

        background = ColorVisual(GameColor.cornSilk)

        addComponents(headlineLabel, backButton)
    }

}
