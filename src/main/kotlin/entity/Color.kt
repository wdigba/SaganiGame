package entity

import kotlinx.serialization.Serializable

/**
 * Enum to distinguish between the four colors the players can have
 */
@Serializable
enum class Color {
    WHITE, BROWN, GREY, BLACK
}
