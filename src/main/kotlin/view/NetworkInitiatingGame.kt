package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * [NetworkInitiatingGame] creates scene for initiating game
 */

class NetworkInitiatingGame(private val rootService: RootService) :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Create a key for the game",
        font = Font(size = 22)
    )

    private val NameLabel = Label(
        width = 100, height = 35,
        posX = 50, posY = 125,
        text = "Name:"
    )
    private val NameInput: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 160
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
        onKeyTyped = {
            startButton.isDisabled = !startIsAvailable()
        }
    }

    private val KeyLabel = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
        text = "Key:"
    )
    private val KeyInput: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 240
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
        onKeyTyped = {
            startButton.isDisabled = !startIsAvailable()
        }
    }

    val backButton = StandardButton(
        posX = 50, posY = 465,
        width = 140,
        text = "Go Back",
    ).apply {
        onMouseClicked = {
            for (input in playerInput) {
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
            for (input in playerInput) {
                playerNames.add(input.first.text.trim())
            }

            // leere Werte entfernen
            playerNames.removeIf() { it.isBlank() }
            playerNames.removeIf() { it.isEmpty() }

            //neues Spiel starten
            //rootService.gameService.startNewGame()
        }
    }

    /**
     * checks, if  it is possible to start the game
     */
    private fun startIsAvailable(): Boolean {
        // Player Name is not empty
        for (input in playerInput) {
            if (input.first.text.trim() == "") {
                return false
            }
        }
        // If player has name ID must not be empty
        for (input in playerInput) {
            if (input.first.text.trim() != "") {
                if (input.second.text.trim() == "")
                    return false
            }
        }
        return true
    }

    private val playerInput = mutableListOf( Pair(NameInput, KeyInput))

    /**
     * buttton to generate a key
     */

    private val randomIDButton = StandardButton(
        posX = 50, posY = 520,
        text = "Random ID"
    ).apply {
        onMouseClicked = {

            val randomID = mutableListOf(
                "2efkenekvew9", "90hnoertt889", "cnwleri22", "1vm0r9fh90n", "45tgbjo0uttn"
            )

            randomID.shuffle()



            for (input in playerInput) {

                // ID
                input.second.text = "${randomID.removeFirst()}"


            }
            startButton.isDisabled = !startIsAvailable()
        }
    }

    init {
        opacity = 1.0
        startButton.isDisabled = true
        background = ColorVisual(GameColor.cornSilk)
        addComponents(headlineLabel, NameLabel, NameInput,
            KeyInput, KeyLabel,
            startButton, backButton, randomIDButton)
    }


}