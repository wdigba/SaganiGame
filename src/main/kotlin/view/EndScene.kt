package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * [EndScene] defines scene after game ends
 */
class EndScene(private val rootService: RootService) :
    MenuScene(400, 1080), Refreshable {

    private val winnerLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        font = Font(size = 22)
    )
    private val Player1Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
    )
    private val Player2Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 255,
    )
    private val Player3Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 305,
    )
    private val Player4Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 355,
    )
    /**
     * fun [showPlayerPoints] defines list of player's points at the vey end of the game
     */
    private fun showPlayerPoints(){
        val game = rootService.currentGame
        checkNotNull(game) {"No started game found."}

        val playerLabels = listOf(Player1Label, Player2Label, Player3Label, Player4Label)

        val sortedPlayers = game.players.sortedByDescending{it.points.first}

        winnerLabel.text = "${sortedPlayers[0].name.uppercase()} WON!"
        for( i in 0 until sortedPlayers.size ){
            playerLabels[i].text = "${sortedPlayers[i].name}:         " +
                    "${sortedPlayers[i].points} points"

        }
        for (i in 0 until 4){
            playerLabels[i].isVisible =i<sortedPlayers.size
        }

    }

    /**
     * fun [onAllRefreshables] clears the labels after creating a new game (so that if you play another round the points table is empty)
     */
    fun onAllRefreshables() {
        Player1Label.text = ""

        Player2Label.text = ""

        Player3Label.text = ""

        Player4Label.text = ""
        showPlayerPoints()
    }

    val QuitButton = StandardButton(
        posX = 50, posY = 465,
        width = 140,
        text = "Quit",
    )

    val newGameButton = StandardButton(
        posX = 230, posY = 465,
        width = 140,
        text = "New game")

    init {
        opacity = 1.0

        background = ColorVisual(GameColor.cornSilk)
        addComponents(winnerLabel, Player4Label, Player3Label, Player2Label, Player1Label, QuitButton, newGameButton)
    }




}