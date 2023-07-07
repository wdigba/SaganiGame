package view

import Location
import entity.Player
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication
import view.controllers.NetworkSceneController
import view.controllers.PlayerConfigSceneController
import view.controllers.SaganiGameSceneController
import view.scene.*

class SaganiApplication : BoardGameApplication("SoPra Game"), Refreshable {
    private val rootService = RootService()

    private val playerConfigScene = PlayerConfigScene()
    private val networkScene = NetworkScene()
    private val saganiGameScene = SaganiGameScene()



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
    }

    private val scoreScene = ScoreScene(rootService).apply {
        backButton.onMouseClicked={
            this@SaganiApplication.hideMenuScene()
        }
    }

    private val saganiGameSceneController: SaganiGameSceneController =
        SaganiGameSceneController(saganiGameScene, rootService).apply {
            saganiGameScene.scoreButton.onMouseClicked = {
                this@SaganiApplication.showMenuScene(scoreScene)
            }
        }

    private val ruleScene: RuleScene = RuleScene(rootService)

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
            this@SaganiApplication.showGameScene(ruleScene)
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
            saganiGameSceneController
        )
    }

    override fun refreshAfterStartNewGame(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {
        this@SaganiApplication.hideMenuScene()
    }
}

