package view.controllers

import service.RootService
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.SaganiApplication
import view.scene.PlayerConfigScene

class PlayerConfigSceneController(
    private val playerConfigScene: PlayerConfigScene,
    private val rootService: RootService,
    private val saganiApplication: SaganiApplication,
) : Refreshable {


    /**
     * List contains player inputs. If a player is added/removed in GUI the input is added/removed to the list accordingly.
     * This happens in [repositionButtonsMinus]/[repositionButtonsPlus].
     */
    private val playerInputs = mutableListOf(
        Pair(playerConfigScene.player1Input, playerConfigScene.comboBox1),
        Pair(playerConfigScene.player2Input, playerConfigScene.comboBox2)
    )

    private val comboBoxes = mutableListOf(
        playerConfigScene.comboBox1,
        playerConfigScene.comboBox2,
        playerConfigScene.comboBox3,
        playerConfigScene.comboBox4
    )
   private val comboBoxColors = mutableListOf("White", "Gray", "Brown", "Black")

    init {
        playerConfigScene.startButton.apply {
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

        playerConfigScene.backButton.apply {
            onMouseClicked = {
                for (input in playerInputs) {
                    input.first.text = ""
                }
            }
        }

        /*
        playerConfigScene.player1Input.apply {
            onKeyTyped = {
                playerConfigScene.startButton.isDisabled = !startIsAvailable()
            }
        }

        playerConfigScene.player2Input.apply {
            onKeyTyped = {
                playerConfigScene.startButton.isDisabled = !startIsAvailable()
            }
        }

        playerConfigScene.player3Input.apply {
            onKeyTyped = {
                playerConfigScene.startButton.isDisabled = !startIsAvailable()
            }
        }*/

        initPlayerLabels()
        initComboBoxes()



        playerConfigScene.plusButton.apply {
            onMouseClicked = {
                repositionButtonsPlus()
            }
        }

        playerConfigScene.minusButton.apply {
            onMouseClicked = {
                repositionButtonsMinus()
            }
        }

        playerConfigScene.randomNamesButton.apply {
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
                playerConfigScene.startButton.isDisabled = !startIsAvailable()
            }
        }
    }

    /**
     * Automatisiert:
     * playerConfigScene.player4Input.apply {
    onKeyTyped = {
    playerConfigScene.startButton.isDisabled = !startIsAvailable()
    }
    }
     */
    private fun initPlayerLabels() {
        for (input in playerInputs) {
            input.first.apply {
                onKeyTyped = {
                    playerConfigScene.startButton.isDisabled = !startIsAvailable()
                }
            }
        }
    }

    private fun initComboBoxes() {
        for (comboBox in comboBoxes){
            comboBox.items = comboBoxColors
            comboBox.selectedItemProperty.addListener{_,newValue ->
                comboBox.visual = returnColorFromString(newValue)
                playerConfigScene.startButton.isDisabled = !startIsAvailable()
            }
        }
    }

    private fun repositionButtonsPlus() {
        if (!playerConfigScene.player3Label.isVisible) {
            playerConfigScene.minusButton.isVisible = true
            playerConfigScene.plusButton.reposition(320, 320)
            playerConfigScene.player3Label.isVisible = true
            playerConfigScene.player3Input.isVisible = true
            playerConfigScene.color3Label.isVisible = true
            playerConfigScene.comboBox3.isVisible = true
            playerInputs.add(Pair(playerConfigScene.player3Input, playerConfigScene.comboBox3))
            playerConfigScene.startButton.isDisabled = !startIsAvailable()

        } else if (!playerConfigScene.player4Label.isVisible) {
            playerConfigScene.minusButton.reposition(360, 400)
            playerConfigScene.player4Input.isVisible = true
            playerConfigScene.player4Label.isVisible = true
            playerConfigScene.color4Label.isVisible = true
            playerConfigScene.comboBox4.isVisible = true
            playerConfigScene.plusButton.isVisible = false
            playerInputs.add(Pair(playerConfigScene.player4Input, playerConfigScene.comboBox4))
            playerConfigScene.startButton.isDisabled = !startIsAvailable()
        }
    }


    private fun repositionButtonsMinus() {
        playerConfigScene.plusButton.isDisabled = false
        // blendet Felder aus
        if (playerConfigScene.player4Label.isVisible) {
            playerConfigScene.player4Label.isVisible = false
            playerConfigScene.player4Input.text = ""
            playerConfigScene.player4Input.isVisible = false
            playerConfigScene.color4Label.isVisible = false
            playerConfigScene.comboBox4.isVisible = false
            playerInputs.remove(Pair(playerConfigScene.player4Input, playerConfigScene.comboBox4))
            playerConfigScene.minusButton.reposition(360, 320)
            playerConfigScene.plusButton.reposition(320, 320)
            playerConfigScene.plusButton.isVisible = true
            playerConfigScene.startButton.isDisabled = !startIsAvailable()
        } else if (playerConfigScene.player3Label.isVisible) {
            playerConfigScene.player3Label.isVisible = false
            playerConfigScene.player3Input.text = ""
            playerConfigScene.player3Input.isVisible = false
            playerConfigScene.color3Label.isVisible = false
            playerConfigScene.comboBox3.isVisible = false
            playerInputs.remove(Pair(playerConfigScene.player3Input, playerConfigScene.comboBox3))
            playerConfigScene.minusButton.isVisible = false
            playerConfigScene.plusButton.reposition(320, 240)
            playerConfigScene.plusButton.isVisible = true
            playerConfigScene.startButton.isDisabled = !startIsAvailable()
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

    private fun returnColorFromString(color: String?): ColorVisual {
        checkNotNull(color) { "No color selected." }
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