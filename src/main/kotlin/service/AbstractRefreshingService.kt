package service

import view.Refreshable

/**
 * [AbstractRefreshingService] connects service layer and view layer
 */
abstract class AbstractRefreshingService {
    private val refreshables = mutableListOf<Refreshable>()

    /**
     * Add [Refreshable] to refreshable
     */
    fun addRefreshable(newRefreshable: Refreshable) {
        refreshables += newRefreshable
    }

    /**
     * refresh GUI
     */
    fun onAllRefreshables(method: Refreshable.() -> Unit) = refreshables.forEach { it.method() }
}
