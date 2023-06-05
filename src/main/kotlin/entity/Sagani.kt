package entity

import java.io.File
import kotlin.random.Random.Default.nextInt

/**
 * [Sagani] is the game
 * @property lastTurn: the game state the turn before / to undo a turn
 * @property nextTurn: the game state before undo / to redo a turn
 * @property turnCount: time stamp for calculating points
 * @property intermezzo: true if the game is in an intermezzo
 * @property lastRound: true if a condition to end the game is met / game ends if the next player would be the startPlayer
 * @property startPlayer: starts the game
 * @property players: List of the players in turn order
 * @property actPlayer: the player whose turn it is
 * @property intermezzoPlayers: empty if there is no intermezzo / contains the players in turn order during an intermezzo
 * @property stacks contains all tiles that aren't part of the game yet
 * @property offerDisplay: tiles player can choose from during their turn
 * @property intermezzoStorage: tile player can choose from during an intermezzo
 */
data class Sagani(val playerNames: List<Pair<String,Color>>) {
    var lastTurn: Sagani? = null
    var nextTurn: Sagani? = null
    var turnCount: Int = 0
    var intermezzo: Boolean = false
    var lastRound: Boolean = false
    var startPlayer: Int
    val players: MutableList<Player> = mutableListOf()
    var actPlayer: Player
    val intermezzoPlayers: MutableList<Player> = mutableListOf()
    val stacks: MutableList<Tile> = mutableListOf()
    val offerDisplay: MutableList<Tile> = mutableListOf()
    val intermezzoStorage: MutableList<Tile> = mutableListOf()

    init {
        // 2 to 4 players
        require(playerNames.size in 2..4){"You need to be 2 to 4 players to play this game."}
        // no empty names
        playerNames.forEach {
            require(it.first.trim() != ""){"Each player has to have a name."}
        }
        // unique colors
        val colors: MutableList<Color> = mutableListOf()
        playerNames.forEach {
            require(!colors.contains(it.second)){"Each player has to have a unique color."}
            colors.add(it.second)
        }

        // create players
        playerNames.forEach { players.add(Player(it.first, it.second)) }
        // choose random startPlayer
        startPlayer = nextInt(players.size)
        // startPlayer is first actPlayer
        actPlayer = players[startPlayer]
        // create stacks with all possible tiles from .csv-file
        createStacks()
        // shuffle tiles
        stacks.shuffle()
        // fill offerDisplay with 5 tiles
        repeat(5){
            offerDisplay.add(stacks.removeFirst())
        }
    }

    private fun createStacks(){
        // read each line of .csv-file
        val lines = File("tiles_colornames_v2.csv").readLines()
        val tiles: MutableList<List<String>> = mutableListOf()
        // split each line
        lines.forEach { line -> tiles.add(line.split(","))}
        // create a tile for each line
        for(line in 1..72){
            val arrows: MutableList<Arrow> = mutableListOf()
            for(row in 2..9){
                if(tiles[line][row] != "NONE"){
                    arrows.add(Arrow(
                        element = Element.valueOf(tiles[line][row]),
                        direction = Direction.values()[row-2]
                    ))
                }
            }
            stacks.add(Tile(
                points = tiles[line][10].toInt(),
                element = Element.valueOf(tiles[line][1]),
                arrows = arrows
            ))
        }
    }
}