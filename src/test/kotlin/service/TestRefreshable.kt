package service

import view.Refreshable

/**
 * Tests use [TestRefreshable] to check if functions are called.
 */
class TestRefreshable : Refreshable {
    var refreshAfterStartNewGameCalled: Boolean = false
        private set

    /**
     * reset all properties
     */
    fun reset() {
        refreshAfterStartNewGameCalled = false
    }

    override fun refreshAfterStartNewGame() {
        refreshAfterStartNewGameCalled = true
    }
}