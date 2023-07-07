package view.controllers

import service.RootService
import view.*
import view.scene.LoadGameScene
import java.io.File
import java.nio.file.Paths

/**
 * Controller for the [LoadGameScene].
 */
class LoadGameSceneController(
    private val loadGameScene: LoadGameScene,
    private val rootService: RootService,
) : Refreshable {

    init {
        loadGameScene.loadGameButton.onMouseClicked = {
            val game = checkNotNull(rootService.gameService)
            val path = checkValidPath(loadGameScene.pathInput.text)
            game.loadGame(path)
        }
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
