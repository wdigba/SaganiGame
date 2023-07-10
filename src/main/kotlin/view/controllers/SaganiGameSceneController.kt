package view.controllers

import Location
import entity.*
import service.RootService
import service.TileImageLoader
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import view.*
import view.ZoomLevels.LEVEL1
import view.scene.SaganiGameScene
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Controller for the [SaganiGameScene].
 */
class SaganiGameSceneController(
    private val saganiGameScene: SaganiGameScene, private val rootService: RootService
) : Refreshable {
    private var board: MutableMap<Location, Tile>

    // The active Player gets returned by refreshAfterChangeToNextPlayer
    private var game: Sagani
    private var actPlayer: Player
    private var validLocations = setOf<Location>()
    private var intermezzo: Boolean

    private var possibleMovements = mutableListOf<CardView>()

    private var loadedBoardViews = mutableMapOf<Location, CardView>()

    private var selectedTile: Tile

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

        // deactivate confirm button
        saganiGameScene.confirmButton.isDisabled = true

        game = checkNotNull(rootService.currentGame) { "There is no game." }
        actPlayer = game.actPlayer
        board = actPlayer.board
        intermezzo = false
        selectedTile = game.offerDisplay[0]
        chosenTileView = CardView(0, 0, 120, 120, front = ColorVisual(225, 225, 225, 50))

        reloadCardViews(game, true)

        updateActivePlayerLabel()

        // button for getting back if looking at another players board
        saganiGameScene.returnFromOtherPlayerButton.isVisible = false
        saganiGameScene.returnFromOtherPlayerButton.isDisabled = true


        //TODO Funktion
        saganiGameScene.testButton.apply {
            onMouseClicked = {
                updateActivePlayerLabel()
            }
        }

        saganiGameScene.returnFromOtherPlayerButton.apply {
            onMouseClicked = {
                loadBoardTiles(board)
                saganiGameScene.returnFromOtherPlayerButton.isVisible = false
                saganiGameScene.returnFromOtherPlayerButton.isDisabled = true
                //enable all offerings again
                val game = checkNotNull(rootService.currentGame) { "no game loaded" }
                reloadCardViews(game)
            }
        }

        saganiGameScene.confirmButton.apply {
            onMouseClicked = {
                confirmPlacement()
            }
        }

        saganiGameScene.skipButton.apply {
            onMouseClicked = {
                rootService.playerActionService.skipIntermezzoTurn()
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

        saganiGameScene.undoButton.apply {
            onMouseClicked = {
                rootService.gameService.undo()
                println("undo")
            }
        }

        saganiGameScene.redoButton.apply {
            onMouseClicked = {
                rootService.gameService.redo()
            }
        }

        //Rotate can only affect the currently placed tileView saved in chosenTileView
        saganiGameScene.rotateButton.apply {

            onMouseClicked = {
                chosenTileView.rotation += 90.0
            }
        }

        saganiGameScene.cardStack.apply {

            onMouseClicked = {
                saganiGameScene.offer1.isDisabled = true
                saganiGameScene.offer2.isDisabled = true
                saganiGameScene.offer3.isDisabled = true
                saganiGameScene.offer4.isDisabled = true
                saganiGameScene.offer5.isDisabled = true

                saganiGameScene.soundDiscs.isDisabled = true
                saganiGameScene.cacophonyDiscs.isDisabled = true

                flipTileToFrontSide(saganiGameScene.cardStack)
                selectedTile = game.stacks[0]
                // override chosenTileView to clear all possiblePlacements if this is not the first offering
                // chosen this turn
                chosenTileView = CardView(
                    0, 0, 120, 120,
                    front = ColorVisual(225, 225, 225, 90)
                )
                clearPossibleMoves()
                drawPossiblePlacements()

            }
        }

        saganiGameScene.offer1.apply {
            onMouseClicked = {
                chosenOfferDisplay = 0
                selectedTile = game.offerDisplay[0]
                // override chosenTileView to clear all possiblePlacements if this is not the first offering
                // chosen this turn
                chosenTileView = CardView(
                    0, 0, 120, 120,
                    front = ColorVisual(225, 225, 225, 90)
                )
                clearPossibleMoves()
                drawPossiblePlacements()
            }
        }
        saganiGameScene.offer2.apply {
            onMouseClicked = {
                chosenOfferDisplay = 1
                selectedTile = game.offerDisplay[1]
                // override chosenTileView to clear all possiblePlacements if this is not the first offering
                // chosen this turn
                chosenTileView = CardView(
                    0, 0, 120, 120,
                    front = ColorVisual(225, 225, 225, 90)
                )
                clearPossibleMoves()
                drawPossiblePlacements()
            }
        }
        saganiGameScene.offer3.apply {
            onMouseClicked = {
                chosenOfferDisplay = 2
                selectedTile = game.offerDisplay[2]
                // override chosenTileView to clear all possiblePlacements if this is not the first offering
                // chosen this turn
                chosenTileView = CardView(
                    0, 0, 120, 120,
                    front = ColorVisual(225, 225, 225, 90)
                )
                clearPossibleMoves()
                drawPossiblePlacements()
            }
        }
        saganiGameScene.offer4.apply {

            onMouseClicked = {
                chosenOfferDisplay = 3
                selectedTile = game.offerDisplay[3]
                // override chosenTileView to clear all possiblePlacements if this is not the first offering
                // chosen this turn
                chosenTileView = CardView(
                    0, 0, 120, 120,
                    front = ColorVisual(225, 225, 225, 90)
                )
                clearPossibleMoves()
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
                // override chosenTileView to clear all possiblePlacements if this is not the first offering
                // chosen this turn
                chosenTileView = CardView(
                    0, 0, 120, 120,
                    front = ColorVisual(225, 225, 225, 90)
                )
                clearPossibleMoves()
                drawPossiblePlacements()
            }
        }

        saganiGameScene.intermezzoOffer1.apply {
            onMouseClicked = {
                selectedTile = game.intermezzoStorage[0]
                // override chosenTileView to clear all possiblePlacements if this is not the first offering
                // chosen this turn
                chosenTileView = CardView(
                    0, 0, 120, 120,
                    front = ColorVisual(225, 225, 225, 90)
                )
                clearPossibleMoves()
                drawPossiblePlacements()
            }
        }
        saganiGameScene.intermezzoOffer2.apply {
            onMouseClicked = {
                selectedTile = game.intermezzoStorage[1]
                // override chosenTileView to clear all possiblePlacements if this is not the first offering
                // chosen this turn
                chosenTileView = CardView(
                    0, 0, 120, 120,
                    front = ColorVisual(225, 225, 225, 90)
                )
                clearPossibleMoves()
                drawPossiblePlacements()
            }
        }
        saganiGameScene.intermezzoOffer3.apply {
            onMouseClicked = {
                selectedTile = game.intermezzoStorage[2]
                // override chosenTileView to clear all possiblePlacements if this is not the first offering
                // chosen this turn
                chosenTileView = CardView(
                    0, 0, 120, 120,
                    front = ColorVisual(225, 225, 225, 90)
                )
                clearPossibleMoves()
                drawPossiblePlacements()
            }
        }
        saganiGameScene.intermezzoOffer4.apply {
            onMouseClicked = {
                selectedTile = game.intermezzoStorage[3]
                // override chosenTileView to clear all possiblePlacements if this is not the first offering
                // chosen this turn
                chosenTileView = CardView(
                    0, 0, 120, 120,
                    front = ColorVisual(225, 225, 225, 90)
                )
                clearPossibleMoves()
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
                    "Fastest" -> 200
                    "Fast" -> 750
                    "Normal" -> 200
                    "Slow" -> 5000
                    "Slowest" -> 10000
                    else -> error("Invalid simulation speed")
                }
            }
        }


        initScene()
    }


    private fun centerTilePane() {
        saganiGameScene.tilePane.posX = CENTER_TILE_PANE_POS_X
        saganiGameScene.tilePane.posY = CENTER_TILE_PANE_POS_Y
    }

    //TODO
    private fun confirmPlacement() {

        saganiGameScene.tilePane.removeAll(possibleMovements)
        possibleMovements.clear()

        // Pixel locations of tiles are defined as x,y -> centerPoint + 120*x, centerpoint - 120*y
        // -> so to get x from pixel (selectedTilePlacement.posX - center)/120
        //    and y from pixel : (center-selectedTilePlacement.posY)/120
        val selectedPlacement =
            Location(
                (chosenTileView.posX.toInt() - CENTER_POS_IN_TILE_PANE_X.toInt()) / 120,
                (CENTER_POS_IN_TILE_PANE_Y.toInt() - chosenTileView.posY.toInt()) / 120
            )

        println(selectedPlacement)
        val chosenDirection: Direction = when (chosenTileView.rotation) {
            90.0 -> Direction.RIGHT
            180.0 -> Direction.DOWN
            270.0 -> Direction.LEFT
            else -> Direction.UP
        }
        // ToDo tile not always in offerDisplay
        rootService.playerActionService.placeTile(
            selectedTile,
            chosenDirection,
            selectedPlacement
        )
        chosenOfferDisplay = -1

        // deactivate confirm button until a new offering is clicked
        saganiGameScene.confirmButton.isDisabled = true

    }

    /**
     * clears the board represented by loadedBoardViews and re-loads it with empty CardViews
     * Update 2023-07-09 -> only create CardViews that hold Tiles or are possible tile locations
     *                      for performance reasons
     * @param parBoard optional parameter used in case the board of a [Player] that is not actPlayer should be drawn
     */
    private fun resetLoadedBoardViews(parBoard: MutableMap<Pair<Int, Int>, Tile>) {

        // clear everything
        loadedBoardViews.clear()
        saganiGameScene.tilePane.clear()

        // geht erst alle y von 0 bis 8880 in 120er schritten durch und dann ein x (120er schritte) weiter
        // Daniel Gast: changed to step and height/width of 60 -> 4440/120 = 37 Tiles
        //              To get 36 Tiles in each direction 4440/(72+1) = 60.82 ~ 61px


        // for the game start ... duplicate code from below
        if (parBoard.isEmpty()) {

            val tileSlotX = CENTER_POS_IN_TILE_PANE_X.toInt()
            val tileSlotY = CENTER_POS_IN_TILE_PANE_Y.toInt()
            val relevantLocations = listOf(Pair(0, 0))

            for (delta in relevantLocations) {

                if (!loadedBoardViews.containsKey(Location(tileSlotX + delta.first, tileSlotY + delta.second))) {

                    val iterX = tileSlotX + delta.first
                    val iterY = tileSlotY + delta.second
                    val emptyCardView = CardView(
                        iterX, iterY, 120, 120,
                        front = ColorVisual(225, 225, 225, 50)
                    )

                    loadedBoardViews[Location(iterX, iterY)] = emptyCardView
                    saganiGameScene.tilePane.add(emptyCardView)

                }

            }
        } else {

            // for each Tile in board -> get px coordinates for tile and 4 neighbors
            // create cardView at that location of none exists yet
            parBoard.forEach {

                val tileSlotX = CENTER_POS_IN_TILE_PANE_X.toInt() + 120 * it.key.first
                val tileSlotY = CENTER_POS_IN_TILE_PANE_Y.toInt() - 120 * it.key.second
                //                             center,     top,           right,        bottom,       left
                val relevantLocations = listOf(Pair(0, 0), Pair(0, -120), Pair(120, 0), Pair(0, 120), Pair(-120, 0))

                for (delta in relevantLocations) {

                    if (!loadedBoardViews.containsKey(Location(tileSlotX + delta.first, tileSlotY + delta.second))) {

                        val iterX = tileSlotX + delta.first
                        val iterY = tileSlotY + delta.second
                        val emptyCardView = CardView(
                            iterX, iterY, 120, 120,
                            front = ColorVisual(225, 225, 225, 50)
                        )

                        loadedBoardViews[Location(iterX, iterY)] = emptyCardView
                        saganiGameScene.tilePane.add(emptyCardView)


                    }
                }

            }

        }


    }

    /**
     * called from ScoreSceneController
     * Loads the player with the given idx
     */
    fun showBoardOfPlayer(index: Int) {
        // load data of player
        val game = checkNotNull(rootService.currentGame) { "game not loaded in showBoardOfPlayer" }
        val showPlayer = game.players[index]

        if (showPlayer != game.actPlayer) {
            //load board
            loadBoardTiles(showPlayer.board)

            // make back button visible
            saganiGameScene.returnFromOtherPlayerButton.isVisible = true
            saganiGameScene.returnFromOtherPlayerButton.isDisabled = false

            // disable offerings and intermezzo cards
            saganiGameScene.offer1.isDisabled = true
            saganiGameScene.offer2.isDisabled = true
            saganiGameScene.offer3.isDisabled = true
            saganiGameScene.offer4.isDisabled = true
            saganiGameScene.offer5.isDisabled = true

            saganiGameScene.intermezzoOffer1.isDisabled = true
            saganiGameScene.intermezzoOffer2.isDisabled = true
            saganiGameScene.intermezzoOffer3.isDisabled = true
            saganiGameScene.intermezzoOffer4.isDisabled = true

        }
    }

    private fun initScene() {

        // reset board
        resetLoadedBoardViews(board)

        for (tile in loadedBoardViews) {
            tile.value.isVisible = true
            tile.value.isDisabled = true
        }

        //TODO: SampleTile Weg?
        //saganiGameScene.sampleTile.isVisible = true
        //saganiGameScene.sampleTile.isFocusable = true

        val game = rootService.currentGame
        checkNotNull(game) { "game was null in initScene()" }

        game.actPlayer.board[Pair(0, 0)] = game.stacks.removeFirst()
        game.actPlayer.board[Pair(0, 0)]?.discs?.add(Disc.CACOPHONY)
        game.actPlayer.board[Pair(1, 0)] = game.stacks.removeFirst()
        game.actPlayer.board[Pair(1, 0)]?.discs?.add(Disc.SOUND)
        game.actPlayer.board[Pair(1, 1)] = game.stacks.removeFirst()
        game.actPlayer.board[Pair(1, 1)]?.arrows?.forEach { it.disc.add(Disc.SOUND) }


        println("added Tiles")
        loadBoardTiles(game.actPlayer.board)
        println(game.actPlayer.board)
        println("loaded board")
        print("Active player is: ")
        println(game.actPlayer.name)
    }

    /**
     * Takes a [Player] board as Input and loads it to loadedBoardViews
     */
    private fun loadBoardTiles(board: MutableMap<Pair<Int, Int>, Tile>) {

        // clear current board
        resetLoadedBoardViews(board)

        // check if board is not from actPlayer. In this case a different label needs to
        // be pushed to saganiGameScene.playerName
        val ownerPlayer = (game.players.filter { it.board == board }).first()

        // assuming there is only one element in list -> board objects should be unique
        if (ownerPlayer != actPlayer) {
            saganiGameScene.playerName.text = "Board owner: ${ownerPlayer.name}"
        } else {
            updateActivePlayerLabel()
        }

        // count and display cacaphony and sound discs
        val soundDiscs = (ownerPlayer.discs.filter { it == Disc.SOUND }).size
        val cacaphonyDiscs = (ownerPlayer.discs.filter { it == Disc.CACOPHONY }).size
        saganiGameScene.soundDiscCount.text = "$soundDiscs"
        saganiGameScene.cacophonyDiscCount.text = "$cacaphonyDiscs"

        // Pane is 4440px wide, equals
        board.forEach {
            //(120 * 18) +
            val tileView = loadedBoardViews[Location(
                CENTER_POS_IN_TILE_PANE_X.toInt() + 120 * it.key.first,
                CENTER_POS_IN_TILE_PANE_Y.toInt() - 120 * it.key.second
            )]

            // push Tile image to tilePane
            if (tileView != null) {
                initializeTileView(tile = it.value, tileView)
                tileView.showFront()

                tileView.isVisible = true
                tileView.isDisabled = false

                // flip Tile if it is solved
                if (it.value.flipped) {
                    tileView.rotation = 0.0
                    tileView.flip()
                }
                //saganiGameScene.tilePane.add(tileView)

            }

            // CENTER_POS_IN_TILE_PANE_X.toInt() + 120 * it.key.first gets to top left corner of disc,
            // STANDARD_TILE_VIEW_WIDTH/2 gets close center of disc, 20 accounts with size of token
            val tokenSize = 20
            val tileSize = 120
            val centerViewX = (CENTER_POS_IN_TILE_PANE_X.toInt() + tileSize * it.key.first
                    + STANDARD_TILE_VIEW_WIDTH / 2 - tokenSize / 2)
            val centerViewY = (CENTER_POS_IN_TILE_PANE_Y.toInt() - tileSize * it.key.second
                    + STANDARD_TILE_VIEW_HEIGHT / 2 - tokenSize / 2)

            // add unassigned Discs to Tile center
            it.value.discs.forEach { disc ->
                // Random.nextInt introduces some jitter so that multiple discs can be seen
                if (disc == Disc.SOUND) {
                    saganiGameScene.tilePane.add(
                        TokenView(
                            width = tokenSize,
                            height = tokenSize,
                            posX = centerViewX + Random.nextInt(-5, 5),
                            posY = centerViewY + Random.nextInt(-5, 5),
                            visual = ColorVisual.WHITE
                        )
                    )
                } else {
                    saganiGameScene.tilePane.add(
                        TokenView(
                            width = tokenSize,
                            height = tokenSize,
                            posX = centerViewX + Random.nextInt(-5, 5),
                            posY = centerViewY + Random.nextInt(-5, 5),
                            visual = ColorVisual.RED
                        )
                    )
                }
            }

            val solvedArrowFactor = 0.325
            // add disc to each arrow if it is "solved"
            it.value.arrows.forEach { arrow ->

                if (arrow.disc.size > 0) {

                    val solvedArrowX: Double
                    val solvedArrowY: Double
                    // find coordinates based on direction ... if there is time change to when() { ...}
                    when (arrow.direction) {
                        Direction.UP -> {
                            solvedArrowX = centerViewX
                            solvedArrowY = centerViewY - tileSize * solvedArrowFactor
                        }

                        Direction.UP_RIGHT -> {
                            solvedArrowX = centerViewX + tileSize * solvedArrowFactor
                            solvedArrowY = centerViewY - tileSize * solvedArrowFactor
                        }

                        Direction.RIGHT -> {
                            solvedArrowX = centerViewX + tileSize * solvedArrowFactor
                            solvedArrowY = centerViewY
                        }

                        Direction.DOWN_RIGHT -> {
                            solvedArrowX = centerViewX + tileSize * solvedArrowFactor
                            solvedArrowY = centerViewY + tileSize * solvedArrowFactor
                        }

                        Direction.DOWN -> {
                            solvedArrowX = centerViewX
                            solvedArrowY = centerViewY + tileSize * solvedArrowFactor
                        }

                        Direction.DOWN_LEFT -> {
                            solvedArrowX = centerViewX - tileSize * solvedArrowFactor
                            solvedArrowY = centerViewY + tileSize * solvedArrowFactor
                        }

                        Direction.LEFT -> {
                            solvedArrowX = centerViewX - tileSize * solvedArrowFactor
                            solvedArrowY = centerViewY
                        }

                        Direction.UP_LEFT -> {
                            solvedArrowX = centerViewX - tileSize * solvedArrowFactor
                            solvedArrowY = centerViewY - tileSize * solvedArrowFactor
                        }
                    }

                    if (arrow.disc.first() == Disc.SOUND) {
                        saganiGameScene.tilePane.add(
                            TokenView(
                                width = tokenSize,
                                height = tokenSize,
                                posX = solvedArrowX,
                                posY = solvedArrowY,
                                visual = ColorVisual.WHITE
                            )
                        )
                    } else {
                        saganiGameScene.tilePane.add(
                            TokenView(
                                width = tokenSize,
                                height = tokenSize,
                                posX = solvedArrowX,
                                posY = solvedArrowY,
                                visual = ColorVisual.RED
                            )
                        )
                    }
                }

            }


        }
    }

    private fun flipTileToFrontSide(tileView: CardView) {
        if (tileView.currentSide == CardView.CardSide.BACK) {
            tileView.showFront()
        }
    }

    private fun initializeTileView(tile: Tile, tileView: CardView) {
        val tileImageLoader = TileImageLoader()

        tileView.frontVisual = ImageVisual(tileImageLoader.getFrontImage(tile.id))
        tileView.backVisual = ImageVisual(tileImageLoader.getBackImage(tile.id))

        when (tile.direction) {
            Direction.RIGHT -> tileView.rotation = 90.0
            Direction.DOWN -> tileView.rotation = 180.0
            Direction.LEFT -> tileView.rotation = 270.0
            else -> tileView.rotation = 0.0
        }

    }


    /**
     * prüft, ob gewählte Variable bereits im Board enthalten ist.
     * Es werden nur Tiles in 120 Schritten hinzugefügt
     * also: x= 0,120,240...
     * y = 0,12,240...
     */
    private fun isOccupied(x: Int, y: Int): Boolean {

        // get cardview and check if View has ImageVisual or ColorVisual
        // ColorVisual means that no card is loaded -> not occupied
        val toCheckCardView = loadedBoardViews[Location(x, y)]
        if (toCheckCardView?.frontVisual is ColorVisual) {
            return false
        } else if (toCheckCardView?.frontVisual is ImageVisual) {
            return true
        }
        return true

    }

    /**
     * Updates the active player label in the UI
     */
    private fun updateActivePlayerLabel() {
        // assuming GameModel has a method to get the current active player's name
        val activePlayerName = actPlayer.name
        saganiGameScene.playerName.text = "Active Player: $activePlayerName"
    }

    private fun drawPossiblePlacements() {/* Player has not yet placed a tile -> place in the middle of the board (centerPosInTilePane) */

        possibleMovements.clear()

        if (board.isEmpty()) {
            val centerCardView =
                loadedBoardViews[Location(CENTER_POS_IN_TILE_PANE_X.toInt(), CENTER_POS_IN_TILE_PANE_Y.toInt())]


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
                        saganiGameScene.confirmButton.isDisabled = false
                    }
                }
            }

        }

        //TODO: Fehler Abfangen wenn ein Tile am Rand gesetzt wird. da sonst koordinate negativ

        board.forEach {
            /* Check right, bottom, left, top */
            // x = it.key.first
            // y = it.key.second
            val bottomX = CENTER_POS_IN_TILE_PANE_X.toInt() + it.key.first * 120
            val bottomY = CENTER_POS_IN_TILE_PANE_Y.toInt() - it.key.second * 120 + 120
            val rightX = CENTER_POS_IN_TILE_PANE_X.toInt() + it.key.first * 120 + 120
            val rightY = CENTER_POS_IN_TILE_PANE_Y.toInt() - it.key.second * 120
            val topX = CENTER_POS_IN_TILE_PANE_X.toInt() + it.key.first * 120
            val topY = CENTER_POS_IN_TILE_PANE_Y.toInt() - it.key.second * 120 - 120
            val leftX = CENTER_POS_IN_TILE_PANE_X.toInt() + it.key.first * 120 - 120
            val leftY = CENTER_POS_IN_TILE_PANE_Y.toInt() - it.key.second * 120

            //Checks bottom
            if (!isOccupied(bottomX, bottomY)) {

                val currentView = checkNotNull(
                    loadedBoardViews[Location(bottomX, bottomY)]
                ) { "Something went wrong!" }

                possibleMovements += currentView
                currentView.isVisible = true
                currentView.isDisabled = false

                currentView.apply {
                    onMouseClicked = {
                        chosenTileView = currentView
                        println(currentView.posX)
                        executeTileMove()
                        saganiGameScene.confirmButton.isDisabled = false
                    }
                }

            }

            //Checks right
            if (!isOccupied(rightX, rightY)
            ) {
                val currentView = checkNotNull(
                    loadedBoardViews[Location(rightX, rightY)]
                ) { "Something went wrong!" }

                possibleMovements += currentView
                currentView.isVisible = true
                currentView.isDisabled = false

                currentView.apply {
                    onMouseClicked = {
                        chosenTileView = currentView
                        println(currentView.posX)
                        executeTileMove()
                        saganiGameScene.confirmButton.isDisabled = false
                    }
                }

            }
            //checks top
            if (!isOccupied(topX, topY)
            ) {
                val currentView = checkNotNull(
                    loadedBoardViews[Location(topX, topY)]
                ) { "Something went wrong!" }



                possibleMovements += currentView
                currentView.isVisible = true
                currentView.isDisabled = false

                currentView.apply {
                    onMouseClicked = {
                        //chosenTileView.forEach { }
                        chosenTileView = currentView
                        println(currentView.posX)
                        executeTileMove()
                        saganiGameScene.confirmButton.isDisabled = false
                    }
                }

            }

            //checks left
            if (!isOccupied(leftX, leftY)
            ) {
                val currentView = checkNotNull(
                    loadedBoardViews[Location(leftX, leftY)]
                ) { "Something went wrong!" }

                possibleMovements += currentView
                currentView.isVisible = true
                currentView.isDisabled = false
                currentView.apply {
                    onMouseClicked = {
                        chosenTileView = currentView
                        println(currentView.posX)
                        executeTileMove()
                        saganiGameScene.confirmButton.isDisabled = false
                    }
                }

            }
        }

        for (tile in possibleMovements) {
            // if check necessary since two exiting tiles can share a possible tile -> leads to crash
            if (!saganiGameScene.tilePane.contains(tile)) {
                saganiGameScene.tilePane.add(tile)
                tile.isVisible = true
                tile.isDisabled = false
            }

        }
    }

    /**
     * Clears offering/intermezzo cards that have not been set yet if another location or tile is chosen
     */
    private fun clearPossibleMoves() {
        // all other views in possibleMovements should show no card
        possibleMovements.forEach {
            if (it != chosenTileView) {
                it.frontVisual = ColorVisual(225, 225, 225, 90)
                // reset rotation
                it.rotation = 0.0
            }
        }
    }

    private fun executeTileMove() {
        // push image to selected TileView
        initializeTileView(selectedTile, chosenTileView)
        flipTileToFrontSide(chosenTileView)

        clearPossibleMoves()
    }

    /**
     * After Undo or Redo rootService.currentGame points towards a different Sagani object
     * So we get a pointer for the current game and redraw everything
     */
    override fun refreshAfterRedo() {
        game = checkNotNull(rootService.currentGame)
        intermezzo = game.intermezzo
        actPlayer = if (intermezzo) {
            game.intermezzoPlayers[0]
        } else {
            game.actPlayer
        }
        board = actPlayer.board
        // reload the board. active Player label will be set in loadBoardTiles() as well
        loadBoardTiles(board)

        // reload offering tiles, intermezzo and stack
        reloadCardViews(game)
    }

    /**
     * Logic identical to refresnAfterRedo -> just get new currentGame and reload everything
     */
    override fun refreshAfterUndo() {
        refreshAfterRedo()
    }

    override fun refreshAfterStartNewGame(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {
        game = checkNotNull(rootService.currentGame)
        // active Player gets returned by refreshAfterChangeToNextPlayer
        actPlayer = player
        board = actPlayer.board
        this.validLocations = validLocations
        this.intermezzo = intermezzo
        reloadCardViews(game)
        loadBoardTiles(board)

        println("test")

        //if (actPlayer.playerType in listOf(PlayerType.BEST_AI, PlayerType.RANDOM_AI)) {
        //    rootService.gameService.changeToNextPlayer()
        //}
    }

    override fun refreshAfterChangeToNextPlayer(
        player: Player,
        validLocations: Set<Location>,
        intermezzo: Boolean
    ) {
        game = checkNotNull(rootService.currentGame)
        // active Player gets returned by refreshAfterChangeToNextPlayer
        actPlayer = player
        board = actPlayer.board
        this.validLocations = validLocations
        this.intermezzo = intermezzo
        reloadCardViews(game)
        loadBoardTiles(board)

        println(actPlayer.playerType)
        // if (actPlayer.playerType in listOf(PlayerType.BEST_AI, PlayerType.RANDOM_AI)) {
        //    rootService.gameService.changeToNextPlayer()
        //}
    }

    override fun refreshAfterLoadGame() {
        refreshAfterRedo()
    }

    private fun reloadCardViews(game: Sagani, flipped: Boolean = false) {
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
                    offer.isDisabled = false
                } else {
                    println(game.offerDisplay.size)
                    frontVisual = ColorVisual(255, 255, 255, 50)
                    backVisual = ColorVisual(255, 255, 255, 50)
                    offer.isDisabled = true
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

        intermezzoOffers.forEachIndexed { index, intermezzoOffer ->
            intermezzoOffer.apply {
                if (game.intermezzoStorage.size > index) {
                    frontVisual = ImageVisual(tileImageLoader.getFrontImage(game.intermezzoStorage[index].id))
                    backVisual = ImageVisual(tileImageLoader.getBackImage(game.intermezzoStorage[index].id))
                    intermezzoOffer.isDisabled = !intermezzo
                } else {
                    frontVisual = ColorVisual(GameColor.chaletGreen)
                    backVisual = ColorVisual(GameColor.chaletGreen)
                    intermezzoOffer.isDisabled = true
                }
                if (flipped) flip()
            }
        }




        saganiGameScene.cardStack.apply {
            if (game.stacks.size > 0) {
                frontVisual = ImageVisual(tileImageLoader.getFrontImage(game.stacks[0].id))
                backVisual = ImageVisual(tileImageLoader.getBackImage(game.stacks[0].id))
            }
            isDisabled = (game.offerDisplay.size > 1 || intermezzo)
            showBack()
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

        saganiGameScene.skipButton.apply {
            isVisible = intermezzo
            isDisabled = !intermezzo
        }
    }


}