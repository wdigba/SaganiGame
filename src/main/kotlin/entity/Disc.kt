package entity

import kotlinx.serialization.Serializable

/**
 * Enum to distinguish between the two kinds of discs
 */
@Serializable
enum class Disc {
    SOUND, CACOPHONY
}
