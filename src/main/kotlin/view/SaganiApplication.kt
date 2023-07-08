package view

import Location
import entity.Player
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication
import view.controllers.*
import view.scene.*

class SaganiApplication : BoardGameApplication("SoPra Game"), Refreshable {
    private val rootService = RootService()

    private val playerConfigScene = PlayerConfigScene()
    private val networkScene = NetworkScene()
    private val saganiGameScene = SaganiGameScene()
    private val kiMenuScene = KIMenuScene()
    private val networkWaitingForPlayers = NetworkWaitingForPlayersScene(rootService)
    private val scoreScene = ScoreScene()

    private val endScene: EndScene = EndScene(rootService).apply {
        quitButton.onMouseClicked = {
            exit()
        }
    }


    private val initiatingGame: NetworkInitiatingGameScene = NetworkInitiatingGameScene().apply {
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(networkInitiateOrJoin)
        }
    }


    private val networkInitiateOrJoin = NetworkInitiateOrJoinScene().apply {
        joinButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(networkWaitingForPlayers)
        }
        initiateButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(initiatingGame)
        }
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(configurationScene)
        }
    }

    private val playerConfigSceneController: PlayerConfigSceneController =
        PlayerConfigSceneController(playerConfigScene, rootService).apply {


            playerConfigScene.backButton.onMouseClicked = {
                this@SaganiApplication.showMenuScene(configurationScene)
            }

        }

    private val networkConfigSceneController: NetworkSceneController =
        NetworkSceneController(networkScene).apply {

            networkScene.backButton.onMouseClicked = {
                this@SaganiApplication.showMenuScene(configurationScene)
            }
        }

    private val kiMenuSceneController: KIMenuSceneController = KIMenuSceneController(kiMenuScene).apply {

    }


    private val configurationScene: ConfigurationScene = ConfigurationScene().apply {
        playersButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(playerConfigScene)
        }
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(newGameMenuScene)
        }
        networkButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(networkInitiateOrJoin)
        }
    }
    private val kIMenuScene: KIMenuScene = KIMenuScene().apply {
        startButton.onMouseClicked = {

        }
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(newGameMenuScene)
        }
    }


    private val scoreSceneController = ScoreSceneController(scoreScene, rootService).apply {
        scoreScene.backButton.onMouseClicked = {
            this@SaganiApplication.hideMenuScene()
        }
    }

    private val saganiGameSceneController: SaganiGameSceneController =
        SaganiGameSceneController(saganiGameScene, rootService).apply {
            saganiGameScene.scoreButton.onMouseClicked = {
                this@SaganiApplication.showMenuScene(scoreScene)
            }
        }

    private val ruleScene: RuleScene = RuleScene(rootService).apply {
        backButton.apply {
            onMouseClicked = { this@SaganiApplication.showMenuScene(newGameMenuScene) }
        }
    }

    private val newGameMenuScene: NewGameMenuScene = NewGameMenuScene().apply {

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
            //networkConfigSceneController,
            playerConfigSceneController,
            saganiGameSceneController,
            scoreSceneController
        )
    }

    override fun refreshAfterStartNewGame(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {
        this@SaganiApplication.hideMenuScene()
    }
}

