package view

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

}
