package entity

import java.util.*

/**
 * Enum to distinguish between the eight direction an arrow can point to.
 * tileDirection contains only the four directions a tile can have.
 */
enum class Direction {
    UP,
    UP_RIGHT,
    RIGHT,
    DOWN_RIGHT,
    DOWN,
    DOWN_LEFT,
    LEFT,
    UP_LEFT;

    companion object {

        /**
         * A set of direction a tile can have
         */
        fun tileDirection(): Set<Direction> {
            return EnumSet.of(
                UP,
                RIGHT,
                DOWN,
                LEFT
            )
        }
    }
}
