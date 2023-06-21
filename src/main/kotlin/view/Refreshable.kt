package view

import Location
import entity.Player
import entity.Tile

/**
 * [Refreshable] enables to refresh GUI in service layer functions
 */
interface Refreshable {
    /**
     * refresh Gui after startNewGame()
     */
    fun refreshAfterStartNewGame(player: Player, validLocation: Set<Location>, intermezzo: Boolean) {}

    /**
     * refresh Gui after ChangeToNextPlayers()
     * @param player: next player to place a tile
     * @param validLocation: Set of all valid locations for next player's board
     */
    fun refreshAfterChangeToNextPlayer(player: Player, validLocation: Set<Location>, intermezzo: Boolean) {}

    /**
     * refresh Gui after CalculateWinner()
     */
    fun refreshAfterCalculateWinner() {}

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
     * @param player who placed the tile
     * @param tile that was placed
     * @param location where the tile was placed
     */
    fun refreshAfterPlaceTile(player: Player, tile: Tile, location: Location) {}

    /**
     * refresh Gui after undo()
     */
    fun refreshAfterUndo() {}

    /**
     * refresh Gui after redo()
     */
    fun refreshAfterRedo() {}
}
