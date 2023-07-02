package view

import service.RootService
import tools.aqua.bgw.core.BoardGameApplication
import view.scene.ConfigurationScene
import view.scene.NewGameMenuScene
import view.scene.SaganiGameScene

class SaganiApplication : BoardGameApplication("SoPra Game") {
    private val rootService = RootService()


    private val playerConfigScene = PlayerConfigScene()


    private val playerConfigSceneController: PlayerConfigSceneController =
        PlayerConfigSceneController(playerConfigScene, rootService, this).apply {

            //TODO: nur zu Testzwecken
            playerConfigScene.startButton.onMouseClicked = {
                this@SaganiApplication.hideMenuScene()
            }
            playerConfigScene.backButton.onMouseClicked = {
                this@SaganiApplication.showMenuScene(configurationScene)
            }

        }
    private val NetworkConfigScene: NetworkScene = NetworkScene(rootService).apply {

        //TODO: nur zu Testzwecken
        startButton.onMouseClicked = {
            this@SaganiApplication.hideMenuScene()
        }
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(configurationScene)
        }

    }


    private val configurationScene: ConfigurationScene = ConfigurationScene(rootService).apply {
        playersButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(playerConfigScene)
        }
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(newGameMenuScene)
        }
        networkButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(NetworkConfigScene)
        }
    }
    private val saganiGameScene: SaganiGameScene = SaganiGameScene(rootService)
    private val ruleScene: RuleScene = RuleScene(rootService)

    private val newGameMenuScene: NewGameMenuScene = NewGameMenuScene(rootService).apply {
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
    }

}

