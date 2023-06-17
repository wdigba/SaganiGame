package view

import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

class SaganiApplication : BoardGameApplication("SoPra Game") {
    private val rootService = RootService()

    private val playerConfigScene: PlayerConfigScene = PlayerConfigScene(rootService).apply {

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
    }
    private val saganiGameScene: SaganiGameScene = SaganiGameScene(rootService)
    private val ruleScene: RuleScene = RuleScene(rootService)
    private val newGameMenuScene: NewGameMenuScene = NewGameMenuScene(rootService).apply {
        kIButton.onMouseClicked = {
            hideMenuScene()
            this@SaganiApplication.showGameScene(saganiGameScene)
        }
        standardButton.onMouseClicked = {
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

