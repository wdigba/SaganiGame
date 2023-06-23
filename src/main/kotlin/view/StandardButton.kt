package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.Visual

/**
 * Custum Button class that extends [Button]
 */

class StandardButton(
    posX: Number = 0,
    posY: Number = 0,
    width: Number = 200,
    height: Number = 35,
    text: String = "",
    font: Font = Font(size = 18, color = Color.cornSilk),
    alignment: Alignment = Alignment.CENTER,
    isWrapText: Boolean = false,
    visual: Visual = ColorVisual.TRANSPARENT,
) :
    Button(
        posX = posX,
        posY = posY,
        width = width,
        height = height,
        text = text,
        font = font,
        alignment = alignment,
        isWrapText = isWrapText,
        visual = visual,
) {
        init {
            componentStyle = "-fx-background-color: #606C38; -fx-background-radius: 15px"
        }
    }
//    , UIComponent()