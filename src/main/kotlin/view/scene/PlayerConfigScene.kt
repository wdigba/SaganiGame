package view.scene

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.ComboBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.StandardButton

/**
 * Custom [MenuScene] for the player configuration menu.
 */
class PlayerConfigScene :
    MenuScene(700, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Player configuration",
        font = Font(size = 22)
    )

    private val player1Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 125,
        text = "Player 1:"
    )

    private val color1Label = Label(
        width = 100, height = 35,
        posX = 200, posY = 125,
        text = "Color:"
    )

    val player1Input: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 160
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
    }

    val comboBox1 =
        ComboBox<String>(posX = 230, posY = 160, width = 70)

    val comboBox2 =
        ComboBox<String>(posX = 230, posY = 240, width = 70)

    val comboBox3 =
        ComboBox<String>(posX = 230, posY = 320, width = 70)

    val comboBox4 =
        ComboBox<String>(posX = 230, posY = 400, width = 70)

    private val player2Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
        text = "Player 2:"
    )
    private val color2Label = Label(
        width = 100, height = 35,
        posX = 200, posY = 205,
        text = "Color:"
    )

    val player2Input: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 240
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
    }

    val player3Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 285,
        text = "Player 3:"
    )
    val color3Label = Label(
        width = 100, height = 35,
        posX = 200, posY = 285,
        text = "Color:"
    )

    val player3Input: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 320
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
    }

    val player4Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 365,
        text = "Player 4:"
    )
    val color4Label = Label(
        width = 100, height = 35,
        posX = 200, posY = 365,
        text = "Color:"
    )

    val player4Input: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 400
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
    }

    /**
     * Button leads to the previous MenuScene.
     */
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

    val plusButton = Button(
        width = 35, height = 35,
        posX = 500, posY = 240,
        text = "+", font = Font(size = 14), alignment = Alignment.CENTER
    ).apply {
        visual = ColorVisual(GameColor.paleLeaf)
    }

    val minusButton = Button(
        width = 35, height = 35,
        posX = 550, posY = 320,
        text = "-", font = Font(size = 20), alignment = Alignment.CENTER
    ).apply {
        visual = ColorVisual(GameColor.paleLeaf)
    }

    /**
     * creates random names with random adjectives and adds them to the inputs accordingly.
     */
    val randomNamesButton = StandardButton(
        posX = 50, posY = 520,
        text = "Random Names"
    )

    private val kI1Label = Label(
        width = 150, height = 35,
        posX = 320, posY = 125,
        text = "Who is playing?"
    )
    private val kI2Label = Label(
        width = 150, height = 35,
        posX = 320, posY = 205,
        text = "Who is playing?"
    )
    val kI3Label = Label(
        width = 150, height = 35,
        posX = 320, posY = 285,
        text = "Who is playing?"
    )
    val kI4Label = Label(
        width = 150, height = 35,
        posX = 320, posY = 365,
        text = "Who is playing?"
    )
    val comboBoxKIArt = mutableListOf("Human", "Random KI", "Smart KI")

    val comboBoxKI1 =
        ComboBox<String>(posX = 320, posY = 160, width = 150)
    val comboBoxKI2 =
        ComboBox<String>(posX = 320, posY = 240, width = 150)
    val comboBoxKI3 =
        ComboBox<String>(posX = 320, posY = 320, width = 150)
    val comboBoxKI4 =
        ComboBox<String>(posX = 320, posY = 400, width = 150)


    init {
        background = ColorVisual(GameColor.cornSilk)
        player3Label.isVisible = false
        player3Input.isVisible = false
        player4Input.isVisible = false
        player4Label.isVisible = false
        minusButton.isVisible = false
        startButton.isDisabled = true
        color3Label.isVisible = false
        color4Label.isVisible = false
        comboBox3.isVisible = false
        comboBox4.isVisible = false
        kI3Label.isVisible = false
        comboBoxKI3.isVisible = false
        kI4Label.isVisible = false
        comboBoxKI4.isVisible = false


        opacity = 1.0
        addComponents(
            headlineLabel,
            player1Label, player1Input, color1Label, comboBox1,
            player2Label, player2Input, color2Label, comboBox2,
            player3Label, player3Input, color3Label, comboBox3,
            player4Label, player4Input, color4Label, comboBox4,
            startButton, backButton, plusButton, minusButton,
            randomNamesButton,
            comboBoxKI1, comboBoxKI2, comboBoxKI3, comboBoxKI4,
            kI1Label, kI2Label, kI3Label, kI4Label
        )
    }

}
