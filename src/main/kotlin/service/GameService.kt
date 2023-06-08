package service

import entity.*
import java.io.File
import java.net.URL

/**
 * [GameService] provides server function for the game
 */
class GameService(private val rootService: RootService) : AbstractRefreshingService() {
    /**
     * [startNewGame] creates a new game
     */
    fun startNewGame(playerNames: List<Triple<String, Color, PlayerType>>) {
        val players: MutableList<Player> = mutableListOf()
        val stacks: MutableList<Tile> = mutableListOf()
        playerNames.forEach {
            players.add(Player(it.first, it.second, it.third))
        }
        createStacks(stacks)
        // create new game
        rootService.currentGame = Sagani(players, stacks)
        // refresh GUI
        onAllRefreshables { refreshAfterStartNewGame() }
    }

    private fun createStacks(stacks: MutableList<Tile>) {
        // read each line of .csv-file
        val url: URL? = GameService::class.java.getResource("/tiles_colornames_v2.csv")
        checkNotNull(url){"There is no file."}
        val lines = File(url.path).readLines()
        val tiles: MutableList<List<String>> = mutableListOf()
        // split each line
        lines.forEach { line -> tiles.add(line.split(",")) }
        // create a tile for each line
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
    }
}