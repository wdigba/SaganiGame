package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

// TODO: PlayButton Klasse erstellen


class ConfigurationScene(private val rootService: RootService) :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Welcome to Sagani",
        font = Font(size = 22)
    )

    val networkButton = StandardButton(
        posX = 100, posY = 200,
        text = "Network game"
    ).apply {
        componentStyle = "-fx-background-color: #606C38; -fx-background-radius: 15px"
    }

    val playersButton = StandardButton(
        posX = 100, posY = 300,
        text = "Add players",
    ).apply {
        componentStyle = "-fx-background-color: #606C38; -fx-background-radius: 15px"
    }

    val backButton = StandardButton(
        posX = 100, posY = 400,
        text = "Go Back",
    ).apply {
        componentStyle = "-fx-background-color: #606C38; -fx-background-radius: 15px"
    }

    init {
        opacity = 1.0
        background = ColorVisual(Color.cornSilk)
        addComponents(
            headlineLabel,
            networkButton, playersButton, backButton
        )

    }
}