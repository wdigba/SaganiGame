package view.scene

import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.StandardButton

/**
 * [NetworkInitiateOrJoinScene] offers possibility to choose between two options, join someone's game or start own game.
 */

class NetworkInitiateOrJoinScene :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "What do you want to do?",
        font = Font(size = 22)
    )

    val initiateButton = StandardButton(
        posX = 100, posY = 200,
        text = "Initiate game",
    )

    val joinButton = StandardButton(
        posX = 100, posY = 300,
        text = "Join game",
    )


    val backButton = StandardButton(
        posX = 50, posY = 465,
        width = 140,
        text = "Go Back",
    )

    init {
        opacity = 1.0
        background = ColorVisual(GameColor.cornSilk)
        addComponents(headlineLabel, initiateButton, joinButton, backButton)
    }

}
