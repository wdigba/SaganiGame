package entity

import kotlinx.serialization.Serializable

/**
 * [Arrow] of a [Tile]
 * @property element of the arrow
 * @property direction the arrow points to
 * @property disc contains a disc if the arrow points to a [Tile] with the same element
 */
@Serializable
data class Arrow(val element: Element, var direction: Direction, val disc: MutableList<Disc> = mutableListOf())
