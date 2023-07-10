package service

import Location
import edu.udo.cs.sopra.ntf.ConnectionState
import entity.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import tools.aqua.bgw.core.BoardGameApplication
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Integer.min
import java.net.URI
import kotlin.concurrent.thread

/**
 * [GameService] provides server function for the game
 */
class GameService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * The time in milliseconds that the AI waits before calculating its move.
     * Default is the medium speed setting representing 2000ms.
     */
    var simulationTime = 2000

    /**
     * [startNewGame] creates a new game
     */
    fun startNewGame(playerNames: List<Triple<String, Color, PlayerType>>) {
        // create players
        val players: MutableList<Player> = mutableListOf()
        playerNames.forEach {
            val player = Player(it.first, it.second, it.third)
            // add sound discs
            repeat(24) { player.discs.add(Disc.SOUND) }
            players.add(player)
        }

        // create stacks
        val stacks = createStacks()
        stacks.shuffle()

        // create new game
        val game = Sagani(players, stacks)
        rootService.currentGame = game

        // send game init to network players
        rootService.networkService.client?.sendGameInit()

        // fill offer display
        repeat(5) {
            game.offerDisplay.add(game.stacks.removeFirst())
        }

        // refresh GUI
        onAllRefreshables {
            refreshAfterStartNewGame(
                game.players[0], setOf(Location(0, 0)), false
            )
        }


        // If the start player is an AI, calculate its move
        if (game.players[0].playerType == PlayerType.RANDOM_AI) {
            Thread.sleep(simulationTime.toLong())
            rootService.kIServiceRandom.calculateRandomMove()
        } else if (game.players[0].playerType == PlayerType.BEST_AI) {
            Thread.sleep(simulationTime.toLong())
            rootService.kIService.playBestMove()
        }
    }

    /**
     * [createStacks] reads .csv-file, creates all tiles und returns them as a list
     */
    fun createStacks(): MutableList<Tile> {
        // read each line of .csv-file
//        val lines = File(GameService::class.java.getResource("/tiles_colornames_v2.csv")!!.path).readLines()
        val url = GameService::class.java.getResource("/tiles_colornames_v2.csv")!!.path
        // Convert url to uri to which will remove the %20 (space) characters:
        val uri = URI(url.toString())
        val lines = File(uri.path).readLines()


        val tiles: MutableList<List<String>> = mutableListOf()
        // split each line
        lines.forEach { line -> tiles.add(line.split(",")) }
        // create a tile for each line
        val stacks = mutableListOf<Tile>()
        for (line in 1..72) {
            val arrows: MutableList<Arrow> = mutableListOf()
            for (row in 2..9) {
                if (tiles[line][row] != "NONE") {
                    arrows.add(
                        Arrow(
                            Element.valueOf(tiles[line][row]), Direction.values()[row - 2]
                        )
                    )
                }
            }
            stacks.add(
                Tile(
                    tiles[line][0].toInt(), tiles[line][10].toInt(), Element.valueOf(tiles[line][1]), arrows
                )
            )
        }
        return stacks
    }

    /**
     * [calculatePoints] precalculates a move without writing in the entity-layer
     *     @param player that wants to precalculate a move
     *     @param newTile that the player potentially wants to place
     *     @param tileDirection the direction in which the player wants to potentially place newTile
     *     @param location the location in which the player wants to potentially place newTile
     *     @return a mutableList containing
     *          the points the player would have
     *          the sound disc difference of the move
     *          the amount of cacophony discs, the player would have to buy
     *          the arrows, that are being fulfilled, including the arrows of newTile
     */
    fun calculatePoints(
        player: Player, newTile: Tile, tileDirection: Direction, location: Location
    ): MutableList<Int> {
        // Get old values
        val discPile = player.discs.size
        val points = player.points.first
        // New Counter
        var newPoints = points
        var newDiscPile = discPile
        val arrowCount = mutableListOf<Pair<Arrow, Int>>()
        var boughtCacoDiscs = 0
        //collect all newly fulfilled arrows in a list
        for (direction in Direction.values()) {
            var filteredBoard = rootService.playerActionService.filterInDirection(player.board, direction, location)
            filteredBoard = filteredBoard.filterValues {
                it.arrows.contains(
                    Arrow(
                        newTile.element,
                        Direction.values()[(direction.ordinal + 4) % 8]
                    )
                ) && !it.flipped
            }
            filteredBoard.values.forEach {
                it.arrows.forEach { arrow ->
                    if (arrow.direction == Direction.values()[(direction.ordinal + 4) % 8] && arrow.disc.isEmpty()) {
                        arrowCount.add(Pair(arrow, it.id))
                    }
                }
            }
        }
        //add points for newly fulfilled tiles, add freed discs to the pile
        for (tile in player.board) {
            if (!tile.value.flipped) {
                val idSortedArrows = arrowCount.filter { it.second == tile.value.id }
                if (tile.value.discs.size - idSortedArrows.size == 0) {
                    newPoints += tile.value.points
                    newDiscPile += tile.value.arrows.size
                }
            }
        }
        // put discs on the new Tile
        repeat(newTile.arrows.size) {
            if (newDiscPile > 0) {
                newDiscPile--
            } else {
                boughtCacoDiscs++
            }
        }
        newPoints -= boughtCacoDiscs * 2
        newTile.rotate(tileDirection)

        //check if arrows of the new Tile are fulfilled
        for (arrow in newTile.arrows) {
            var filteredBoard =
                rootService.playerActionService.filterInDirection(player.board, arrow.direction, location)
            filteredBoard = filteredBoard.filterValues { it.element == arrow.element }
            if (filteredBoard.isNotEmpty()) {
                arrowCount.add(Pair(arrow, newTile.id))
            }
        }
        //add points and put the discs back, if all arrows of the new Tile are fulfilled
        val idSortedArrows = arrowCount.filter { it.second == newTile.id }
        if (newTile.discs.size + idSortedArrows.size == newTile.arrows.size) {
            newPoints += newTile.points
            newDiscPile += newTile.arrows.size
        }

        /*
            return a list containing
                the new amount of points
                the sound disc difference
                the amount of bought cacophony discs
                the amount of newly fulfilled arrows
             */
        return mutableListOf(newPoints, newDiscPile - discPile - boughtCacoDiscs, boughtCacoDiscs, arrowCount.size)
    }

    /**
     * [changeToNextPlayer] is called after each player's turn.
     * Checks if an intermezzo has to start or to end.
     * Refills empty offerDisplay and checks if it is the lastRound.
     * Determines next player.
     * Increases turnCount and copies game state.
     * Checks if the game has to end.
     */
    fun changeToNextPlayer() {
        var nextPlayer: Player? = null
        val validLocations: Set<Location>
        val currentGame = rootService.currentGame
        checkNotNull(currentGame) { "There is no game." }

        // identify player
        val player = if (currentGame.intermezzo) {
            currentGame.players.find { it.color == currentGame.intermezzoPlayers[0].color }!!
        } else {
            currentGame.players.find { it.color == currentGame.actPlayer.color }!!
        }
        


        val previousRoundIntermezzo = !currentGame.intermezzo
        val previousRoundLastRound = !currentGame.lastRound

        // check if intermezzo has to start/end
        if (currentGame.intermezzo) {
            // remove first player who had their intermezzo turn already
            currentGame.intermezzoPlayers.removeFirst()
            if (currentGame.intermezzoPlayers.isNotEmpty()) {
                nextPlayer = currentGame.intermezzoPlayers[0]
            } else {
                currentGame.intermezzo = false
                if (currentGame.intermezzoStorage.size == 4) {
                    currentGame.intermezzoStorage.clear()
                }
            }
        } else {
            if (currentGame.intermezzoStorage.size == 4) {
                currentGame.intermezzo = true
                currentGame.intermezzoPlayers.addAll(currentGame.players)
                currentGame.intermezzoPlayers.sortByDescending { it.points.second }
                currentGame.intermezzoPlayers.sortBy { it.points.first }
                nextPlayer = currentGame.intermezzoPlayers[0]
            }
        }

        // if no intermezzo
        if (!currentGame.intermezzo) {
            // check if offerDisplay has to be refilled
            if (currentGame.offerDisplay.isEmpty()) {
                val numberOfNewTiles = min(5, currentGame.stacks.size)
                repeat(numberOfNewTiles) {
                    currentGame.offerDisplay.add(currentGame.stacks.removeFirst())
                }
                if (currentGame.stacks.size < 5) {
                    currentGame.lastRound = true
                }
            }
        }
        // check if player has needed amount of points to end the game
        currentGame.players.forEach {
            if (it.points.first >= 105 - currentGame.players.size * 15) {
                currentGame.lastRound = true
            }
        }

        if (rootService.networkService.connectionState == ConnectionState.PLAYING_MY_TURN) {
            rootService.networkService.client?.sendTurnMessage(player)
        } else {
            // Check Checksum
            rootService.networkService.client?.lastTurnChecksum?.let {
                check(it.score == player.points.first) {
                    "Checksum: Score did not match. ${it.score} != ${player.points.first}"
                }
                check(it.availableDiscs == player.discs.size) {
                    "Checksum: Available discs did not match. ${it.availableDiscs} != ${player.discs.size}"
                }
                val startedIntermezzo = previousRoundIntermezzo && currentGame.intermezzo
                check(it.startedIntermezzo == startedIntermezzo) {
                    "Checksum: Intermezzo did not match. ${it.startedIntermezzo} != $startedIntermezzo"
                }
                val initiatedLastRound = previousRoundLastRound && currentGame.lastRound
                check(it.initiatedLastRound == initiatedLastRound) {
                    "Checksum: Last round did not match. ${it.initiatedLastRound} != $initiatedLastRound"
                }
                rootService.networkService.client?.lastTurnChecksum = null
            }
        }

        // determine next player
        if (nextPlayer == null) {
            nextPlayer =
                currentGame.players[(currentGame.players.indexOf(currentGame.actPlayer) + 1) % currentGame.players.size]
            currentGame.actPlayer = nextPlayer
        }

        if (rootService.networkService.connectionState != ConnectionState.DISCONNECTED) {
            if (nextPlayer.name == rootService.networkService.client?.playerName) {
                rootService.networkService.connectionState = ConnectionState.PLAYING_MY_TURN
            } else {
                rootService.networkService.connectionState = ConnectionState.WAITING_FOR_OPPONENTS
            }
        }

        // increase turnCount
        currentGame.turnCount++

        // check if game has to end
        if (
            currentGame.lastRound &&
            currentGame.players.indexOf(currentGame.actPlayer) == 0 &&
            !currentGame.intermezzo
        ) {
            calculateWinner()
        } else {
            // Add a delay so the player can see the move of the AI
            // But only if the player is playing a single player game to not break the UI in multiplayer
            if (rootService.networkService.connectionState == ConnectionState.DISCONNECTED) {
                //Thread.sleep(simulationTime.toLong() / 2)
            }
            validLocations = rootService.playerActionService.validLocations(nextPlayer.board)

            onAllRefreshables { refreshAfterChangeToNextPlayer(nextPlayer, validLocations, currentGame.intermezzo) }

            if (nextPlayer.playerType == PlayerType.RANDOM_AI || nextPlayer.playerType == PlayerType.BEST_AI) {
                Thread {
                    // Simulate the AI's "thinking" time
                    Thread.sleep(simulationTime.toLong()/2)
                    BoardGameApplication.runOnGUIThread {
                        if (nextPlayer.playerType == PlayerType.RANDOM_AI) {
                            rootService.kIServiceRandom.calculateRandomMove()
                        } else if (nextPlayer.playerType == PlayerType.BEST_AI) {
                            rootService.kIService.playBestMove()
                        }
                    }


                    // No need to refresh or switch to UI thread here since it will be handled
                    // in `playBestMove()` or `calculateRandomMove()`

                }.start()
            }






        }
    }

    /**
     * [calculateWinner] sorts the playerlist by points and the time they reach points
     * If a player reaches the same point count as another he still has less, because he got there later
     * This method will only be called when the game ends, since
     */
    fun calculateWinner() {

        // get game reference
        val game = rootService.currentGame
        checkNotNull(game)

        // sort player list by points and turncount they reached point
        game.players.sortWith(compareBy({ -it.points.first }, { it.points.second }))

        // update GUI
        onAllRefreshables { refreshAfterCalculateWinner() }
        rootService.networkService.disconnect()
    }

    /**
     * [gameCopy] creates a deep copy of the entity layer and adds to currentGame.nextTurn
     * Also sets a reference to currentGame as lastTurn of copy to establish a doubly linked list
     */
    fun gameCopy() {

        // check if game exists
        val game = rootService.currentGame
        checkNotNull(game) { "currentGame was null, but gameCopy() was called" }

        // delete nextTurn
        game.nextTurn = null

        // store reference to lastTurn and set to null to prevent infinite loop
        val lastGame = game.lastTurn
        game.lastTurn = null

        // allowStructuredMapKeys allows saving with a Pair(Int, Int) as map key
        val jsonBuilder = Json { allowStructuredMapKeys = true }

        // convert game to JSON string
        val gameAsJson = jsonBuilder.encodeToString(game)

        // recreate game from JSON string. This results in an equal but not same object
        // i.e. the new object will be a different instance
        val gameFromJson = jsonBuilder.decodeFromString<Sagani>(gameAsJson)

        // re-establish link from the game that was copied to its lastGame
        game.lastTurn = lastGame
        // make backlink to game for doubly linked list
        gameFromJson.lastTurn = game

        // write new game to nextTurn property of Sagani
        game.nextTurn = gameFromJson

        // set currentGame pointer to new copy
        rootService.currentGame = gameFromJson


    }

    /**
     * [saveGame] saves the current Entity layer, so the [Sagani] object at rootService.currentGame
     * to a file at the specified path
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun saveGame(path: String) {
        val file = File(path)

        if (!file.exists()) {
            file.createNewFile()
        }

        // check if game exists
        val game = rootService.currentGame
        checkNotNull(game) { "currentGame was null, but saveGame() was called" }

        val jsonBuilder = Json { allowStructuredMapKeys = true }

        var jsonIndex = 0
        var jsonAllGameString = ""
        var gameAsJson: String

        var iterGame = game
        var iterLastGame = game.lastTurn
        var iterNextGame = game.nextTurn

        // if both are null there is only one Sagani Game object
        if ((iterLastGame == null) && (iterNextGame == null)) {

            jsonAllGameString = jsonBuilder.encodeToString(iterGame)

        } else {

            while (iterLastGame != null) {

                // store references
                iterLastGame = iterGame?.lastTurn
                iterNextGame = iterGame?.nextTurn
                // cut links to prevent infinite loop
                iterGame?.lastTurn = null
                iterGame?.nextTurn = null
                // add iterGame to JSON string
                gameAsJson = jsonBuilder.encodeToString(iterGame)
                jsonAllGameString += gameAsJson
                if (iterLastGame != null) {
                    jsonAllGameString += ";"
                }


                // re-establish links, move pointer and increase index (key in json
                iterGame?.nextTurn = iterNextGame
                iterGame?.lastTurn = iterLastGame
                iterGame = iterGame?.lastTurn
                jsonIndex += 1

            }
        }

        // write json encoding of game to file specified by parameter path
        FileOutputStream(file).use {
            jsonBuilder.encodeToStream(jsonAllGameString, it)
        }

        // refresh GUI
        onAllRefreshables { refreshAfterSaveGame() }
    }

    /**
     * [loadGame] loads a Jsom file of a [Sagani] game object and stores it in rootService.currentGame
     * from a specified path
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun loadGame(path: String) {
        check(rootService.networkService.connectionState == ConnectionState.DISCONNECTED) {
            "Cannot load game while connected to server"
        }

        val jsonBuilder = Json { allowStructuredMapKeys = true }
        //
        val loadGame = FileInputStream(File(path)).use {
            jsonBuilder.decodeFromStream<String>(it)
        }

        // Sagani strings are separated with ";" during saving
        // List starts with most current game and is descending
        var iterGame: Sagani? = null
        var newGame: Sagani
        for (gameString in loadGame.split(";")) {

            newGame = jsonBuilder.decodeFromString(gameString)

            if (iterGame != null) {

                newGame.nextTurn = iterGame
                iterGame.lastTurn = newGame

            }
            iterGame = newGame

        }

        checkNotNull(iterGame)
        // move back to youngest (i.e. first in list) game
        while (iterGame?.nextTurn != null) {
            iterGame = iterGame.nextTurn
        }

        rootService.currentGame = iterGame

        // refresh GUI
        onAllRefreshables { refreshAfterLoadGame() }
    }

    /**
     * [undo] Checks if a previous instance of [Sagani] exists, which would be stored in the property
     * Sagani.lastTurn. If so set currentGame reference to this object
     */
    fun undo() {
        check(rootService.networkService.connectionState == ConnectionState.DISCONNECTED) {
            "Cannot undo while connected to server"
        }

        val game = rootService.currentGame
        checkNotNull(game) { "undo() was called but currentGame is null" }

        if (game.lastTurn != null) {
            rootService.currentGame = game.lastTurn
        }


        // refresh GUI
        onAllRefreshables { refreshAfterUndo() }
    }

    /**
     * [redo] Checks if a next instance of [Sagani] exists, which would be stored in the property
     * Sagani.nextTurn. If so set currentGame reference to this object
     */
    fun redo() {
        check(rootService.networkService.connectionState == ConnectionState.DISCONNECTED) {
            "Cannot redo while connected to server"
        }

        val game = rootService.currentGame
        checkNotNull(game) { "redo() was called but currentGame is null" }

        if (game.nextTurn != null) {
            rootService.currentGame = game.nextTurn
        }

        // refresh GUI
        onAllRefreshables { refreshAfterRedo() }
    }

}
