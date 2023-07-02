package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

class NetworkScene(private val rootService: RootService) :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Enter your data",
        font = Font(size = 22)
    )

    private val nameLabel = Label(
        width = 100, height = 35,
        posX = 50, posY = 125,
        text = "Name:"
    )
    private val nameInput: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 160
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
        onKeyTyped = {
            startButton.isDisabled = !startIsAvailable()
        }
    }

    private val iDLabel = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
        text = "Your ID:"
    )
    private val iDInput: TextField = TextField(
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

    private val playerInput = mutableListOf( Pair(nameInput, iDInput))

    init {
        opacity = 1.0
        startButton.isDisabled = true
        background = ColorVisual(GameColor.cornSilk)
        addComponents(headlineLabel, nameLabel, nameInput,
            iDInput, iDLabel,
            startButton, backButton)
    }

}