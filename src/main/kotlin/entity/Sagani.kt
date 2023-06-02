package entity

import kotlin.random.Random.Default.nextInt

data class Sagani(val playerNames: List<Pair<Int,Int>>) {
    var lastTurn: Sagani? = null
    var nextTurn: Sagani? = null
    var turnCount: Int = 0
    var intermezzo: Boolean = false
    var lastRound: Boolean = false
    var startPlayer: Int

    init {
        startPlayer = nextInt(playerNames.size)

    }
}