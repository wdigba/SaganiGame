package view.controllers

import service.RootService
import view.*
import view.scene.SaveGameScene

/**
 * Controller for the [SaveGameScene].
 */
class SaveGameSceneController(
    private val saveGameScene: SaveGameScene,
    private val rootService: RootService,
) : Refreshable {

    init {
        saveGameScene.saveGameButton.onMouseClicked = {
            val game = checkNotNull(rootService.gameService)
            val path = checkValidPath(saveGameScene.pathInput.text)
            game.saveGame(path)
        }
    }

    private fun checkValidPath(path: String): String {
        // for future implementation
        return path
    }
}
