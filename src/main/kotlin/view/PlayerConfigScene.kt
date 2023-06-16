package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import java.awt.Color

/**
 * TODO:
 * - Farben (Nils)
 * - Shuffle Spielerreihenfolge Button (wird auch angezeigt) (Nils)
 * - Random Farben
 * - Random Namen ( Tutoren Name)
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
        onKeyTyped = {
            startButton.isDisabled = checkIfStartIsAvailable()
        }
    }

    private val outputLabel = Label(
        posX = 230,
        posY = 160,
        width = 50,
        text = "I am",
        alignment = Alignment.CENTER,
        isWrapText = true
    )
    private val comboBox1 =
        ComboBox<Double>(posX = 230, posY = 160, width = 50, prompt = "Select an option!")
    private val comboBox2 =
        ComboBox<Double>(posX = 230, posY = 240, width = 50, prompt = "Select an option!")
    private val comboBox3 =
        ComboBox<Double>(posX = 230, posY = 320, width = 50, prompt = "Select an option!")
    private val comboBox4 =
        ComboBox<Double>(posX = 230, posY = 400, width = 50, prompt = "Select an option!")


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
        onKeyTyped = {
            startButton.isDisabled = checkIfStartIsAvailable()
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
        onKeyTyped = {
            startButton.isDisabled = checkIfStartIsAvailable()
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
        onKeyTyped = {
            startButton.isDisabled = checkIfStartIsAvailable()
        }
    }


    /**
     * List contains player inputs. If a player is added/removed in GUI the input is added/removed to the list accordingly.
     * This happens in [repositionButtonsMinus]/[repositionButtonsPlus].
     */
    private val playerInputs = mutableListOf(player1Input, player2Input)

    /**
     * Button leads to the previous MenuScene.
     */
    val backButton = Button(
        width = 140, height = 35,
        posX = 50, posY = 465,
        text = "Go Back",
        font = Font(size = 18, color = Color.white)
    ).apply {
        visual = ColorVisual(96, 108, 56)
        onMouseClicked = {
            for (input in playerInputs) {
                input.text = ""
            }

        }
    }

    /**
     * Starts the game.
     */
    val startButton = Button(
        width = 140, height = 35,
        posX = 230, posY = 465,
        text = "Start",
        font = Font(size = 18, color = Color.white)
    ).apply {
        visual = ColorVisual(96, 108, 56)
        onMouseClicked = {
            val playerNames = mutableListOf<String>()
            for (input in playerInputs) {
                playerNames.add(input.text.trim())
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
        visual = ColorVisual(200, 202, 167)
        onMouseClicked = {
            repositionButtonsPlus()
        }
    }

    private val minusButton = Button(
        width = 35, height = 35,
        posX = 360, posY = 320,
        text = "-", font = Font(size = 20), alignment = Alignment.CENTER
    ).apply {
        visual = ColorVisual(200, 202, 167)
        onMouseClicked = {
            repositionButtonsMinus()
        }
    }


    /**
     * creates random names with random adjectives and adds them to the inputs accordingly.
     */
    private val randomNamesButton = Button(
        width = 200, height = 35,
        posX = 50, posY = 520,
        text = "Random Names", font = Font(size = 18, color = Color.WHITE), alignment = Alignment.CENTER
    ).apply {
        visual = ColorVisual(96, 108, 56)
        onMouseClicked = {
            val randomNames = mutableListOf(
                "Till", "Marc", "Luka", "Sven", "Nick", "Friedemann", "Moritz", "Stefan", "Kai", "Vadym",
                "Nils", "Marie", "Niklas", "Polina", "Christian", "Torben", "Daniel", "Noah", "Karina"
            )
            val randomAdjectives = mutableListOf(
                "awesome", "brilliant", "clumsy", "aggressive", "scary",
                "amazing", "bored", "weird", "ambitious"
            )
            for (input in playerInputs) {
                input.text = randomAdjectives.random() + " " + randomNames.random()
            }
            startButton.isDisabled = checkIfStartIsAvailable()
        }
    }

    init {
        background = ColorVisual(254, 250, 224)
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

        comboBox1.formatFunction = {
            "${it.toString()}"
        }
        comboBox1.items = mutableListOf(0.0, 0.1, 0.4, 0.2)
        comboBox1.selectedItemProperty.addListener { _, newValue ->
            outputLabel.text = "Combo box selection is : $newValue"
        }
        comboBox2.formatFunction = {
            "${it.toString()}"
        }
        comboBox2.items = mutableListOf(0.0, 0.1, 0.4, 0.2)
        comboBox2.selectedItemProperty.addListener { _, newValue ->
            outputLabel.text = "Combo box selection is : $newValue"
        }
        comboBox3.formatFunction = {
            "${it.toString()}"
        }
        comboBox3.items = mutableListOf(0.0, 0.1, 0.4, 0.2)
        comboBox3.selectedItemProperty.addListener { _, newValue ->
            outputLabel.text = "Combo box selection is : $newValue"
        }
        comboBox4.formatFunction = {
            "${it.toString()}"
        }
        comboBox4.items = mutableListOf(0.0, 0.1, 0.4, 0.2)
        comboBox4.selectedItemProperty.addListener { _, newValue ->
            outputLabel.text = "Combo box selection is : $newValue"
        }

        opacity = .5
        addComponents(
            headlineLabel,
            player1Label, player1Input, color1Label, comboBox1,
            player2Label, player2Input, color2Label, comboBox2,
            player3Label, player3Input, color3Label, comboBox3,
            player4Label, player4Input, color4Label, comboBox4,
            startButton, backButton, plusButton, minusButton,
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
            playerInputs.add(player3Input)

        } else if (!player4Label.isVisible) {
            minusButton.reposition(360, 400)
            player4Input.isVisible = true
            player4Label.isVisible = true
            color4Label.isVisible = true
            comboBox4.isVisible = true
            plusButton.isVisible = false
            playerInputs.add(player4Input)
        }
    }

    private fun repositionButtonsMinus() {
        plusButton.isDisabled = false
        // blendet Felder aus
        if (player4Label.isVisible) {
            player4Label.isVisible = false
            player4Input.text = ""
            player4Input.isVisible = false
            color4Label.isVisible = false
            comboBox4.isVisible = false
            playerInputs.remove(player4Input)
            minusButton.reposition(360, 320)
            plusButton.reposition(320, 320)
            plusButton.isVisible = true
        } else if (player3Label.isVisible) {
            player3Label.isVisible = false
            player3Input.text = ""
            player3Input.isVisible = false
            color3Label.isVisible = false
            comboBox3.isVisible = false
            playerInputs.remove(player3Input)
            minusButton.isVisible = false
            plusButton.reposition(320, 240)
            plusButton.isVisible = true
        }
    }

    /**
     * checks if one of the player inputs are empty. If so, the startbutton stays disabled.
     * If none of the player inputs is empty, the start button is enabled.
     */
    private fun checkIfStartIsAvailable(): Boolean {
        for (input in playerInputs) {
            if (input.text == "") {
                return true
            }
        }
        return false
    }

}