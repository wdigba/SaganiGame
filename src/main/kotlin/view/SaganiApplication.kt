package view

import Location
import entity.Player
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication
import view.controllers.*
import view.scene.*

/**
 * The main application class that starts the game.
 */
class SaganiApplication : BoardGameApplication("SoPra Game"), Refreshable {
    private val rootService = RootService()

    private val playerConfigScene = PlayerConfigScene()
    private val networkScene = NetworkScene(rootService)
    private val saganiGameScene = SaganiGameScene()

    private val loadGameScene = LoadGameScene(rootService)
    private val saveGameScene = SaveGameScene(rootService)


    private val loadGameSceneController: LoadGameSceneController = LoadGameSceneController(loadGameScene, rootService).apply {
        loadGameScene.backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(configurationScene)
        }
    }

    private val saveGameSceneController = SaveGameSceneController(saveGameScene, rootService).apply {
        saveGameScene.backButton.onMouseClicked = {
            this@SaganiApplication.hideMenuScene()
        }
        saveGameScene.saveGameButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(newGameMenuScene)
        }
    }


    private val playerConfigSceneController: PlayerConfigSceneController =
        PlayerConfigSceneController(playerConfigScene, rootService, this).apply {


            playerConfigScene.backButton.onMouseClicked = {
                this@SaganiApplication.showMenuScene(configurationScene)
            }

        }
    private val networkConfigSceneController: NetworkSceneController =
        NetworkSceneController(networkScene, rootService, this).apply {

            networkScene.backButton.onMouseClicked = {
                this@SaganiApplication.showMenuScene(configurationScene)
            }
        }

    private val configurationScene: ConfigurationScene = ConfigurationScene().apply {
        playersButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(playerConfigScene)
        }
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(newGameMenuScene)
        }
        networkButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(networkScene)
        }
        loadGameButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(loadGameScene)
        }
    }

    private val scoreScene = ScoreScene(rootService).apply {
        backButton.onMouseClicked = {
            this@SaganiApplication.hideMenuScene()
        }

    }

    private val saganiGameSceneController: SaganiGameSceneController =
        SaganiGameSceneController(saganiGameScene, rootService).apply {
            saganiGameScene.scoreButton.onMouseClicked = {
                this@SaganiApplication.showMenuScene(scoreScene)
            }
            saganiGameScene.saveGameButton.onMouseClicked = {
                this@SaganiApplication.showMenuScene(saveGameScene)
            }
        }

    private val ruleScene: RuleScene = RuleScene(rootService).apply {
        backButton.apply {
            onMouseClicked = { this@SaganiApplication.showMenuScene(newGameMenuScene) }
        }
    }

    private val newGameMenuScene: NewGameMenuScene = NewGameMenuScene().apply {
        playWithKIButton.onMouseClicked = {
            hideMenuScene()
            this@SaganiApplication.showGameScene(saganiGameScene)
        }
        playWithOthersButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(configurationScene)
        }
        ruleButton.onMouseClicked = {
            hideMenuScene()
            this@SaganiApplication.showMenuScene(ruleScene)
        }
        quitButton.onMouseClicked = {
            exit()
        }
    }

    init {
        this.showMenuScene(newGameMenuScene)
        this.showGameScene(saganiGameScene)
        rootService.addEachRefreshable(
            this,
            networkConfigSceneController,
            playerConfigSceneController,
            saganiGameSceneController,
            loadGameSceneController,
            saveGameSceneController
        )
    }

    override fun refreshAfterStartNewGame(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {
        this@SaganiApplication.hideMenuScene()
    }

    override fun refreshAfterLoadGame() {

    }
}

