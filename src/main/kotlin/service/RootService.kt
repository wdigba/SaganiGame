package service

class RootService {
    val gameService = GameService(this)
    val playerActionService = PlayerActionService(this)
    val kIService = KIService(this)
    val networkService = NetworkService(this)
}