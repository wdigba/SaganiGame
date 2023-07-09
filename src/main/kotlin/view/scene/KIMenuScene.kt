package view.scene

import tools.aqua.bgw.components.uicomponents.ComboBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.StandardButton

/**
 * Custom [MenuScene] for the KI menu.
 */
class KIMenuScene :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Choose the way to play",
        font = Font(size = 22)
    )

    private val nameLabel = Label(
        width = 100,
        height = 35,
        posX = 50,
        posY = 125,
        text = "Name:"
    )
    val nameInput: TextField = TextField(
        width = 150,
        height = 35,
        posX = 70,
        posY = 160
    )

    val backButton = StandardButton(
        posX = 50, posY = 465,
        width = 140,
        text = "Go Back",
    )
    //random , smart

    /**
     * Starts the game.
     */
    val startButton = StandardButton(
        posX = 230, posY = 465,
        width = 140,
        text = "Start",
    )

    /**
     * checks, if  it is possible to start the game
     */

    private val kIArt = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
        text = "Choose KI:"
    )

    val comboBoxKIArt = mutableListOf("Random", "Smart")

    val kIInput =
        ComboBox<String>(posX = 70, posY = 240, width = 150, height = 35)


    init {
        opacity = 1.0
        background = ColorVisual(GameColor.cornSilk)

        addComponents(
            headlineLabel, nameLabel, nameInput,
            kIInput, kIArt,
            startButton, backButton
        )
    }
}
