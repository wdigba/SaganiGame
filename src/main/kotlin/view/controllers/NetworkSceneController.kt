package view.controllers

import service.RootService
import view.Refreshable
import view.SaganiApplication
import view.scene.NetworkScene

/**
 * Controller for the [NetworkScene].
 */
class NetworkSceneController(
    private val networkScene: NetworkScene,
    private val rootService: RootService,
    private val saganiApplication: SaganiApplication,
) : Refreshable {

    private val playerInput = mutableListOf(Pair(networkScene.nameInput, networkScene.sessionIDInput))

    init {


        networkScene.nameInput.apply {
            onKeyTyped = {
                networkScene.startButton.isDisabled = startIsAvailable()
            }
        }

        networkScene.sessionIDInput.apply {
            onKeyTyped = {
                networkScene.startButton.isDisabled = startIsAvailable()
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
                playerNames.removeIf() { it.isBlank() }
                playerNames.removeIf() { it.isEmpty() }

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