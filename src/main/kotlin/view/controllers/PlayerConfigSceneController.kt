package view.controllers

import entity.Color
import entity.PlayerType
import service.RootService
import tools.aqua.bgw.components.uicomponents.ComboBox
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.scene.PlayerConfigScene

/**
 * Controller class of [PlayerConfigScene].
 */
class PlayerConfigSceneController(
    private val playerConfigScene: PlayerConfigScene,
    private val rootService: RootService,
) : Refreshable {

    /**
     * List contains player inputs. If a player is added/removed in GUI the input is added/removed to the list accordingly.
     * This happens in [repositionButtonsMinus]/[repositionButtonsPlus].
     */

    private val playerInputs: MutableList<Pair<TextField, ComboBox<String>>> = mutableListOf(
        Pair(
            playerConfigScene.player1Input,
            playerConfigScene.comboBox1
        ),
        Pair(
            playerConfigScene.player2Input,
            playerConfigScene.comboBox2
        )
    )


    /*
private var playerInputs = mutableListOf(
Triple(
    playerConfigScene.player1Input,
    playerConfigScene.comboBox1,
    playerConfigScene.comboBoxKI1.selectedItem.toString()
),
Triple(
    playerConfigScene.player2Input,
    playerConfigScene.comboBox2,
    playerConfigScene.comboBoxKI2.selectedItem.toString()
)
)*/

    private val comboBoxes = mutableListOf(
        playerConfigScene.comboBox1,
        playerConfigScene.comboBox2,
        playerConfigScene.comboBox3,
        playerConfigScene.comboBox4
    )

    private val comboBoxColors = mutableListOf<String>()

    init {

        // init player type Combo boxes
        playerConfigScene.comboBoxKI1.items = playerConfigScene.comboBoxKIArt
        playerConfigScene.comboBoxKI1.selectedItemProperty.addListener { _, _ ->
            playerConfigScene.startButton.isDisabled = !startIsAvailable()
        }

        playerConfigScene.comboBoxKI2.items = playerConfigScene.comboBoxKIArt
        playerConfigScene.comboBoxKI2.selectedItemProperty.addListener { _, _ ->
            playerConfigScene.startButton.isDisabled = !startIsAvailable()
        }
        playerConfigScene.comboBoxKI3.items = playerConfigScene.comboBoxKIArt
        playerConfigScene.comboBoxKI3.selectedItemProperty.addListener { _, _ ->
            playerConfigScene.startButton.isDisabled = !startIsAvailable()
        }
        playerConfigScene.comboBoxKI4.items = playerConfigScene.comboBoxKIArt
        playerConfigScene.comboBoxKI4.selectedItemProperty.addListener { _, _ ->
            playerConfigScene.startButton.isDisabled = !startIsAvailable()
        }

        // init Color Combo boxes
        playerConfigScene.comboBox1.items = comboBoxColors
        playerConfigScene.comboBox1.selectedItemProperty.addListener { _, newValue ->
            playerConfigScene.comboBox1.visual = returnColorVisualFromString(newValue)
            playerConfigScene.startButton.isDisabled = !startIsAvailable()
        }


        playerConfigScene.comboBox2.items = comboBoxColors
        playerConfigScene.comboBox2.selectedItemProperty.addListener { _, newValue ->
            playerConfigScene.comboBox2.visual = returnColorVisualFromString(newValue)
            playerConfigScene.startButton.isDisabled = !startIsAvailable()
        }

        playerConfigScene.comboBox3.items = comboBoxColors
        playerConfigScene.comboBox3.selectedItemProperty.addListener { _, newValue ->
            playerConfigScene.comboBox3.visual = returnColorVisualFromString(newValue)
            playerConfigScene.startButton.isDisabled = !startIsAvailable()
        }

        playerConfigScene.comboBox4.items = comboBoxColors
        playerConfigScene.comboBox4.selectedItemProperty.addListener { _, newValue ->
            playerConfigScene.comboBox4.visual = returnColorVisualFromString(newValue)
            playerConfigScene.startButton.isDisabled = !startIsAvailable()
        }

        // Farben aus der Entity Schicht übernehmen
        Color.values().forEach {
            comboBoxColors.add(it.toString())
        }


        playerConfigScene.startButton.apply {
            onMouseClicked = {
                //neues Spiel starten
                rootService.gameService.startNewGame(getInfosFromScene())
            }
        }

        playerConfigScene.backButton.apply {
            onMouseClicked = {
                for (input in playerInputs) {
                    input.first
                }
            }
        }

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
                createRandomNames()
            }
        }
    }

    private fun createRandomNames() {
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


        // get a copy of comboBoxColors to not delete any colors in comboBoxColors
        val colors = comboBoxColors.toMutableList()
        colors.shuffle()

        for (input in playerInputs) {
            // Names
            input.first.text = "${randomAdjectives.removeFirst()} ${randomNames.removeFirst()}"
            // Colors

            input.second.items = colors
            colors.removeFirst()
        }
        playerConfigScene.startButton.isDisabled = !startIsAvailable()
    }

    private val playerTypes = mutableListOf(
        playerConfigScene.comboBoxKI1,
        playerConfigScene.comboBoxKI2,
        playerConfigScene.comboBoxKI3,
        playerConfigScene.comboBoxKI4
    )

    private fun getInfosFromScene(): MutableList<Triple<String, Color, PlayerType>> {

        val playerInfos: MutableList<Triple<String, Color, PlayerType>> = mutableListOf()

        // Convert PlayerInputs to necessary input for startNewGame
        var playerType = 0
        for (input in playerInputs) {

            playerInfos.add(
                Triple(
                    input.first.text,
                    getPlayerEntityColorFromString(input.second.selectedItem),
                    getPlayerTypeFromString(playerTypes[playerType].selectedItem.toString())
                )
            )

            playerType++
        }


        return playerInfos

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
        for (comboBox in comboBoxes) {
            comboBox.items = comboBoxColors
            comboBox.selectedItemProperty.addListener { _, newValue ->
                comboBox.visual = returnColorVisualFromString(newValue)
                playerConfigScene.startButton.isDisabled = !startIsAvailable()
            }
        }
    }

    private fun repositionButtonsPlus() {
        if (!playerConfigScene.player3Label.isVisible) {
            playerConfigScene.minusButton.isVisible = true
            playerConfigScene.plusButton.reposition(500, 320)
            playerConfigScene.player3Label.isVisible = true
            playerConfigScene.player3Input.isVisible = true
            playerConfigScene.color3Label.isVisible = true
            playerConfigScene.comboBox3.isVisible = true
            playerConfigScene.comboBoxKI3.isVisible = true
            playerConfigScene.kI3Label.isVisible = true
            playerInputs.add(
                Pair(
                    playerConfigScene.player3Input,
                    playerConfigScene.comboBox3
                )
            )
            playerConfigScene.startButton.isDisabled = !startIsAvailable()

        } else if (!playerConfigScene.player4Label.isVisible) {
            playerConfigScene.minusButton.reposition(550, 400)
            playerConfigScene.player4Input.isVisible = true
            playerConfigScene.player4Label.isVisible = true
            playerConfigScene.color4Label.isVisible = true
            playerConfigScene.comboBox4.isVisible = true
            playerConfigScene.plusButton.isVisible = false
            playerConfigScene.comboBoxKI4.isVisible = true
            playerConfigScene.kI4Label.isVisible = true
            playerInputs.add(
                Pair(
                    playerConfigScene.player4Input,
                    playerConfigScene.comboBox4
                )
            )
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
            playerConfigScene.comboBoxKI4.isVisible = false
            playerConfigScene.kI4Label.isVisible = false
            playerInputs.remove(
                Pair(
                    playerConfigScene.player4Input,
                    playerConfigScene.comboBox4
                )
            )
            playerConfigScene.minusButton.reposition(550, 320)
            playerConfigScene.plusButton.reposition(500, 320)
            playerConfigScene.plusButton.isVisible = true
            playerConfigScene.startButton.isDisabled = !startIsAvailable()
        } else if (playerConfigScene.player3Label.isVisible) {
            playerConfigScene.player3Label.isVisible = false
            playerConfigScene.player3Input.text = ""
            playerConfigScene.player3Input.isVisible = false
            playerConfigScene.color3Label.isVisible = false
            playerConfigScene.comboBox3.isVisible = false
            playerConfigScene.comboBoxKI3.isVisible = false
            playerConfigScene.kI3Label.isVisible = false
            playerInputs.remove(
                Pair(
                    playerConfigScene.player3Input,
                    playerConfigScene.comboBox3
                )
            )
            playerConfigScene.minusButton.isVisible = false
            playerConfigScene.plusButton.reposition(500, 240)
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
                if (input.second == null)
                    return false
            }
        }
        // Player Colors all different
        val colors = arrayListOf<String>()
        for (comboBox in playerInputs.map { it.second }) {
            if (comboBox != null) {
                colors.add(comboBox.selectedItem.toString())
            }
        }
        return colors.size == colors.distinct().size
    }

    private fun returnColorVisualFromString(color: String?): ColorVisual {
        checkNotNull(color) { "No color selected." }
        return when (color) {
            "WHITE" -> ColorVisual(GameColor.white)
            "GREY" -> ColorVisual(GameColor.gray)
            "BROWN" -> ColorVisual(GameColor.brown)
            "BLACK" -> ColorVisual(GameColor.black)
            else -> {
                ColorVisual(GameColor.white)
            }
        }
    }

    private fun getPlayerTypeFromString(playerType: String?): PlayerType {
        checkNotNull(playerType) { "No playerType selected" }

        return when (playerType) {
            "Human" -> PlayerType.HUMAN
            "Random AI" -> PlayerType.RANDOM_AI
            "Best AI" -> PlayerType.BEST_AI
            else -> {
                PlayerType.HUMAN
            }
        }

    }

    private fun getPlayerEntityColorFromString(color: String?): Color {
        checkNotNull(color) { "No color selected." }
        return when (color) {
            "WHITE" -> entity.Color.WHITE
            "GREY" -> entity.Color.GREY
            "BROWN" -> entity.Color.BROWN
            "BLACK" -> entity.Color.BLACK
            else -> {
                entity.Color.WHITE
            }
        }

    }
}

