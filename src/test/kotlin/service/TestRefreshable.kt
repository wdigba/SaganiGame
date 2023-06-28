package service

import Location
import edu.udo.cs.sopra.ntf.ConnectionState
import entity.Player
import entity.Tile
import view.Refreshable

/**
 * Tests use [TestRefreshable] to check if functions are called.
 */
class TestRefreshable : Refreshable {
    var refreshAfterStartNewGameCalled: Boolean = false
        private set
    var refreshAfterChangeToNextPlayerCalled: Boolean = false
        private set
    var refreshAfterCalculateWinnerCalled: Boolean = false
        private set
    var refreshAfterSaveGameCalled: Boolean = false
        private set
    var refreshAfterLoadGameCalled: Boolean = false
        private set
    var refreshAfterPlaceTileCalled: Boolean = false
        private set
    var refreshAfterUndoCalled: Boolean = false
        private set
    var refreshAfterRedoCalled: Boolean = false
        private set
    var refreshAfterConnectionStateChangeCalled: Boolean = false
        private set

    /**
     * reset all properties
     */
    fun reset() {
        refreshAfterStartNewGameCalled = false
        refreshAfterChangeToNextPlayerCalled = false
        refreshAfterCalculateWinnerCalled = false
        refreshAfterSaveGameCalled = false
        refreshAfterLoadGameCalled = false
        refreshAfterPlaceTileCalled = false
        refreshAfterUndoCalled = false
        refreshAfterRedoCalled = false
        refreshAfterConnectionStateChangeCalled = false
    }

    /**
     * Tests can check if refreshAfterStartNewGame was called
     */
    override fun refreshAfterStartNewGame(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {
        refreshAfterStartNewGameCalled = true
    }

    /**
     * Tests can check if refreshAfterChangeToNextPlayer was called
     */
    override fun refreshAfterChangeToNextPlayer(player: Player, validLocations: Set<Location>, intermezzo: Boolean) {
        refreshAfterChangeToNextPlayerCalled = true
    }

    /**
     * Tests can check if refreshAfterCalculateWinner was called
     */
    override fun refreshAfterCalculateWinner() {
        refreshAfterCalculateWinnerCalled = true
    }

    /**
     * Tests can check if refreshAfterSaveGame was called
     */
    override fun refreshAfterSaveGame() {
        refreshAfterSaveGameCalled = true
    }

    /**
     * Tests can check if refreshAfterLoadGame was called
     */
    override fun refreshAfterLoadGame() {
        refreshAfterLoadGameCalled = true
    }

    /**
     * Tests can check if refreshAfterPlaceTile was called
     */
    override fun refreshAfterPlaceTile(player: Player, tile: Tile, location: Location) {
        refreshAfterPlaceTileCalled = true
    }

    /**
     * Tests can check if refreshAfterUndo was called
     */
    override fun refreshAfterUndo() {
        refreshAfterUndoCalled = true
    }

    /**
     * Tests can check if refreshAfterRedo was called
     */
    override fun refreshAfterRedo() {
        refreshAfterRedoCalled = true
    }

    override fun refreshAfterConnectionStateChange(newState: ConnectionState) {
        refreshAfterConnectionStateChangeCalled = true
    }
}
