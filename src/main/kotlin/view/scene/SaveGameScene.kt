package view.scene

import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import view.GameColor
import view.Refreshable
import view.StandardButton

/**
 * Custom [MenuScene] for the save game menu.
 */
class SaveGameScene : MenuScene(400, 1080), Refreshable {

    val saveGameButton = StandardButton(
        posX = 100, posY = 200,
        text = "Save game"
    )

    val backButton = StandardButton(
        posX = 100, posY = 300,
        text = "Go Back"
    )

    val pathInput = TextField(
        posX = 50, posY = 100, width = 300, height = 50
    )

    init {
        opacity = 1.0
        background = ColorVisual(GameColor.cornSilk)
        addComponents(
            Label(
                width = 300, height = 50, posX = 50, posY = 50,
                text = "Save your game",
                font = Font(size = 22)
            ),
            pathInput, saveGameButton, backButton
        )
    }

    fun checkValidPath(path: String): String {
        // for future implementation
        return path
    }
}
