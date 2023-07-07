package view.scene


import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.StandardButton

/**
 * [NetworkInitiatingGameScene] creates scene for initiating game
 */

class NetworkInitiatingGameScene:
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Create a key for the game",
        font = Font(size = 22)
    )

    private val nameLabel = Label(
        width = 100, height = 35,
        posX = 50, posY = 125,
        text = "Name:"
    )
     val nameInput: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 160
    )

    private val keyLabel = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
        text = "Key:"
    )
     val keyInput: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 240
    )

    val backButton = StandardButton(
        posX = 50, posY = 465,
        width = 140,
        text = "Go Back",
    )

    /**
     * Starts the game.
     */
    val startButton = StandardButton(
        posX = 230, posY = 465,
        width = 140,
        text = "Start",
    )

    /**
     * button to generate a key
     */
     val randomIDButton = StandardButton(
        posX = 50, posY = 520,
        text = "Random ID"
    )

    init {
        opacity = 1.0
        startButton.isDisabled = true
        background = ColorVisual(GameColor.cornSilk)
        addComponents(headlineLabel, nameLabel, nameInput,
            keyInput, keyLabel,
            startButton, backButton, randomIDButton)
    }


}