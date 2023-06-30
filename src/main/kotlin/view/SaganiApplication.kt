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
    private val NetworkConfigScene: NetworkScene = NetworkScene(rootService).apply {

        //TODO: nur zu Testzwecken
        startButton.onMouseClicked = {
            this@SaganiApplication.hideMenuScene()
        }
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(NetworkInitiateOrJoin)
        }

    }
    private val NetworkInitiateOrJoin: NetworkInitiateOrJoin = NetworkInitiateOrJoin(rootService).apply {

        //TODO: nur zu Testzwecken
        InitiateButton.onMouseClicked={
            this@SaganiApplication.showMenuScene(NetworkInitiatingGame)
        }
        JoinButton.onMouseClicked={
            this@SaganiApplication.showMenuScene(NetworkConfigScene)
        }
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(configurationScene)
        }

    }
    private val NetworkInitiatingGame: NetworkInitiatingGame = NetworkInitiatingGame(rootService).apply {

        //TODO: nur zu Testzwecken
        startButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(NetworkWaitingForPlayers)
        }
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(NetworkInitiateOrJoin)
        }

    }
    private val NetworkWaitingForPlayers: NetworkWaitingForPlayers= NetworkWaitingForPlayers(rootService).apply {


    }


    private val configurationScene: ConfigurationScene = ConfigurationScene(rootService).apply {
        playersButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(playerConfigScene)
        }
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(newGameMenuScene)
        }
        networkButton.onMouseClicked ={
            this@SaganiApplication.showMenuScene(NetworkInitiateOrJoin)
        }
    }
    private val saganiGameScene: GridPaneVersuch = GridPaneVersuch(rootService).apply {

    }
    private val ruleScene: RuleScene = RuleScene(rootService)

    private val endScene: EndScene =EndScene(rootService).apply {
        newGameButton.onMouseClicked ={
            this@SaganiApplication.showMenuScene(newGameMenuScene)
        }
        QuitButton.onMouseClicked={
            exit()
        }
    }

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

