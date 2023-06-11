package view

import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

class SopraApplication : BoardGameApplication("SoPra Game") {
    private val rootService = RootService()

    private val playerConfigScene = PlayerConfigScene(rootService)
    private val configurationScene = ConfigurationScene(rootService).apply {
        playersButton.onMouseClicked = {
            this@SopraApplication.showMenuScene(playerConfigScene)
        }
    }
    private val saganiGameScene = SaganiGameScene(rootService)
    private val ruleScene = RuleScene(rootService)
    private val newGameMenuScene = NewGameMenuScene(rootService).apply {
        kIButton.onMouseClicked = {
            hideMenuScene()
            this@SopraApplication.showGameScene(saganiGameScene)
        }
        playButton.onMouseClicked = {
            this@SopraApplication.showMenuScene(configurationScene)
        }
        ruleButton.onMouseClicked = {
            hideMenuScene()
            this@SopraApplication.showGameScene(ruleScene)
        }
    }

    init {
        this.showMenuScene(newGameMenuScene)
        this.showGameScene(saganiGameScene)
    }


}

