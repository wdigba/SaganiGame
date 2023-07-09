package entity

import kotlinx.serialization.Serializable

/**
 * Enum to distinguish between human payers and AIs
 */
@Serializable
enum class PlayerType {
    HUMAN, RANDOM_AI, BEST_AI, NETWORK_PLAYER
}
