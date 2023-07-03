package view.controllers

import service.RootService
import view.Refreshable
import view.SaganiApplication
import view.scene.SaganiGameScene
import Location
import entity.Color
import entity.PlayerType
import entity.Tile
import tools.aqua.bgw.event.KeyCode
import view.PossibleMovements
import view.ZoomLevels
import view.ZoomLevels.*
import kotlin.math.max
import kotlin.math.min

class SaganiGameSceneController(
    private val saganiGameScene: SaganiGameScene,
    private val rootService: RootService,
    private val saganiApplication: SaganiApplication
) : Refreshable {

    private var board: MutableMap<Location, Tile>
    private var possibleMovements = mutableListOf<PossibleMovements>()

    private var loadedBoardTiles = mutableMapOf<Location, Tile>()

    private var selectedTile: Tile
    private var selectedTilePlacement = saganiGameScene.sampleTile
    private var tilePlaced = false
    private var currentZoom = LEVEL1

    private var chosenOfferDisplay = -1

    init {

        val playerList = mutableListOf<Triple<String, Color, PlayerType>>(
            Triple("Marie", Color.BLACK, PlayerType.HUMAN),
            Triple("Nils", Color.BROWN, PlayerType.HUMAN),
            Triple("Polina", Color.GREY, PlayerType.HUMAN)
        )

        rootService.gameService.startNewGame(playerList)


        val game = checkNotNull(rootService.currentGame) { "There is no game." }
        board = game.actPlayer.board
        selectedTile = game.offerDisplay[0]

        saganiGameScene.confirmButton.apply {
            onMouseClicked = {
                confirmPlacement()
            }
        }

        saganiGameScene.zoomInButton.apply {
            onMouseClicked = {
                println(ZoomLevels.values()[min(ZoomLevels.values().size - 1, currentZoom.ordinal + 1)])
                currentZoom =
                    ZoomLevels.values()[min(ZoomLevels.values().size - 1, currentZoom.ordinal + 1)]
                saganiGameScene.tilePane.scale(currentZoom.scale)
            }
        }
        saganiGameScene.zoomOutButton.apply {
            onMouseClicked = {
                currentZoom = ZoomLevels.values()[max(0, currentZoom.ordinal - 1)]
                saganiGameScene.tilePane.scale(currentZoom.scale)
            }
        }

        // TODO: Relation zum Ausschnitt des fensters herstellen?
        saganiGameScene.moveUpButton.apply {
            onMouseClicked = {
                saganiGameScene.tilePane.reposition(posX, posY - 50)
            }
        }
        saganiGameScene.moveDownButton.apply {
            onMouseClicked = {
                saganiGameScene.tilePane.reposition(posX, posY = posY + 50)
            }
        }
        saganiGameScene.moveLeftButton.apply {
            onMouseClicked = {
                saganiGameScene.tilePane.reposition(posX = posX - 50, posY)
            }
        }
        saganiGameScene.moveRightButton.apply {
            onMouseClicked = {
                saganiGameScene.tilePane.reposition(posX = posX + 50, posY)
            }
        }

        saganiGameScene.offer1.apply {
            onMouseClicked = {
                chosenOfferDisplay = 1
                selectedTile = game.offerDisplay[0]
            }
        }
        saganiGameScene.offer2.apply {
            onMouseClicked = {
                chosenOfferDisplay = 1
                selectedTile = game.offerDisplay[1]
            }
        }
        saganiGameScene.offer3.apply {
            onMouseClicked = {
                chosenOfferDisplay = 1
                selectedTile = game.offerDisplay[2]
            }
        }
        saganiGameScene.offer4.apply {
            onMouseClicked = {
                chosenOfferDisplay = 1
                selectedTile = game.offerDisplay[3]
            }
        }
        saganiGameScene.offer5.apply {
            onMouseClicked = {
                chosenOfferDisplay = 1
                selectedTile = game.offerDisplay[4]
            }
        }

        // currentZoom = LEVEL4
        // saganiGameScene.tilePane.scale(currentZoom.scale)

        initScene()

    }


    //TODO
    private fun confirmPlacement() {
        if (!tilePlaced) {

            //TODO: Show Error
            return
        }
        saganiGameScene.tilePane.removeAll(possibleMovements)

        val selectedPlacement = Location(selectedTilePlacement.posX.toInt(), selectedTilePlacement.posY.toInt())

        rootService.playerActionService.placeTile(selectedTile, selectedTile.direction, selectedPlacement)

    }


    private fun initScene() {
        if (!tilePlaced) {
            drawPossiblePlacements()
            saganiGameScene.sampleTile.apply {
                //TODO Load and show front
            }
        }
        loadBoardTiles()
    }

    //Ladet alle Tiles vom Board und added in Scene
    private fun loadBoardTiles() {
        val game = checkNotNull(rootService.currentGame)  // TODO hier notwendig?
        loadedBoardTiles.putAll(game.actPlayer.board)


        //kann Tiles nicht in Scene packen -> muss Card Views reinpacken und die Tiles entsprechend an anderer Stelle laden
        // TODO: Tiles im tilePane laden!
    }


    /**
     * prüft, ob gewählte Variable bereits im Board enthalten ist.
     * Es werden nur Tiles in 120 Schritten hinzugefügt
     * also: x= 0,120,240...
     * y = 0,12,240...
     */
    private fun isOccupied(x: Int, y: Int): Boolean {

        return board.containsKey(Location(x, y))
    }


    private fun drawPossiblePlacements() {
        /* Player has not yet placed a tile -> place in the middle of the board (840,480) */
        if (board.isEmpty()) {
            possibleMovements += PossibleMovements(
                x = 840,
                y = 480,
            )
        }

        //TODO: Fehler Abfangen wenn ein Tile am Rand gesetzt wird. da sonst koordinate negativ

        board.forEach {
            /* Check right, bottom, left, top */
            // x = it.key.first
            // y = it.key.second
            //Checks bottom
            if (!isOccupied(it.key.first + 120, it.key.second)) {
                possibleMovements += PossibleMovements(
                    x = it.key.first + 1,
                    y = it.key.second,
                )
            }
            //Checks right
            if (!isOccupied(it.key.first, it.key.second + 120)) {
                possibleMovements += PossibleMovements(
                    x = it.key.first,
                    y = it.key.second + 120,
                )
            }
            //checks top
            if (!isOccupied(it.key.first - 120, it.key.second)) {
                possibleMovements += PossibleMovements(
                    x = it.key.first - 120,
                    y = it.key.second,
                )
            }
            //checks left
            if (!isOccupied(it.key.first, it.key.second - 120)) {
                possibleMovements += PossibleMovements(
                    x = it.key.first,
                    y = it.key.second - 120,
                )
            }
        }
        /* Add all [VisualGuideSpace]s to the scene and add functionality. */
        possibleMovements.forEach { space ->
            space.apply {
                /* Place tile on a valid space */
                onMouseClicked = {
                    testPlacement(this.x, this.y)
                }
                onKeyPressed = { key ->
                    when (key.keyCode) {
                        KeyCode.ENTER -> testPlacement(this.x, this.y)
                        KeyCode.SPACE -> testPlacement(this.x, this.y)
                        else -> {}
                    }
                }
            }
        }


    }


    private fun testPlacement(x: Int, y: Int) {
        tilePlaced = true
        //ToDo: testTile()

        // hier darf Tile nicht direkt geplaced werden
        saganiGameScene.sampleTile.reposition(x, y)
        saganiGameScene.sampleTile.apply {
            showBack()
        }

        //TODO: Rotate?
    }


}