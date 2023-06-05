package entity

import entity.Direction.Companion.tileDirection

/**
 * Players place [Tile] on their board during the game
 * @property points the tile gives when flipped
 * @property element of the tile
 * @property direction of the tile
 * @property arrows: each tile has 1 to 4 arrows
 * @property discs: List of discs that are on the tile but not on an arrow of the tile
 * @property flipped: tile gets flipped when it is solved
 */
data class Tile(
    val points: Int,
    val element: Element,
    var direction: Direction = Direction.UP,
    val arrows: List<Arrow>,
    val discs: MutableList<Disc> = mutableListOf()
){
    var flipped: Boolean = false
    init {
        require(direction in tileDirection()){"Illegal direction."}
        require(arrows.size in 1..4){"Each tile has to have 1 to 4 arrows."}
        require(points == when(arrows.size){
            1 -> 1
            2 -> 3
            3 -> 6
            else -> 10
        }){"Points don't match with number of arrows."}
    }
    /**
     * [rotate] changes the direction of the tile to the new direction
     * Each arrow's direction is adjusted to the new direction.
     */
    fun rotate(newDirection: Direction = Direction.values()[(direction.ordinal + 2) % 8]){
        // tile needs to have a tileDirection
        require(newDirection in tileDirection()){"Illegal direction."}
        // calculate rotation
        val rotation = Direction.values()[(newDirection.ordinal - direction.ordinal + 8) % 8]
        // rotate each arrow
        arrows.forEach {
            it.direction = Direction.values()[(it.direction.ordinal + rotation.ordinal) % 8]
        }
        // tile gets the newDirection
        direction = newDirection
    }
}
