package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.Visual

class PossibleMovements(
    val x: Int,
    val y: Int,
    width: Number = 120,
    height: Number = 120,
    visual: Visual = ColorVisual(255, 255, 255, 50)
) : Button(
    posX = x * (120 + 5) +
            1600 / 2 - 120 / 2,
    posY = y * (120 + 5) +
            838 / 2 - 120 / 2,
    width = width,
    height = height,
    visual = visual,
)
