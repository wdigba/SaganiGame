package view

import Location
import edu.udo.cs.sopra.ntf.ConnectionState
import entity.Color
import entity.Player
import entity.PlayerType
import service.RootService
import tools.aqua.bgw.components.uicomponents.CheckBox
import tools.aqua.bgw.components.uicomponents.ComboBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * A [MenuScene] for configuration of network games.
 */
class NetworkScene(private val rootService: RootService) :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Enter your data",
        font = Font(size = 22)
    )

    private val headlineWaitingForGuestsLabel = Label(
        width = 350, height = 50, posX = 20, posY = 50,
        text = "[Session ID]: Waiting for guests...",
        font = Font(size = 22)
    ).apply {
        isVisible = false
    }

    private val nameLabel = Label(
        width = 100, height = 35,
        posX = 50, posY = 125,
        text = "Name:"
    )

     val nameInput: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 160
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
        onKeyTyped = {
            hostGameButton.isDisabled = this.text.trim() == ""
            joinGameButton.isDisabled = this.text.trim() == "" || sessionIDInput.text.trim() == ""
        }
    }

    private val sessionIDLabel = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
        text = "Your ID:"
    )
   val sessionIDInput: TextField = TextField(
        width = 150, height = 35,
        posX = 70, posY = 240
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
        onKeyTyped = {
            joinGameButton.isDisabled = this.text.trim() == "" || nameInput.text.trim() == ""
        }
    }

    val backButton = StandardButton(
        posX = 50, posY = 465,
        width = 280,
        text = "Go Back",
    ).apply {
        onMouseClicked = {
            rootService.networkService.disconnect()
            for (input in playerInput) {
                input.first.text = ""
            }
        }
    }

    val exitButton = StandardButton(
        posX = 50, posY = 465,
        width = 280,
        text = "Exit game",
    ).apply {
        isVisible = false
        isDisabled = false
        onMouseClicked = {
            rootService.networkService.disconnect()
            resetScene()
        }
    }

    private var sessionID = ""

    /**
     * Starts the game.
     */
    val startButton = StandardButton(
        posX = 50, posY = 415,
        width = 280,
        text = "Start",
    ).apply {
        isVisible = false
        onMousePressed = {
            println("Test 2")
            playerComponents.sortBy { it.orderComboBox.selectedItem!! }
            val players = playerComponents.map {
                val name = it.nameLabel.text.trim()
                val color = Color.valueOf(it.colorComboBox.selectedItem!!.uppercase())
                val selectedPlayerType = it.playerTypeComboBox.selectedItem!!
                val playerType = when (selectedPlayerType) {
                    "Human" -> PlayerType.HUMAN
                    "Random AI" -> PlayerType.RANDOM_AI
                    "Smart AI" -> PlayerType.BEST_AI
                    "Network Player" -> PlayerType.NETWORK_PLAYER
                    else -> error("Unknown Player type $selectedPlayerType")
                }
                Triple(name, color, playerType)
            }

            rootService.gameService.startNewGame(players)
        }
    }

    val randomizeButton = CheckBox(
        posX = 50, posY = 365,
        width = 30,
        text = "Randomize",
        isChecked = false
    ).apply {
        isVisible = false
        isCheckedProperty.addListener { _, newValue ->
            startButton.isDisabled = !isStartAvailable()
        }
    }

    val hostGameButton = StandardButton(
        posX = 50, posY = 415,
        width = 280,
        text = "Host Game"
    ).apply {
        isDisabled = true
        onMouseClicked = {
            val name = nameInput.text.trim()
            val id = if (sessionIDInput.text.trim() == "") null else sessionIDInput.text.trim()
            disableJoinAndHostButtons()
            rootService.networkService.hostGame(name, id)
        }
    }

    val joinGameButton = StandardButton(
        posX = 50, posY = 365,
        width = 280,
        text = "Join Game"
    ).apply {
        isDisabled = true
        onMouseClicked = {
            val name = nameInput.text.trim()
            val id = sessionIDInput.text.trim()
            disableJoinAndHostButtons()
            rootService.networkService.joinGame(name, id)
        }
    }

    private val playerInput = mutableListOf(Pair(nameInput, sessionIDInput))

    init {
        opacity = 1.0
        startButton.isDisabled = true
        background = ColorVisual(GameColor.cornSilk)
        addComponents(
            headlineLabel, nameLabel, nameInput,
            sessionIDInput, sessionIDLabel,
            startButton, backButton,
            joinGameButton, hostGameButton,
            exitButton, headlineWaitingForGuestsLabel,
            randomizeButton
        )
    }

    private fun resetScene() {
        headlineLabel.isVisible = true
        headlineWaitingForGuestsLabel.isVisible = false
        nameInput.isVisible = true
        nameLabel.isVisible = true
        sessionIDInput.isVisible = true
        sessionIDLabel.isVisible = true
        startButton.isVisible = true
        backButton.isVisible = true
        joinGameButton.isVisible = true
        hostGameButton.isVisible = true
        startButton.isDisabled = true
        backButton.isDisabled = false
        joinGameButton.isDisabled = true
        hostGameButton.isDisabled = true
        exitButton.isVisible = false
        randomizeButton.isVisible = false
    }

    private val playerComponents = mutableListOf<PlayerComponent>()

    private data class PlayerComponent(
        val nameLabel: Label,
        val colorComboBox: ComboBox<String>,
        val playerTypeComboBox: ComboBox<String>,
        val orderComboBox: ComboBox<Int>
    )

    fun drawPlayers(names: List<String>) {
        // Delete old components
        for (player in playerComponents) {
            removeComponents(player.nameLabel, player.colorComboBox, player.playerTypeComboBox, player.orderComboBox)
        }
        playerComponents.clear()

        // Create new components
        names.forEachIndexed { index, name ->
            val nameLabel = Label(
                posX = 0, posY = 100 + 50 * index,
                width = 100, text = name
            )
            val colors = ComboBox<String>(
                posX = 80, posY = 100 + 50 * index,
                width = 90
            ).apply {
                items = listOf("White", "Brown", "Grey", "Black")
                selectedItem = items[index]
                selectedItemProperty.addListener { _, _ ->
                    startButton.isDisabled = !isStartAvailable()
                }
            }

            val playerType = ComboBox<String>(
                posX = 200, posY = 100 + 50 * index,
                width = 120
            ).apply {
                items = if (nameInput.text.trim() == name) {
                    listOf("Human", "Random AI", "Smart AI")
                } else {
                    listOf("Network Player")
                }
                selectedItem = if (nameInput.text.trim() == name) "Human" else "Network Player"
            }

            val order = ComboBox<Int>(
                posX = 340, posY = 100 + 50 * index,
                width = 25
            ).apply {
                val items = mutableListOf(1)
                for (i in 2..names.size) {
                    items += i
                }
                this.items = items
                this.selectedItem = index + 1
                selectedItemProperty.addListener { _, _ ->
                    startButton.isDisabled = !isStartAvailable()
                }
            }

            addComponents(nameLabel, colors, playerType, order)
            playerComponents += PlayerComponent(nameLabel, colors, playerType, order)
        }
    }

    override fun refreshAfterConnectionStateChange(newState: ConnectionState) {
        if (newState == ConnectionState.WAITING_FOR_GUESTS || newState == ConnectionState.WAITING_FOR_INIT) {
            joinGameButton.isVisible = false
            hostGameButton.isVisible = false
            nameLabel.isVisible = false
            nameInput.isVisible = false
            sessionIDLabel.isVisible = false
            sessionIDInput.isVisible = false
            backButton.isVisible = false
            headlineLabel.isVisible = false
            sessionID = rootService.networkService.client!!.sessionID!!
            headlineWaitingForGuestsLabel.isVisible = true
            exitButton.isVisible = true
        }

        when (newState) {
            ConnectionState.WAITING_FOR_GUESTS -> {
                headlineWaitingForGuestsLabel.text = "$sessionID: Waiting for guests"
                drawPlayers(listOf(rootService.networkService.client!!.playerName))
                startButton.isVisible = true
                randomizeButton.isVisible = true
            }

            ConnectionState.WAITING_FOR_INIT -> {
                headlineWaitingForGuestsLabel.text = "$sessionID: Waiting for host"
            }

            else -> {}
        }
    }

    override fun refreshAfterStartNewGame(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {
        //TODO: Change to game scene
        resetScene()
    }

    private fun disableJoinAndHostButtons() {
        joinGameButton.isDisabled = true
        hostGameButton.isDisabled = true
    }

    /**
     * Check if all players have a unique color and order, if it's not randomized.
     */
    private fun isStartAvailable(): Boolean {
        val uniqueColors =
            playerComponents.map { it.colorComboBox.selectedItem }.distinct().size == playerComponents.size
        val uniqueOrder =
            playerComponents.map { it.orderComboBox.selectedItem }.distinct().size == playerComponents.size
        println("uniqueColors: $uniqueColors, uniqueOrder: $uniqueOrder")
        return uniqueColors && (uniqueOrder || randomizeButton.isChecked) && playerComponents.size > 1
    }


    override fun refreshAfterPlayerListChange(currentPlayers: List<String>) {
        if (rootService.networkService.connectionState == ConnectionState.WAITING_FOR_GUESTS) {
            drawPlayers(currentPlayers)
            startButton.isDisabled = !isStartAvailable()
        }
    }

}