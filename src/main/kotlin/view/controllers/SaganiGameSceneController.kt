package view.controllers

import service.RootService
import view.scene.SaganiGameScene
import Location
import entity.Color
import entity.PlayerType
import entity.Tile
import service.TileImageLoader
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.event.KeyCode
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import view.*
import view.ZoomLevels.*
import kotlin.math.max
import kotlin.math.min

class SaganiGameSceneController(
    private val saganiGameScene: SaganiGameScene,
    private val rootService: RootService
) : Refreshable {

    private var board: MutableMap<Location, Tile>
    private var possibleMovements = mutableListOf<PossibleMovements>()

    private var loadedBoardTiles = mutableMapOf<Location, Tile>()

    private var loadedBoardViews = mutableMapOf<Location, CardView>()

    private var selectedTile: Tile
    private var selectedTilePlacement = saganiGameScene.sampleTile
    private var tilePlaced = false
    private var currentZoom = LEVEL1

    private var chosenOfferDisplay = -1
    val tileImageLoader = TileImageLoader()

    init {
        // Creates initial game
        val playerList = mutableListOf(
            Triple("Marie", Color.BLACK, PlayerType.HUMAN),
            Triple("Nils", Color.BROWN, PlayerType.HUMAN),
            Triple("Polina", Color.GREY, PlayerType.HUMAN)
        )
        rootService.gameService.startNewGame(playerList)

        val game = checkNotNull(rootService.currentGame) { "There is no game." }
        board = game.actPlayer.board
        selectedTile = game.offerDisplay[0]


        //TODO Funktion
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

        // TODO: Relation zum Zoom herstellen ( Reingezoomt -> Kleinere Schritte, Rausgezoomt -> größere Schritte
        saganiGameScene.moveUpButton.apply {
            onMouseClicked = {
                saganiGameScene.tilePane.posY += 10
            }
        }
        saganiGameScene.moveDownButton.apply {
            onMouseClicked = {
                saganiGameScene.tilePane.posY -= 10
            }
        }
        saganiGameScene.moveLeftButton.apply {
            onMouseClicked = {
                saganiGameScene.tilePane.posX += 10
            }
        }
        saganiGameScene.moveRightButton.apply {
            onMouseClicked = {
                saganiGameScene.tilePane.posX -= 10
            }
        }

        saganiGameScene.homeButton.apply {
            onMouseClicked = {
                centerTilePane()
            }
        }


        val offers = listOf(
            saganiGameScene.offer1,
            saganiGameScene.offer2,
            saganiGameScene.offer3,
            saganiGameScene.offer4,
            saganiGameScene.offer5
        )

        offers.forEachIndexed { index, offer ->
            offer.apply {
                if (game.offerDisplay.size > index) {
                    visual = ImageVisual(tileImageLoader.getFrontImage(game.offerDisplay[index].id))
                }
                onMouseClicked = {
                    chosenOfferDisplay = 1
                    selectedTile = game.offerDisplay[index]
                }
            }
        }

        // TODo: Karte in die Mitte legen
        saganiGameScene.cardStack.apply {
            if (game.stacks.size > 0) {
                visual = ImageVisual(tileImageLoader.getBackImage(game.stacks[0].id))
            }
        }

        saganiGameScene.smallCardStack1.apply {
            if (game.stacks.size in 25..48) {
                visual = ImageVisual(tileImageLoader.getBackImage(game.stacks[game.stacks.size - 24].id))
            } else if (rootService.currentGame!!.stacks.size > 48) {
                visual = ImageVisual(tileImageLoader.getBackImage(game.stacks[game.stacks.size - 48].id))
            }
        }

        saganiGameScene.smallCardStack2.apply {
            if (game.stacks.size > 48) {
                visual = ImageVisual(tileImageLoader.getBackImage(game.stacks[game.stacks.size - 24].id))
            }
        }

        val intermezzoOffers = listOf(
            saganiGameScene.intermezzoOffer1,
            saganiGameScene.intermezzoOffer2,
            saganiGameScene.intermezzoOffer3,
            saganiGameScene.intermezzoOffer4)

        intermezzoOffers.forEachIndexed { index, intermezzo ->
            intermezzo.apply {
                if (game.intermezzoStorage.size > index) {
                    visual = ImageVisual(tileImageLoader.getFrontImage(game.intermezzoStorage[index].id))
                }
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

    private fun centerTilePane(){
        // Methode um tilePane zu centern --> wenn neuer Spieler dann kann gecentert werden
        //TODO

        saganiGameScene.tilePane.posX = saganiGameScene.centerTilePanePosX
        saganiGameScene.tilePane.posY = saganiGameScene.centerTilePanePosY

    }



    // Home Button um nach Center zu springen
    // Move faktor Abhängig vom Scale faktor (größer wenn rausgezoomt, kleiner wenn reingezoomt)



    private fun initScene() {
        // geht erst alle y von 0 bis 960 in 120er schritten durch und dann ein x (120er schritte) weiter


        for (x in 0..1800 step 120) {
            for (y in 0..960 step 120) {
                loadedBoardViews.put(
                    Location(x, y),
                    CardView(x, y, 120, 120, front = ColorVisual(255,255,255,50))
                )
            }
        }


        saganiGameScene.tilePane.addAll(loadedBoardViews.values)

        for (tile in loadedBoardViews){
            tile.value.isVisible = false
        }

       // checkNotNull(loadedBoardViews.get(Location(840,480))).isVisible = true




        // Mitte trd sichtbar ? --> SampleTile kann dann weg
        // Grid besteht dann aus eingeblendeten CardViews
        // Liste besteht dann aus CardViews die eingeblendet werden


        /*
           if (!tilePlaced) {
               drawPossiblePlacements()
               saganiGameScene.sampleTile.apply {
                   //TODO Load and show front
               }
           }
           loadBoardTiles()*/
    }

    //Ladet alle Tiles vom Board und added in Scene
    private fun loadBoardTiles() {

        // Pane direkt mit allen card views laden -->
        // ? Rechenaufwand zu groß ?

        //toDo Show Front?

        val game = checkNotNull(rootService.currentGame)  // TODO hier notwendig?
        board = game.actPlayer.board

        val tileImageLoader = TileImageLoader()

        board.forEach {
            val tileView = loadedBoardViews.get(Location(it.key.first, it.key.second))

            if (tileView != null) {
                initializeTileView(
                    tile = it.value,
                    tileView,
                    tileImageLoader = tileImageLoader,
                    flip = false
                )
            }
        }
    }



    private fun initializeTileView(
        tile: Tile,
        tileView: CardView,
        tileImageLoader: TileImageLoader,
        flip: Boolean
    ) {

        tileView.frontVisual = ImageVisual(tileImageLoader.getFrontImage(tile.id))

        tileView.backVisual = ImageVisual(tileImageLoader.getBackImage(tile.id))

        // cardMap.add(card to cardView)

        if (flip) {
            when (tileView.currentSide) {
                CardView.CardSide.BACK -> tileView.showFront()
                CardView.CardSide.FRONT -> tileView.showBack()
            }

        }
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