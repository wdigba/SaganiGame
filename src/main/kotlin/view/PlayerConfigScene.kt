package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * TODO: Random Farben - Nils*
 */

class PlayerConfigScene(private val rootService: RootService) :
    MenuScene(400, 1080), Refreshable {

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
        posX = 230, posY = 125,
        text = "Color:"
    )

    private val player1Input: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 160
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
        onKeyTyped = {
            startButton.isDisabled = !startIsAvailable()
        }
    }

    private val outputLabel1 = Label(
        posX = 230,
        posY = 160,
        width = 50,
        text = " ",
        alignment = Alignment.CENTER,
        isWrapText = true
    )

    val comboBoxColors = mutableListOf("White", "Grey", "Brown", "Black")

    private val comboBox1 =
        ComboBox<String>(posX = 230, posY = 160, width = 70)

    private val outputLabel2 = Label(
        posX = 230,
        posY = 240,
        width = 50,
        text = " ",
        alignment = Alignment.CENTER,
        isWrapText = true
    )
    private val comboBox2 =
        ComboBox<String>(posX = 230, posY = 240, width = 70, prompt = " ")

    private val outputLabel3 = Label(
        posX = 230,
        posY = 320,
        width = 50,
        text = " ",
        alignment = Alignment.CENTER,
        isWrapText = true
    )

    private val comboBox3 =
        ComboBox<String>(posX = 230, posY = 320, width = 70, prompt = " ")
    private val outputLabel4 = Label(
        posX = 230,
        posY = 400,
        width = 50,
        text = " ",
        alignment = Alignment.CENTER,
        isWrapText = true
    )
    private val comboBox4 =
        ComboBox<String>(posX = 230, posY = 400, width = 70, prompt = " ")


    private val player2Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
        text = "Player 2:"
    )
    private val color2Label = Label(
        width = 100, height = 35,
        posX = 230, posY = 205,
        text = "Color:"
    )

    private val player2Input: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 240
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
        onKeyTyped = {
            startButton.isDisabled = !startIsAvailable()
        }
    }

    private val player3Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 285,
        text = "Player 3:"
    )
    private val color3Label = Label(
        width = 100, height = 35,
        posX = 230, posY = 285,
        text = "Color:"
    )

    private val player3Input: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 320
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
        onKeyTyped = {
            startButton.isDisabled = !startIsAvailable()
        }
    }

    private val player4Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 365,
        text = "Player 4:"
    )
    private val color4Label = Label(
        width = 100, height = 35,
        posX = 230, posY = 365,
        text = "Color:"
    )

    private val player4Input: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 400
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
        onKeyTyped = {
            startButton.isDisabled = !startIsAvailable()
        }
    }

    /**
     * List contains player inputs. If a player is added/removed in GUI the input is added/removed to the list accordingly.
     * This happens in [repositionButtonsMinus]/[repositionButtonsPlus].
     */
    private val playerInputs = mutableListOf(Pair(player1Input, outputLabel1), Pair(player2Input, outputLabel2))

    /**
     * Button leads to the previous MenuScene.
     */
    val backButton = StandardButton(
        posX = 50, posY = 465,
        width = 140,
        text = "Go Back",
    ).apply {
        onMouseClicked = {
            for (input in playerInputs) {
                input.first.text = ""
            }
        }
    }

    /**
     * Starts the game.
     */
    val startButton = StandardButton(
        posX = 230, posY = 465,
        width = 140,
        text = "Start",
    ).apply {
        onMouseClicked = {
            val playerNames = mutableListOf<String>()
            for (input in playerInputs) {
                playerNames.add(input.first.text.trim())
            }

            // leere Werte entfernen
            playerNames.removeIf() { it.isBlank() }
            playerNames.removeIf() { it.isEmpty() }

            //neues Spiel starten
            //rootService.gameService.startNewGame()
        }
    }

    private val plusButton = Button(
        width = 35, height = 35,
        posX = 320, posY = 240,
        text = "+", font = Font(size = 14), alignment = Alignment.CENTER
    ).apply {
        visual = ColorVisual(Color.paleLeaf)
        onMouseClicked = {
            repositionButtonsPlus()
        }
    }

    private val minusButton = Button(
        width = 35, height = 35,
        posX = 360, posY = 320,
        text = "-", font = Font(size = 20), alignment = Alignment.CENTER
    ).apply {
        visual = ColorVisual(Color.paleLeaf)
        onMouseClicked = {
            repositionButtonsMinus()
        }
    }

    /**
     * creates random names with random adjectives and adds them to the inputs accordingly.
     */
    private val randomNamesButton = StandardButton(
        posX = 50, posY = 520,
        text = "Random Names"
    ).apply {
        onMouseClicked = {
            val randomNames = mutableListOf(
                "Till", "Marc", "Luka", "Sven", "Nick", "Friedemann", "Moritz", "Stefan", "Kai", "Vadym",
                "Nils", "Marie", "Niklas", "Polina", "Christian", "Torben", "Daniel", "Noah", "Karina"
            )
            randomNames.shuffle()
            val randomAdjectives = mutableListOf(
                "Awesome", "Brilliant", "Clumsy", "Aggressive", "Scary",
                "Amazing", "Bored", "Weird", "Ambitious"
            )

            randomAdjectives.shuffle()

            val colors = comboBoxColors.toMutableList()
            colors.shuffle()

            for (input in playerInputs) {

                // Names
                input.first.text = randomAdjectives.removeFirst() + " " + randomNames.removeFirst()
                // Colors
                input.second.text = colors.removeFirst()

            }
            startButton.isDisabled = !startIsAvailable()
        }
    }

    init {
        background = ColorVisual(Color.cornSilk)
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

        comboBox1.items = comboBoxColors
        comboBox1.selectedItemProperty.addListener { _, newValue ->
            outputLabel1.text = "$newValue"
            startButton.isDisabled = !startIsAvailable()
        }

        comboBox2.items = comboBoxColors
        comboBox2.selectedItemProperty.addListener { _, newValue ->
            outputLabel2.text = "$newValue"
            startButton.isDisabled = !startIsAvailable()
        }

        comboBox3.items = comboBoxColors
        comboBox3.selectedItemProperty.addListener { _, newValue ->
            outputLabel3.text = "$newValue"
            startButton.isDisabled = !startIsAvailable()
        }

        comboBox4.items = comboBoxColors
        comboBox4.selectedItemProperty.addListener { _, newValue ->
            outputLabel4.text = "$newValue"
            startButton.isDisabled = !startIsAvailable()
        }

        opacity = 1.0
        addComponents(
            headlineLabel,
            player1Label, player1Input, color1Label, comboBox1,
            player2Label, player2Input, color2Label, comboBox2,
            player3Label, player3Input, color3Label, comboBox3,
            player4Label, player4Input, color4Label, comboBox4,
            startButton, backButton, plusButton, minusButton, outputLabel1,
            outputLabel2, outputLabel3, outputLabel4,
            randomNamesButton
        )
    }


    private fun repositionButtonsPlus() {
        if (!player3Label.isVisible) {
            minusButton.isVisible = true
            plusButton.reposition(320, 320)
            player3Label.isVisible = true
            player3Input.isVisible = true
            color3Label.isVisible = true
            comboBox3.isVisible = true
            playerInputs.add(Pair(player3Input, outputLabel3))
            startButton.isDisabled = !startIsAvailable()

        } else if (!player4Label.isVisible) {
            minusButton.reposition(360, 400)
            player4Input.isVisible = true
            player4Label.isVisible = true
            color4Label.isVisible = true
            comboBox4.isVisible = true
            plusButton.isVisible = false
            playerInputs.add(Pair(player4Input, outputLabel4))
            startButton.isDisabled = !startIsAvailable()
        }
    }

    private fun repositionButtonsMinus() {
        plusButton.isDisabled = false
        // blendet Felder aus
        if (player4Label.isVisible) {
            player4Label.isVisible = false
            player4Input.text = ""
            outputLabel4.text = ""
            player4Input.isVisible = false
            color4Label.isVisible = false
            comboBox4.isVisible = false
            playerInputs.remove(Pair(player4Input, outputLabel4))
            minusButton.reposition(360, 320)
            plusButton.reposition(320, 320)
            plusButton.isVisible = true
            startButton.isDisabled = !startIsAvailable()
        } else if (player3Label.isVisible) {
            player3Label.isVisible = false
            player3Input.text = ""
            outputLabel3.text = ""
            player3Input.isVisible = false
            color3Label.isVisible = false
            comboBox3.isVisible = false
            playerInputs.remove(Pair(player3Input, outputLabel3))
            minusButton.isVisible = false
            plusButton.reposition(320, 240)
            plusButton.isVisible = true
            startButton.isDisabled = !startIsAvailable()
        }
    }

    /**
     * checks if one of the player inputs are empty. If so, the startbutton stays disabled.
     * If none of the player inputs is empty, the start button is enabled.
     */
    private fun startIsAvailable(): Boolean {
        // Player Names not empty
        for (input in playerInputs) {
            if (input.first.text == "") {
                return false
            }
        }
        // If player has name color must not be empty
        for (input in playerInputs) {
            if (input.first.text.trim() != "") {
                if (input.second.text.trim() == "")
                    return false
            }
        }
        // Player Colors all different
        val colors = arrayListOf<String>()
        for (outputLabel in playerInputs.map { it.second }) {
            if (outputLabel.text.trim() != "") {
                colors.add(outputLabel.text)
            }
        }
        return colors.size == colors.distinct().size
    }
}