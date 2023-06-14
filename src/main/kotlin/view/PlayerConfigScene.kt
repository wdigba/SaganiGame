package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

class PlayerConfigScene(private val rootService: RootService) :
    MenuScene(400, 1080), Refreshable {

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Player configuration",
        font = Font(size = 22)
    )

    private val player1Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 125,
        text = "Player 1:"
    )

    // type inference fails here, so explicit  ": TextField" is required
    // see https://discuss.kotlinlang.org/t/unexpected-type-checking-recursive-problem/6203/14
    private val player1Input: TextField = TextField(
        width = 200, height = 35,
        posX = 70, posY = 160
    ).apply {
        onKeyTyped = {
            startButton.isDisabled = this.text.isBlank() || player2Input.text.isBlank()
        }
    }

    //45
    private val player2Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
        text = "Player 2:"
    )

    //35
    // type inference fails here, so explicit  ": TextField" is required
    // see https://discuss.kotlinlang.org/t/unexpected-type-checking-recursive-problem/6203/14
    private val player2Input: TextField = TextField(
        width = 200, height = 35,
        posX = 70, posY = 240
    ).apply {
        onKeyTyped = {
            startButton.isDisabled = player1Input.text.isBlank() || this.text.isBlank()
        }
    }


    private val player3Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 285,
        text = "Player 3:"
    )

    // type inference fails here, so explicit  ": TextField" is required
    // see https://discuss.kotlinlang.org/t/unexpected-type-checking-recursive-problem/6203/14
    private val player3Input: TextField = TextField(
        width = 200, height = 35,
        posX = 70, posY = 320
    ).apply {
        onKeyTyped = {
            startButton.isDisabled = this.text.isBlank() || player2Input.text.isBlank()
        }
    }


    private val player4Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 365,
        text = "Player 4:"
    )

    // type inference fails here, so explicit  ": TextField" is required
    // see https://discuss.kotlinlang.org/t/unexpected-type-checking-recursive-problem/6203/14
    private val player4Input: TextField = TextField(
        width = 200, height = 35,
        posX = 70, posY = 400
    ).apply {
        onKeyTyped = {
            startButton.isDisabled = this.text.isBlank() || player2Input.text.isBlank()
        }
    }

    private val playerInputs = mutableListOf(player1Input,player2Input,player3Input,player4Input)

    /**
     * Button führt auf vorherige Seite und leert alle Eingabefelder
     * PROBLEM: Wenn in SoPraApplication programmiert kommt ein rekursiver Fehler
     */
    val backButton = Button(
        width = 140, height = 35,
        posX = 50, posY = 465,
        text = "GO BACK",
        font = Font(size = 18)
    ).apply {
        visual = ColorVisual(196, 187, 147)
        onMouseClicked = {
          for (input in playerInputs){
              input.text = ""
          }

        }
    }

    val startButton = Button(
        width = 140, height = 35,
        posX = 230, posY = 465,
        text = "START",
        font = Font(size = 18)
    ).apply {
        visual = ColorVisual(196, 187, 147)
        onMouseClicked = {
            val playerNames = mutableListOf<String>()
            for (input in playerInputs){
                playerNames.add(input.text.trim())
            }

            // leere Werte entfernen
            playerNames.removeIf() { it.isBlank() }
            playerNames.removeIf() { it.isEmpty() }

            //neues Spiel starten
        }
    }

    private val plusButton = Button(
        width = 35, height = 35,
        posX = 320, posY = 240,
        text = "+", font = Font(size = 14), alignment = Alignment.CENTER
    ).apply {
        visual = ColorVisual(196, 187, 147)
        onMouseClicked = {
            repositionButtonsPlus()
        }
    }

    private val minusButton = Button(
        width = 35, height = 35,
        posX = 360, posY = 320,
        text = "-", font = Font(size = 20), alignment = Alignment.CENTER
    ).apply {
        visual = ColorVisual(196, 187, 147)
        onMouseClicked = {
            repositionButtonsMinus()
        }
    }
    //background = ColorVisual(158, 181, 91)

    init {
        background = ColorVisual(255, 249, 222)
        player3Label.isVisible = false
        player3Input.isVisible = false
        player4Input.isVisible = false
        player4Label.isVisible = false
        minusButton.isVisible = false
        startButton.isDisabled = true

        opacity = .5
        addComponents(
            headlineLabel,
            player1Label, player1Input,
            player2Label, player2Input,
            player3Label, player3Input,
            player4Label, player4Input,
            startButton, backButton, plusButton, minusButton
        )
    }


    private fun repositionButtonsPlus() {
        if (!player3Label.isVisible) {
            minusButton.isVisible = true
            plusButton.reposition(320, 320)
            player3Label.isVisible = true
            player3Input.isVisible = true

        } else if (!player4Label.isVisible) {
            minusButton.reposition(360, 400)
            player4Input.isVisible = true
            player4Label.isVisible = true
            plusButton.isVisible = false
        }
    }

    private fun repositionButtonsMinus() {
        plusButton.isDisabled = false
        // blendet Felder aus
        if (player4Label.isVisible) {
            player4Label.isVisible = false
            player4Input.text = ""
            player4Input.isVisible = false
            minusButton.reposition(360, 320)
            plusButton.reposition(320, 320)
            plusButton.isVisible = true
        } else if (player3Label.isVisible) {
            player3Label.isVisible = false
            player3Input.text = ""
            player3Input.isVisible = false
            minusButton.isVisible = false
            plusButton.reposition(320, 240)
            plusButton.isVisible = true
        }
    }


}