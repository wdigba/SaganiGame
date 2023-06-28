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
        posX = 200, posY = 125,
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

    val comboBoxColors = mutableListOf("White", "Gray", "Brown", "Black")

    private val comboBox1 =
        ComboBox<String>(posX = 230, posY = 160, width = 70)

    private val comboBox2 =
        ComboBox<String>(posX = 230, posY = 240, width = 70)

    private val comboBox3 =
        ComboBox<String>(posX = 230, posY = 320, width = 70)

    private val comboBox4 =
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
        posX = 200, posY = 285,
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
        posX = 200, posY = 365,
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
    private val playerInputs = mutableListOf(Pair(player1Input, comboBox1), Pair(player2Input, comboBox2))

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
        visual = ColorVisual(GameColor.paleLeaf)
        onMouseClicked = {
            repositionButtonsPlus()
        }
    }

    private val minusButton = Button(
        width = 35, height = 35,
        posX = 360, posY = 320,
        text = "-", font = Font(size = 20), alignment = Alignment.CENTER
    ).apply {
        visual = ColorVisual(GameColor.paleLeaf)
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
                input.first.text = "${randomAdjectives.removeFirst()} ${randomNames.removeFirst()}"
                // Colors
                input.second.selectedItem = colors.removeFirst()

            }
            startButton.isDisabled = !startIsAvailable()
        }
    }

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

        comboBox1.items = comboBoxColors
        comboBox1.selectedItemProperty.addListener { _, newValue ->
            comboBox1.visual = returnColorfromString(newValue)
            startButton.isDisabled = !startIsAvailable()
        }


        comboBox2.items = comboBoxColors
        comboBox2.selectedItemProperty.addListener { _, newValue ->
            comboBox2.visual = returnColorfromString(newValue)
            startButton.isDisabled = !startIsAvailable()
        }

        comboBox3.items = comboBoxColors
        comboBox3.selectedItemProperty.addListener { _, newValue ->
            comboBox3.visual = returnColorfromString(newValue)
            startButton.isDisabled = !startIsAvailable()
        }

        comboBox4.items = comboBoxColors
        comboBox4.selectedItemProperty.addListener { _, newValue ->
            comboBox4.visual = returnColorfromString(newValue)
            startButton.isDisabled = !startIsAvailable()
        }

        opacity = 1.0
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
            playerInputs.add(Pair(player3Input, comboBox3))
            startButton.isDisabled = !startIsAvailable()

        } else if (!player4Label.isVisible) {
            minusButton.reposition(360, 400)
            player4Input.isVisible = true
            player4Label.isVisible = true
            color4Label.isVisible = true
            comboBox4.isVisible = true
            plusButton.isVisible = false
            playerInputs.add(Pair(player4Input, comboBox4))
            startButton.isDisabled = !startIsAvailable()
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
            playerInputs.remove(Pair(player4Input, comboBox4))
            minusButton.reposition(360, 320)
            plusButton.reposition(320, 320)
            plusButton.isVisible = true
            startButton.isDisabled = !startIsAvailable()
        } else if (player3Label.isVisible) {
            player3Label.isVisible = false
            player3Input.text = ""
            player3Input.isVisible = false
            color3Label.isVisible = false
            comboBox3.isVisible = false
            playerInputs.remove(Pair(player3Input, comboBox3))
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
            if (input.first.text.trim() == "") {
                return false
            }
        }
        // If player has name color must not be empty
        for (input in playerInputs) {
            if (input.first.text.trim() != "") {
                if (input.second.selectedItem == null)
                    return false
            }
        }
        // Player Colors all different
        val colors = arrayListOf<String>()
        for (comboBox in playerInputs.map { it.second }) {
            if (comboBox.selectedItem != null) {
                colors.add(comboBox.selectedItem!!)
            }
        }
        return colors.size == colors.distinct().size
    }

    private fun returnColorfromString(color: String?): ColorVisual {
        checkNotNull(color){"No color selected."}
        when (color) {
            "White" -> return ColorVisual(GameColor.white)
            "Gray" -> return ColorVisual(GameColor.gray)
            "Brown" -> return ColorVisual(GameColor.brown)
            "Black" -> return ColorVisual(GameColor.black)
            else -> {
                return ColorVisual(GameColor.white)
            }
        }
    }

}