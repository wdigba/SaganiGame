package service

import edu.udo.cs.sopra.ntf.ConnectionState
import entity.*
import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * [GameService] provides server function for the game
 */
class GameService(private val rootService: RootService) : AbstractRefreshingService() {
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
        rootService.currentGame = Sagani(players, stacks)

        // send game init to network players
        rootService.networkService.client?.sendGameInit()

        // fill offer display of created game
        val game = rootService.currentGame
        checkNotNull(game)
        repeat(5) {
            game.offerDisplay.add(game.stacks.removeFirst())
        }

        // refresh GUI
        onAllRefreshables { refreshAfterStartNewGame() }
    }

    /**
     * [createStacks] reads .csv-file, creates all tiles und returns them as a list
     */
    fun createStacks(): MutableList<Tile> {
        // read each line of .csv-file
        val lines = File(GameService::class.java.getResource("/tiles_colornames_v2.csv")!!.path).readLines()
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
                            Element.valueOf(tiles[line][row]),
                            Direction.values()[row - 2]
                        )
                    )
                }
            }
            stacks.add(
                Tile(
                    tiles[line][0].toInt(),
                    tiles[line][10].toInt(),
                    Element.valueOf(tiles[line][1]),
                    arrows
                )
            )
        }
        return stacks
    }

    fun changeToNextPlayer() {}

    /**
     * [gameCopy] creates a deep copy of the entity layer and adds to currentGame.nextTurn
     * Also sets a reference to currentGame as lastTurn of copy to establish a doubly linked list
     */
    fun gameCopy() {

        // check if game exists
        val game = rootService.currentGame
        checkNotNull(game) { "currentGame was null, but gameCopy() was called" }

        // store reference to lastTurn and set to null to prevent infinite loop
        val lastGame = game.lastTurn
        game.lastTurn = null

        // allowStructuredMapKeys allows saving with a Pair(Int, Int) as map key
        val jsonBuilder = Json { allowStructuredMapKeys = true }

        // convert game to JSON string
        val gameAsJson = jsonBuilder.encodeToString(game)

        // recreate game from JSON string. This results in a equal but not same object
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

        // check if game exists
        val game = rootService.currentGame
        checkNotNull(game) { "currentGame was null, but saveGame() was called" }

        val jsonBuilder = Json { allowStructuredMapKeys = true }

        var jsonIndex = 0
        var jsonAllGameString = ""
        var gameAsJson : String

        var iterGame = game
        var iterLastGame = game.lastTurn
        var iterNextGame = game.nextTurn

        // if both are null there is only one Sagani Game object
        if ( ( iterLastGame == null ) && ( iterNextGame == null)) {

            jsonAllGameString = jsonBuilder.encodeToString(iterGame)

        }
        else {

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
        FileOutputStream(File(path)).use {
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

        val loadGame = FileInputStream(File(path)).use {
            Json.decodeFromStream<String>(it)
        }

        // Sagani strings are separated with ";" during saving
        // List starts with most current game and is descending
        var iterGame : Sagani? = null
        var newGame : Sagani
        for (gameString in loadGame.split(";")) {

            newGame = Json.decodeFromString<Sagani>(gameString)

            if (iterGame != null) {

                newGame.nextTurn = iterGame
                iterGame.lastTurn = newGame

            }
            iterGame = newGame

        }

        checkNotNull(iterGame)
        // move back to youngest (i.e. first in list) game
        while ( iterGame?.nextTurn != null ) {
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
