package view

import Location
import edu.udo.cs.sopra.ntf.ConnectionState
import entity.Player
import entity.Tile
import service.RootService
import tools.aqua.bgw.core.Scene
import tools.aqua.bgw.visual.ImageVisual
import view.scene.SaganiGameScene

/**
 * [Refreshable] enables to refresh GUI in service layer functions
 */
interface Refreshable {
    /**
     * refresh Gui after startNewGame()
     */
    fun refreshAfterStartNewGame(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {}

    /**
     * refresh Gui after ChangeToNextPlayers()
     * @param player: next player to place a tile
     * @param validLocations: Set of all valid locations for next player's board
     */
    fun refreshAfterChangeToNextPlayer(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {}


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

    /**
     * refresh Gui after a connection state change
     *
     * @param newState The new connection state
     */
    fun refreshAfterConnectionStateChange(newState: ConnectionState) {}

    /**
     * refresh Gui after a player connects or disconnects.
     *
     * @param currentPlayers The list of players after the change
     */
    fun refreshAfterPlayerListChange(currentPlayers: List<String>) {}
}
