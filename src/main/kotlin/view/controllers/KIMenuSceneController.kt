package view.controllers

import view.scene.KIMenuScene

class KIMenuSceneController(private val kiMenuScene: KIMenuScene) {

    private val playerInput = mutableListOf( Pair(kiMenuScene.nameInput, kiMenuScene.kIInput))

    init {
        kiMenuScene.startButton.isDisabled = true

        kiMenuScene.kIInput.items = kiMenuScene.comboBoxKIArt
        kiMenuScene.kIInput.selectedItemProperty.addListener { _, newValue ->
            kiMenuScene.startButton.isDisabled = false
        }



        kiMenuScene.nameInput.apply {
            componentStyle = "-fx-background-color: #C8CAA7"
            onKeyTyped = {
                kiMenuScene.startButton.isDisabled = !startIsAvailable()
            }
        }

        kiMenuScene.backButton.apply {
            onMouseClicked = {
                for (input in playerInput) {
                    input.first.text = ""
                }
            }
        }

        kiMenuScene.startButton.apply {
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
                if (input.second.selectedItem != ""){
                    return false}
            }
        }
        return true
    }


}