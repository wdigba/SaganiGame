package view.scene

import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.StandardButton
import java.io.File
import java.nio.file.Paths

/**
 * Custom [MenuScene] for the load game menu.
 */
class LoadGameScene :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Load Game",
        font = Font(size = 22)
    )

    private val pathLabel = Label(
        width = 300, height = 50, posX = 50, posY = 125,
        text = "Enter path:"
    )

    val pathInput: TextField = TextField(
        width = 300, height = 50,
        posX = 50, posY = 175
    ).apply {
        componentStyle = "-fx-background-color: #C8CAA7"
    }

    /**
     * Button to load the game.
     */
    val loadGameButton = StandardButton(
        posX = 50, posY = 235,
        width = 140,
        text = "Load Game",
    )

    /**
     * Button leads to the previous MenuScene.
     */
    val backButton = StandardButton(
        posX = 200, posY = 235,
        width = 140,
        text = "Go Back",
    )

    init {
        background = ColorVisual(GameColor.cornSilk)
        opacity = 1.0

        addComponents(
            headlineLabel,
            pathLabel,
            pathInput,
            loadGameButton,
            backButton
        )
    }

    /**
     * Check if the given path is valid.
     */
    fun checkValidPath(path: String): String {
        val file = File(path)

        require(file.exists()) { "Path does not exist" }
        require(file.isFile) { "Path does not point to a file" }

        return Paths.get(path).toAbsolutePath().toString()
    }
}
