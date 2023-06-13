package service

import edu.udo.cs.sopra.ntf.ConnectionState
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
    var refreshAfterCalculatePointsCalled: Boolean = false
        private set
    var refreshAfterSaveGameCalled: Boolean = false
        private set
    var refreshAfterLoadGameCalled: Boolean = false
        private set
    var refreshAfterPlaceTileCalled: Boolean = false
        private set
    var refreshAfterRotateTileCalled: Boolean = false
        private set
    var refreshAfterrefreshAfterUndoCalled: Boolean = false
        private set
    var refreshAfterConnectionStateChangeCalled: Boolean = false

    /**
     * reset all properties
     */
    fun reset() {
        refreshAfterStartNewGameCalled = false
        refreshAfterChangeToNextPlayerCalled = false
        refreshAfterCalculateWinnerCalled = false
        refreshAfterCalculatePointsCalled = false
        refreshAfterSaveGameCalled = false
        refreshAfterLoadGameCalled = false
        refreshAfterPlaceTileCalled = false
        refreshAfterRotateTileCalled = false
        refreshAfterrefreshAfterUndoCalled = false
        refreshAfterConnectionStateChangeCalled = false
    }

    /**
     * Tests can check if refreshAfterStartNewGame was called
     */
    override fun refreshAfterStartNewGame() {
        refreshAfterStartNewGameCalled = true
    }

    /**
     * Tests can check if refreshAfterChangeToNextPlayer was called
     */
    override fun refreshAfterChangeToNextPlayer() {
        refreshAfterChangeToNextPlayerCalled = true
    }

    /**
     * Tests can check if refreshAfterCalculateWinner was called
     */
    override fun refreshAfterCalculateWinner() {
        refreshAfterCalculateWinnerCalled = true
    }

    /**
     * Tests can check if refreshAfterCalculatePoints was called
     */
    override fun refreshAfterCalculatePoints() {
        refreshAfterCalculatePointsCalled = true
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
    override fun refreshAfterPlaceTile() {
        refreshAfterPlaceTileCalled = true
    }

    /**
     * Tests can check if refreshAfterRotateTile was called
     */
    override fun refreshAfterRotateTile() {
        refreshAfterRotateTileCalled = true
    }

    override fun refreshAfterUndo() {
        refreshAfterrefreshAfterUndoCalled = true
    }

    override fun refreshAfterConnectionStateChange(newState: ConnectionState) {
        refreshAfterConnectionStateChangeCalled = true
    }
}
