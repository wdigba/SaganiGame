package view.controllers

import view.Refreshable
import view.scene.NetworkScene

/**
 * Controller for the [NetworkScene].
 */
class NetworkSceneController(
    private val networkScene: NetworkScene
) : Refreshable {

    private val playerInput = mutableListOf(Pair(networkScene.nameInput, networkScene.iDInput))


    init {


        networkScene.nameInput.apply {
            componentStyle = "-fx-background-color: #C8CAA7"
            onKeyTyped = {
                networkScene.startButton.isDisabled = startIsAvailable()
            }
        }

        networkScene.iDInput.apply {
            componentStyle = "-fx-background-color: #C8CAA7"
            onKeyTyped = {
                networkScene.startButton.isDisabled = !startIsAvailable()
            }
        }

        networkScene.backButton.apply {
            onMouseClicked = {
                for (input in playerInput) {
                    input.first.text = ""
                }
            }
        }

        networkScene.startButton.apply {
            onMouseClicked = {
                val playerNames = mutableListOf<String>()
                for (input in playerInput) {
                    playerNames.add(input.first.text.trim())
                }

                // leere Werte entfernen
                playerNames.removeIf { it.isBlank() }
                playerNames.removeIf { it.isEmpty() }

                //neues Spiel starten
                //rootService.gameService.startNewGame()
            }
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

}
