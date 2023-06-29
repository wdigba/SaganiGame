package entity

import kotlinx.serialization.Serializable

/**
 * [Sagani] is the game
 * @property lastTurn: the game state the turn before / to undo a turn
 * @property nextTurn: the game state before undo / to redo a turn
 * @property turnCount: time stamp for calculating points
 * @property intermezzo: true if the game is in an intermezzo
 * @property lastRound: true if a condition to end the game is met / game ends if the next player would be the startPlayer
 * @property players: List of the players in turn order
 * @property actPlayer: the player whose turn it is
 * @property intermezzoPlayers: empty if there is no intermezzo / contains the players in turn order during an intermezzo
 * @property stacks contains all tiles that aren't part of the game yet
 * @property offerDisplay: tiles player can choose from during their turn
 * @property intermezzoStorage: tile player can choose from during an intermezzo
 */
@Serializable
data class Sagani(val players: MutableList<Player>, val stacks: MutableList<Tile>) {
    var lastTurn: Sagani? = null
    var nextTurn: Sagani? = null
    var turnCount: Int = 0
    var intermezzo: Boolean = false
    var lastRound: Boolean = false
    var actPlayer: Player = players[0]
    val intermezzoPlayers: MutableList<Player> = mutableListOf()
    val offerDisplay: MutableList<Tile> = mutableListOf()
    val intermezzoStorage: MutableList<Tile> = mutableListOf()

    init {
        // 2 to 4 players
        require(players.size in 2..4) { "You need to be 2 to 4 players to play this game." }
        // no empty names
        players.forEach {
            require(it.name.trim() != "") { "Each player has to have a name." }
        }
        // unique colors
        val colors: MutableList<Color> = mutableListOf()
        players.forEach {
            require(it.color !in colors) { "Each player has to have a unique color." }
            colors.add(it.color)
        }

        // unique tiles
        val tileIDs: MutableList<Int> = mutableListOf()
        stacks.forEach {
            require(it.id !in tileIDs) { "Tile IDs are not unique." }
            require(it.id in 1..72) { "Illegal tile id." }
            tileIDs.add(it.id)
        }

    }

}
