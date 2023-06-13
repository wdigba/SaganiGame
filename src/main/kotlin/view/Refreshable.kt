package view

import edu.udo.cs.sopra.ntf.ConnectionState

/**
 * [Refreshable] enables to refresh GUI in service layer functions
 */
interface Refreshable {
    /**
     * refresh Gui after startNewGame()
     */
    fun refreshAfterStartNewGame() {}

    /**
     * refresh Gui after ChangeToNextPlayers()
     */
    fun refreshAfterChangeToNextPlayer() {}

    /**
     * refresh Gui after CalculateWinner()
     */
    fun refreshAfterCalculateWinner() {}

    /**
     * refresh Gui after CalculatePoints()
     */
    fun refreshAfterCalculatePoints() {}

    /**
     * refresh Gui after saveGame()
     */
    fun refreshAfterSaveGame() {}

    /**
     * refresh Gui after loadGame()
     */
    fun refreshAfterLoadGame() {}

    /**
     * refresh Gui after placeTile()
     */
    fun refreshAfterPlaceTile() {}

    /**
     * refresh Gui after rotateTile()
     */
    fun refreshAfterRotateTile() {}

    /**
     * refresh Gui after undo()
     */
    fun refreshAfterUndo() {}

    /**
     * refresh Gui after a connection state change
     *
     * @param newState The new connection state
     */
    fun refreshAfterConnectionStateChange(newState: ConnectionState) {}
}
