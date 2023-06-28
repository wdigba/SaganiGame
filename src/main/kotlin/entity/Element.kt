package entity

import kotlinx.serialization.Serializable

/**
 * Enum to distinguish between the four possible elements of tiles and arrows
 */
@Serializable
enum class Element {
    FIRE,
    WATER,
    EARTH,
    AIR
}
