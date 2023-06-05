package entity

/**
 * [Player] plays the game
 * @property name of the player
 * @property color of the player's game pieces
 * @property points of the play with time stamp when the points have changed the last time
 * @property discs the player has access to without taking cacophony discs
 * @property board contains all tiles as values the player has placed during the game and their locations as keys
 */
data class Player(val name: String, val color: Color) {
    var points: Pair<Int, Int> = Pair(0, 0)
    val discs: MutableList<Disc> = mutableListOf()
    val board: Map<Pair<Int, Int>, Tile> = mapOf()

    init {
        // each player starts with 24 sound discs
        repeat(24) {
            discs.add(Disc.SOUND)
        }
    }
}
