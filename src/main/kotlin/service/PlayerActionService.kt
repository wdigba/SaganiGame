package service

import entity.*

class PlayerActionService(private val rootService: RootService): AbstractRefreshingService() {

    /**
     * Function to undo a player Action by reverting the [Sagani] game reference back to the previous one.
     * Checks if a previous game state exists and then changes the reference in the RootService object
     */
    fun undo() {

        // get reference to SaganiGame
        val game = rootService.currentGame
        checkNotNull(game) { "Game Object has not been created yet" }
        checkNotNull(game.lastTurn) { "No previous turn exists. This means this was the first game state" }

        // switch currentGame reference in rootservice to the new state
        game.currentGame = game.lastTurn

        // send update to GUI
        onAllRefreshables { refreshUndo() }
    }

    /**
     * Function to redo a player Action by changing the [Sagani] game reference back to the next one.
     * Checks if a next game state exists and then changes the reference in the RootService object
     */
    fun redo() {

        // get reference to SaganiGame
        val game = rootService.currentGame
        checkNotNull(game) { "Game Object has not been created yet" }
        checkNotNull(game.nextTurn) { "No previous turn exists. This means this was the first game state" }

        // switch currentGame reference in rootservice to the new state
        game.currentGame = game.nextTurn

        // send update to GUI
        onAllRefreshables { refreshUndo() }

    }
}