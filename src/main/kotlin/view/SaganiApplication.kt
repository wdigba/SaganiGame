package view

import Location
import entity.Player
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.dialog.FileDialog
import tools.aqua.bgw.dialog.FileDialogMode
import tools.aqua.bgw.visual.ColorVisual
import view.controllers.*
import view.scene.*

class SaganiApplication : BoardGameApplication("SoPra Game"), Refreshable {
    private val rootService = RootService()

    private val playerConfigScene = PlayerConfigScene()
    private val networkScene = NetworkScene()

    val newSaveGameButton = StandardButton(posX = 1400, posY = 1020, width = 80,
        height = 50,text = "Save",visual = ColorVisual.TRANSPARENT,).apply {
        visual = ColorVisual.WHITE
        onMouseClicked = {

            try {
                val filePath = showFileDialog(
                    FileDialog(
                        mode = FileDialogMode.SAVE_FILE,
                        title = "Save Sagani game",
                    )
                ).get()[0].path

                rootService.gameService.saveGame(filePath)
                this@SaganiApplication.hideMenuScene()
            } catch (e : NoSuchElementException) {}

        }
    }

    private val saganiGameScene = SaganiGameScene(newSaveGameButton)
    private val kiMenuScene = KIMenuScene()
    private val networkWaitingForPlayers = NetworkWaitingForPlayersScene(rootService)
    private val scoreScene = ScoreScene()

    private val endScene: EndScene = EndScene(rootService).apply {
        quitButton.onMouseClicked = {
            exit()
        }
        newGameButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(newGameMenuScene)
        }
    }

    //private val endSceneController : EndSceneController = EndSceneController(endScene, rootService)

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

    private val newLoadGameButton = StandardButton(posX = 100, posY = 400, text = "Load",
        visual = ColorVisual.TRANSPARENT).apply {
        visual = ColorVisual.WHITE
        onMouseClicked = {
            try {
                val filePath = showFileDialog(
                    FileDialog(
                        mode = FileDialogMode.OPEN_FILE,
                        title = "Load Sagani savegame",
                    )
                ).get()[0].path

                rootService.gameService.loadGame(filePath)
                this@SaganiApplication.hideMenuScene()
            } catch (e : NoSuchElementException) {}

        }
    }

    private val configurationScene: ConfigurationScene = ConfigurationScene(newLoadGameButton).apply {
        playersButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(playerConfigScene)
        }
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(newGameMenuScene)
        }
        networkButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(networkInitiateOrJoin)
        }
        loadGameButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(loadGameScene)
        }
    }
    private val kIMenuScene: KIMenuScene = KIMenuScene().apply {
        startButton.onMouseClicked = {

        }
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(newGameMenuScene)
        }
    }




    private val loadGameScene = LoadGameScene(newLoadGameButton).apply {
        backButton.onMouseClicked = {
            this@SaganiApplication.showMenuScene(configurationScene)
        }
        loadGameButton.onMouseClicked = {
            val game = checkNotNull(rootService.gameService)
            //val path = checkValidPath(pathInput.text)
            //game.loadGame(path)
            this@SaganiApplication.hideMenuScene()
        }
    }


    private val saveGameScene = SaveGameScene().apply {
        backButton.onMouseClicked = {
            this@SaganiApplication.hideMenuScene()
        }
        saveGameButton.onMouseClicked = {
            val game = checkNotNull(rootService.gameService)
            val path = checkValidPath(pathInput.text)
            game.saveGame(path)
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

    private val scoreSceneController = ScoreSceneController(scoreScene, rootService, saganiGameSceneController, this).apply {
        scoreScene.backButton.onMouseClicked = {
            this@SaganiApplication.hideMenuScene()
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
            scoreSceneController,
            endScene
        )
    }


    override fun refreshAfterStartNewGame(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {
        this@SaganiApplication.hideMenuScene()
    }

    override fun refreshAfterCalculateWinner() {
        this@SaganiApplication.showMenuScene(endScene)
    }
}
