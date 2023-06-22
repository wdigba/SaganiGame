package entity


import kotlinx.serialization.Serializable
import Location


/**
 * [Player] plays the game
 * @property name of the player
 * @property color of the player's game pieces
 * @property points of the player with time stamp when the points have changed the last time
 * @property discs the player has access to without taking cacophony discs
 * @property board contains all tiles as values the player has placed during the game and their locations as keys
 * @property playerType: Player can be a human or an AI
 */
@Serializable
data class Player(val name: String, val color: Color, val playerType: PlayerType = PlayerType.HUMAN) {
    var points: Pair<Int, Int> = Pair(0, 0)
    val discs: MutableList<Disc> = mutableListOf()
    val board: MutableMap<Location, Tile> = mutableMapOf()


}
