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
    private val networkConfigScene: NetworkScene = NetworkScene(rootService).apply {

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
        networkButton.onMouseClicked ={
            this@SaganiApplication.showMenuScene(networkConfigScene)
        }
    }
    private val saganiGameScene: GridPaneVersuch = GridPaneVersuch(rootService)
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

    private val scoreScene = ScoreScene(rootService).apply {
        backButton.onMouseClicked = {
            hideMenuScene()
            this@SaganiApplication.showGameScene(saganiGameScene)
        }
        boardButtons.forEach {
            it.onMouseClicked = {
                hideMenuScene()
                // show chosen board scene
            }
        }
    }

    init {
        this.showMenuScene(newGameMenuScene)
        this.showGameScene(saganiGameScene)
        rootService.addEachRefreshable(networkConfigScene)
    }

}

