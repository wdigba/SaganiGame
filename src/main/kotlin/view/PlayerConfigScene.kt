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

    private val p1Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 125,
        text = "Player 1:"
    )

    // type inference fails here, so explicit  ": TextField" is required
    // see https://discuss.kotlinlang.org/t/unexpected-type-checking-recursive-problem/6203/14
    private val p1Input: TextField = TextField(
        width = 200, height = 35,
        posX = 70, posY = 160
    ).apply {
        onKeyTyped = {
            startButton.isDisabled = this.text.isBlank() || p2Input.text.isBlank()
        }
    }

    //45
    private val p2Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 205,
        text = "Player 2:"
    )

    //35
    // type inference fails here, so explicit  ": TextField" is required
    // see https://discuss.kotlinlang.org/t/unexpected-type-checking-recursive-problem/6203/14
    private val p2Input: TextField = TextField(
        width = 200, height = 35,
        posX = 70, posY = 240
    ).apply {
        onKeyTyped = {
            startButton.isDisabled = p1Input.text.isBlank() || this.text.isBlank()
        }
    }


    private val p3Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 285,
        text = "Player 3:"
    )

    // type inference fails here, so explicit  ": TextField" is required
    // see https://discuss.kotlinlang.org/t/unexpected-type-checking-recursive-problem/6203/14
    private val p3Input: TextField = TextField(
        width = 200, height = 35,
        posX = 70, posY = 320
    ).apply {
        onKeyTyped = {
            startButton.isDisabled = this.text.isBlank() || p2Input.text.isBlank()
        }
    }


    private val p4Label = Label(
        width = 100, height = 35,
        posX = 50, posY = 365,
        text = "Player 4:"
    )

    // type inference fails here, so explicit  ": TextField" is required
    // see https://discuss.kotlinlang.org/t/unexpected-type-checking-recursive-problem/6203/14
    private val p4Input: TextField = TextField(
        width = 200, height = 35,
        posX = 70, posY = 400
    ).apply {
        onKeyTyped = {
            startButton.isDisabled = this.text.isBlank() || p2Input.text.isBlank()
        }
    }

    val quitButton = Button(
        width = 140, height = 35,
        posX = 130, posY = 510,
        text = "Quit",
        font = Font(size = 18)
    ).apply {
        visual = ColorVisual(221, 136, 136)
    }

    private val startButton = Button(
        width = 140, height = 35,
        posX = 130, posY = 465,
        text = "Start",
        font = Font(size = 18)
    ).apply {
        visual = ColorVisual(136, 221, 136)
        onMouseClicked = {

            val players =
                mutableListOf(p1Input.text.trim(), p2Input.text.trim(), p3Input.text.trim(), p4Input.text.trim())

            // leere Werte entfernen
            players.removeIf() { it.isBlank() }
            players.removeIf() { it.isEmpty() }

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
        background = ColorVisual(255,249,222)
        p3Label.isVisible = false
        p3Input.isVisible = false
        p4Input.isVisible = false
        p4Label.isVisible = false
        minusButton.isVisible = false
        startButton.isDisabled = true

        opacity = .5
        addComponents(
            headlineLabel,
            p1Label, p1Input,
            p2Label, p2Input,
            p3Label, p3Input,
            p4Label, p4Input,
            startButton, quitButton, plusButton, minusButton
        )
    }


    private fun repositionButtonsPlus() {
        if (!p3Label.isVisible) {
            minusButton.isVisible = true
            plusButton.reposition(320,320)
            p3Label.isVisible = true
            p3Input.isVisible = true

        } else if (!p4Label.isVisible) {
            minusButton.reposition(360, 400)
            p4Input.isVisible = true
            p4Label.isVisible = true
            plusButton.isVisible = false
        }
    }

    private fun repositionButtonsMinus() {
        plusButton.isDisabled = false
        // blendet Felder aus
        if (p4Label.isVisible) {
            p4Label.isVisible = false
            p4Input.text = ""
            p4Input.isVisible = false
            minusButton.reposition(360, 320)
            plusButton.reposition(320,320)
            plusButton.isVisible=true
        } else if (p3Label.isVisible) {
            p3Label.isVisible = false
            p3Input.text = ""
            p3Input.isVisible = false
            minusButton.isVisible = false
            plusButton.reposition(320,240)
            plusButton.isVisible = true
        }
    }



}