package view.controllers

import service.RootService
import view.scene.SaganiGameScene
import Location
import entity.*
import service.TileImageLoader

import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import view.*
import view.ZoomLevels.*
import kotlin.math.max
import kotlin.math.min

class SaganiGameSceneController(
    private val saganiGameScene: SaganiGameScene, private val rootService: RootService
) : Refreshable {

    private var board: MutableMap<Location, Tile>
    // The active Player gets returned by refreshAfterChangeToNextPlayer
    private var actPlayer: Player

    private var possibleMovements = mutableListOf<CardView>()

    private var loadedBoardTiles = mutableMapOf<Location, Tile>()

    private var loadedBoardViews = mutableMapOf<Location, CardView>()

    private var selectedTile: Tile
    private var selectedTilePlacement = saganiGameScene.sampleTile
    private var tilePlaced = false
    private var currentZoom = LEVEL1

    private var chosenOfferDisplay = -1
    private var chosenTileView: CardView
    private val tileImageLoader = TileImageLoader()

    init {
        // Creates initial game
        val playerList = mutableListOf(
            Triple("Marie", Color.BLACK, PlayerType.HUMAN),
            Triple("Nils", Color.BROWN, PlayerType.HUMAN),
            Triple("Polina", Color.GREY, PlayerType.HUMAN)
        )
        rootService.gameService.startNewGame(playerList)

        val game = checkNotNull(rootService.currentGame) { "There is no game." }
        actPlayer = game.actPlayer
        board = actPlayer.board
        selectedTile = game.offerDisplay[0]
        chosenTileView = CardView(0, 0, 120, 120, front = ColorVisual(225, 225, 225, 90))

        reloadCardViews(game, true)



        updateActivePlayerLabel()



        //TODO Funktion
        saganiGameScene.testButton.apply {
            onMouseClicked = {
                val selectedPlacement =
                    Location(selectedTilePlacement.posX.toInt() - 2060, selectedTilePlacement.posY.toInt() - 2060)
                refreshAfterPlaceTile(game.actPlayer, selectedTile, selectedPlacement)
            }
        }

        saganiGameScene.confirmButton.apply {
            onMouseClicked = {
                confirmPlacement()
            }
        }

        saganiGameScene.zoomInButton.apply {
            onMouseClicked = {
                println(ZoomLevels.values()[min(ZoomLevels.values().size - 1, currentZoom.ordinal + 1)])
                currentZoom = ZoomLevels.values()[min(ZoomLevels.values().size - 1, currentZoom.ordinal + 1)]
                saganiGameScene.tilePane.scale(currentZoom.scale)
            }
        }
        saganiGameScene.zoomOutButton.apply {
            onMouseClicked = {
                currentZoom = ZoomLevels.values()[max(0, currentZoom.ordinal - 1)]
                saganiGameScene.tilePane.scale(currentZoom.scale)
            }
        }

        saganiGameScene.moveUpButton.apply {
            onMouseClicked = {
                saganiGameScene.tilePane.posY += 10 / currentZoom.scale
            }
        }
        saganiGameScene.moveDownButton.apply {
            onMouseClicked = {
                saganiGameScene.tilePane.posY -= 10 / currentZoom.scale
            }
        }
        saganiGameScene.moveLeftButton.apply {
            onMouseClicked = {
                saganiGameScene.tilePane.posX += 10 / currentZoom.scale
            }
        }
        saganiGameScene.moveRightButton.apply {
            onMouseClicked = {
                saganiGameScene.tilePane.posX -= 10 / currentZoom.scale
            }
        }

        saganiGameScene.homeButton.apply {
            onMouseClicked = {
                centerTilePane()
            }
        }

        saganiGameScene.scoreButton.apply {

        }

        saganiGameScene.offer1.apply {
            onMouseClicked = {
                chosenOfferDisplay = 0
                selectedTile = game.offerDisplay[0]
                drawPossiblePlacements()
            }
        }
        saganiGameScene.offer2.apply {
            onMouseClicked = {
                chosenOfferDisplay = 1
                selectedTile = game.offerDisplay[1]
                drawPossiblePlacements()
            }
        }
        saganiGameScene.offer3.apply {
            onMouseClicked = {
                chosenOfferDisplay = 2
                selectedTile = game.offerDisplay[2]
                drawPossiblePlacements()
            }
        }
        saganiGameScene.offer4.apply {
            onMouseClicked = {
                chosenOfferDisplay = 3
                selectedTile = game.offerDisplay[3]
                drawPossiblePlacements()
//                println(possibleMovements.size)

                // board.put(Location(centerPosInTilePaneX.toInt(), centerPosInTilePaneY.toInt()),selectedTile)
                // println(board.size)
            }
        }
        saganiGameScene.offer5.apply {
            onMouseClicked = {
                chosenOfferDisplay = 4
                selectedTile = game.offerDisplay[4]
                drawPossiblePlacements()
            }
        }


        saganiGameScene.sampleTile.apply {
            onMouseClicked = {
                println("Clicked")
            }
        }

        saganiGameScene.simulationSpeedDropDown.apply {
            selectedItem = "Normal"
            selectedItemProperty.addListener { _, newValue ->
                rootService.gameService.simulationTime = when (newValue) {
                    "Fast" -> 200
                    "Normal" -> 750
                    "Slow" -> 2000
                    "Slowest" -> 5000
                    else -> error("Invalid simulation speed")
                }
            }
        }


        initScene()
    }


    private fun centerTilePane() {
        saganiGameScene.tilePane.posX = centerTilePanePosX
        saganiGameScene.tilePane.posY = centerTilePanePosY
    }

    //TODO
    private fun confirmPlacement() {

        val game = rootService.currentGame
        checkNotNull(game){"Something went wrong"}

        saganiGameScene.tilePane.removeAll(possibleMovements)
        possibleMovements.clear()

        val selectedPlacement =
            Location(selectedTilePlacement.posX.toInt() - 2060, selectedTilePlacement.posY.toInt() - 2060)

        rootService.playerActionService.placeTile(game.offerDisplay[chosenOfferDisplay], selectedTile.direction, selectedPlacement)
        chosenOfferDisplay = -1

//        rootService.gameService.changeToNextPlayer()
//        game.let {  } //TODO: Hier richtig?
    }

    //Ladet alle Tiles vom Board und added in Scene
    private fun loadBoardTiles() {

        //toDo Show Front?

        val game = checkNotNull(rootService.currentGame)  // TODO hier notwendig?
        board = game.actPlayer.board

        val tileImageLoader = TileImageLoader()

        board.forEach {
            val tileView = loadedBoardViews.get(Location(it.key.first, it.key.second))

            if (tileView != null) {
                initializeTileView(tile = it.value, tileView, flip = false)
            }
        }

    }

    private fun initScene() {
        // geht erst alle y von 0 bis 4440 in 120er schritten durch und dann ein x (120er schritte) weiter

        for (x in 0..tilePaneWidth.toInt() step 120) {
            for (y in 0..tilePaneHeight.toInt() step 120) {
                loadedBoardViews.put(
                    Location(x, y), CardView(x, y, 120, 120, front = ColorVisual(255, 255, 255, 50))
                )
            }
        }


        // saganiGameScene.i.addAll(loadedBoardViews.values)

        for (tile in loadedBoardViews) {

            tile.value.isVisible = false
            tile.value.isDisabled = true
        }

        // checkNotNull(loadedBoardViews.get(Location(840, 480))).isVisible = true


        //TODO: SampleTile Weg?
        // saganiGameScene.sampleTile.isVisible = true
        //saganiGameScene.sampleTile.isFocusable = true


        loadBoardTiles()

        /*
           if (!tilePlaced) {
               drawPossiblePlacements()
               saganiGameScene.sampleTile.apply {
                   //TODO Load and show front
               }
           }
           loadBoardTiles()*/
    }

    private fun initializeTileView(tile: Tile, tileView: CardView, flip: Boolean = true) {
        val tileImageLoader = TileImageLoader()

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

    fun updateActivePlayerLabel() {
        // assuming GameModel has a method to get the current active player's name
        val game = checkNotNull(rootService.currentGame)
        val activePlayerName = game.actPlayer.name
        saganiGameScene.playerName.text = "Active Player: $activePlayerName"
    }

    private fun drawPossiblePlacements() {/* Player has not yet placed a tile -> place in the middle of the board (centerPosInTilePane) */
        if (board.isEmpty()) {
            val centerCardView =
                loadedBoardViews.get(Location(centerPosInTilePaneX.toInt(), centerPosInTilePaneY.toInt()))


            if (centerCardView != null) {
                possibleMovements.add(centerCardView)
                centerCardView.isVisible = true
                centerCardView.isDisabled = false
                centerCardView.isFocusable = true

                centerCardView.apply {
                    onMouseClicked = {
                        chosenTileView = centerCardView
                        println(centerCardView.posX)
                        executeTileMove()

                    }
                }
            }

        }

        //TODO: Fehler Abfangen wenn ein Tile am Rand gesetzt wird. da sonst koordinate negativ

        board.forEach {
            /* Check right, bottom, left, top */
            // x = it.key.first
            // y = it.key.second
            //Checks bottom
            if (!isOccupied(it.key.first + 120, it.key.second)) {

                val currentView = checkNotNull(
                    loadedBoardViews.get(Location(it.key.first + 120, it.key.second))
                ) { "Something went wrong!" }

                possibleMovements += currentView
                currentView.isVisible = true
                currentView.isDisabled = false

                currentView.apply {
                    onMouseClicked = {
                        chosenTileView = currentView
                        println(currentView.posX)
                        executeTileMove()
                    }
                }
            }

            //Checks right
            if (!isOccupied(it.key.first, it.key.second + 120)) {
                val currentView = checkNotNull(
                    loadedBoardViews.get(
                        Location(it.key.first, it.key.second + 120)
                    )
                ) { "Something went wrong!" }

                possibleMovements += currentView
                currentView.isVisible = true
                currentView.isDisabled = false

                currentView.apply {
                    onMouseClicked = {
                        chosenTileView = currentView
                        println(currentView.posX)
                        executeTileMove()
                    }
                }

            }
            //checks top
            if (!isOccupied(it.key.first - 120, it.key.second)) {
                val currentView = checkNotNull(
                    loadedBoardViews.get(
                        Location(it.key.first - 120, it.key.second)
                    )
                ) { "Something went wrong!" }

                possibleMovements += currentView
                currentView.isVisible = true
                currentView.isDisabled = false

                currentView.apply {
                    onMouseClicked = {
                        chosenTileView = currentView
                        println(currentView.posX)
                        executeTileMove()
                    }
                }
            }

            //checks left
            if (!isOccupied(it.key.first, it.key.second - 120)) {
                val currentView = checkNotNull(
                    loadedBoardViews.get(
                        Location(it.key.first, it.key.second - 120)
                    )
                ) { "Something went wrong!" }

                possibleMovements += currentView
                currentView.isVisible = true
                currentView.isDisabled = false

                currentView.apply {
                    onMouseClicked = {
                        chosenTileView = currentView
                        println(currentView.posX)
                        executeTileMove()
                    }
                }
            }
        }

        for (tile in possibleMovements) {
            saganiGameScene.tilePane.add(tile)
        }
    }

    private fun executeTileMove() {
        initializeTileView(selectedTile, chosenTileView, true)
    }

    // ToDo:

    override fun refreshAfterChangeToNextPlayer(
        player: Player,
        validLocations: Set<Location>,
        intermezzo: Boolean
    ) {
        val game = rootService.currentGame
        checkNotNull(game)
        // activ Player gets returned by refreshAfterChangeToNextPlayer
        actPlayer = player
        reloadCardViews(game)
    }

    fun reloadCardViews(game: Sagani, flipped: Boolean = false) {
        // Search for the tile and reload offer stack, offers or intermezzo
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
                    frontVisual = ImageVisual(tileImageLoader.getFrontImage(game.offerDisplay[index].id))
                    backVisual = ImageVisual(tileImageLoader.getBackImage(game.offerDisplay[index].id))
                } else {
                    println(game.offerDisplay.size)
                    frontVisual = ColorVisual.LIGHT_GRAY
                    backVisual = ColorVisual.LIGHT_GRAY
                }
                if (flipped) flip()
            }
        }

        val intermezzoOffers = listOf(
            saganiGameScene.intermezzoOffer1,
            saganiGameScene.intermezzoOffer2,
            saganiGameScene.intermezzoOffer3,
            saganiGameScene.intermezzoOffer4
        )

        intermezzoOffers.forEachIndexed { index, intermezzo ->
            intermezzo.apply {
                if (game.intermezzoStorage.size > index) {
                    frontVisual = ImageVisual(tileImageLoader.getFrontImage(game.intermezzoStorage[index].id))
                    backVisual = ImageVisual(tileImageLoader.getBackImage(game.intermezzoStorage[index].id))
                }
            }
        }

        saganiGameScene.cardStack.apply {
            if (game.stacks.size > 0) {
                frontVisual = ImageVisual(tileImageLoader.getFrontImage(game.stacks[0].id))
                backVisual = ImageVisual(tileImageLoader.getBackImage(game.stacks[0].id))
            }
        }

        saganiGameScene.smallCardStack1.apply {
            if (game.stacks.size in 25..48) {
                frontVisual = ImageVisual(tileImageLoader.getFrontImage(game.stacks[game.stacks.size - 24].id))
                backVisual = ImageVisual(tileImageLoader.getBackImage(game.stacks[game.stacks.size - 24].id))
            } else if (rootService.currentGame!!.stacks.size > 48) {
                frontVisual = ImageVisual(tileImageLoader.getFrontImage(game.stacks[game.stacks.size - 48].id))
                backVisual = ImageVisual(tileImageLoader.getBackImage(game.stacks[game.stacks.size - 48].id))
            }
        }

        saganiGameScene.smallCardStack2.apply {
            if (game.stacks.size > 48) {
                frontVisual = ImageVisual(tileImageLoader.getFrontImage(game.stacks[game.stacks.size - 24].id))
                backVisual = ImageVisual(tileImageLoader.getBackImage(game.stacks[game.stacks.size - 24].id))
            }
        }
    }


}