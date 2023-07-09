package view.controllers

import view.scene.NetworkInitiatingGameScene

/**
 * Controller for the [NetworkInitiatingGameScene].
 */
class NetworkInitiatingGameSceneController(private val networkInitiatingGameScene: NetworkInitiatingGameScene) {

    private val playerInput =
        mutableListOf(Pair(networkInitiatingGameScene.nameInput, networkInitiatingGameScene.keyInput))


    init {

        networkInitiatingGameScene.nameInput.apply {
            componentStyle = "-fx-background-color: #C8CAA7"
            onKeyTyped = {
                networkInitiatingGameScene.startButton.isDisabled = !startIsAvailable()
            }
        }

        networkInitiatingGameScene.keyInput.apply {
            componentStyle = "-fx-background-color: #C8CAA7"
            onKeyTyped = {
                networkInitiatingGameScene.startButton.isDisabled = !startIsAvailable()
            }
        }

        networkInitiatingGameScene.backButton.apply {
            onMouseClicked = {
                for (input in playerInput) {
                    input.first.text = ""
                }
            }
        }

        networkInitiatingGameScene.startButton.apply {
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

        networkInitiatingGameScene.randomIDButton.apply {
            onMouseClicked = {

                val randomID = mutableListOf(
                    "2efkenekvew9", "90hnoertt889", "cnwleri22", "1vm0r9fh90n", "45tgbjo0uttn"
                )

                randomID.shuffle()



                for (input in playerInput) {
                    // ID
                    input.second.text = randomID.removeFirst()
                }


                networkInitiatingGameScene.startButton.isDisabled = !startIsAvailable()
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
