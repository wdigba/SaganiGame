package service

import entity.Sagani

/**
 * [RootService] allows the view layer to communicate to service layer.
 * @property gameService
 * @property playerActionService
 * @property kIService
 * @property networkService
 * @property currentGame
 */
class RootService {
    val gameService = GameService(this)
    val playerActionService = PlayerActionService(this)
    val kIService = KIService(this)
    val networkService = NetworkService(this)
    var currentGame: Sagani? = null

    /**
     * Add [refreshable] in gameService und playerActionService
     */
    private fun addRefreshable(refreshable: view.Refreshable) {
        gameService.addRefreshable(refreshable)
        playerActionService.addRefreshable(refreshable)
    }

    /**
     * Add multiple [refreshable] in gameService und playerActionService
     */
    fun addEachRefreshable(vararg refreshable: view.Refreshable) {
        refreshable.forEach { addRefreshable(it) }
    }
}