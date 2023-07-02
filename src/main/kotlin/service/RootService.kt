package service

import entity.Sagani

/**
 * [RootService] allows the view layer to communicate to service layer.
 * @property gameService provides server functions for the game.
 * @property playerActionService provides player functions for the game.
 * @property kIService realizes AI.
 * @property networkService connects with network player
 * @property currentGame stores current game data
 */
class RootService {
    val gameService = GameService(this)
    val playerActionService = PlayerActionService(this)
    val kIService = KIService(this)
    val kIServiceRandom = KIServiceRandom(this)
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
