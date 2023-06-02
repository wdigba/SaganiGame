package entity

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
        playerNames.forEach { players.add(Player(it.first, it.second)) }
        startPlayer = nextInt(players.size)
        actPlayer = players[startPlayer]
    }
}