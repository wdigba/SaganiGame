package service

import entity.Sagani

class RootService {
    val gameService = GameService(this)
    val playerActionService = PlayerActionService(this)
    val kIService = KIService(this)
    val networkService = NetworkService(this)
    val currentGame: Sagani? = null
}