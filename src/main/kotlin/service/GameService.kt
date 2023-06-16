package service

import entity.*
import java.io.File

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
            players.add(Player(it.first, it.second, it.third))
        }
        // create stacks
        val stacks = createStacks()
        stacks.shuffle()
        // create new game
        rootService.currentGame = Sagani(players, stacks)
        // refresh GUI
        onAllRefreshables { refreshAfterStartNewGame() }
    }

    /**
     * [createStacks] reads .csv-file, creates all tiles und returns them as a list
     */
    fun createStacks(fileName: String = "/tiles_colornames_v2.csv"): MutableList<Tile> {
        // read each line of .csv-file
        val lines = File(GameService::class.java.getResource(fileName)!!.path).readLines()
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
}
