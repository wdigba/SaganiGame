package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font

/**
 * A scene that is displayed while waiting for other players to join.
 */
class NetworkWaitingForPlayers(private val rootService: RootService) :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Waiting for others",
        font = Font(size = 22)
    )

    init {
        addComponents(headlineLabel)
    }
}