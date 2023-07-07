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


    fun checkValidPath(path: String): String {
        val file = File(path)

        if (!file.exists() || !file.isFile) {
            throw IllegalArgumentException("Path does not exist or is not a file: $path")
        }

        return Paths.get(path).toAbsolutePath().toString()
    }
}
