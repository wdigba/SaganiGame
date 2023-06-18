package view

import java.awt.Color

/**
 * Enum of colors used in the gui
 */
class Color(r: Int, g: Int, b: Int) : Color(r, g, b) {

    companion object {
        val chaletGreen = Color(96, 108, 56)
        val cornSilk = Color(254, 250, 224)
        val paleLeaf = Color(200, 202, 167)

        /**
         * The color white.  In the default sRGB space.
         */
        val white = Color(255, 255, 255)

        /**
         * The color white.  In the default sRGB space.
         * @since 1.4
         */
        val WHITE = white

        /**
         * The color light gray.  In the default sRGB space.
         */
        val lightGray = Color(192, 192, 192)

        /**
         * The color light gray.  In the default sRGB space.
         * @since 1.4
         */
        val LIGHT_GRAY = lightGray

        /**
         * The color gray.  In the default sRGB space.
         */
        val gray = Color(128, 128, 128)

        /**
         * The color gray.  In the default sRGB space.
         * @since 1.4
         */
        val GRAY = gray

        /**
         * The color dark gray.  In the default sRGB space.
         */
        val darkGray = Color(64, 64, 64)

        /**
         * The color dark gray.  In the default sRGB space.
         * @since 1.4
         */
        val DARK_GRAY = darkGray

        /**
         * The color black.  In the default sRGB space.
         */
        val black = Color(0, 0, 0)

        /**
         * The color black.  In the default sRGB space.
         * @since 1.4
         */
        val BLACK = black

        /**
         * The color red.  In the default sRGB space.
         */
        val red = Color(255, 0, 0)

        /**
         * The color red.  In the default sRGB space.
         * @since 1.4
         */
        val RED = red

        /**
         * The color pink.  In the default sRGB space.
         */
        val pink = Color(255, 175, 175)

        /**
         * The color pink.  In the default sRGB space.
         * @since 1.4
         */
        val PINK = pink

        /**
         * The color orange.  In the default sRGB space.
         */
        val orange = Color(255, 200, 0)

        /**
         * The color orange.  In the default sRGB space.
         * @since 1.4
         */
        val ORANGE = orange

        /**
         * The color yellow.  In the default sRGB space.
         */
        val yellow = Color(255, 255, 0)

        /**
         * The color yellow.  In the default sRGB space.
         * @since 1.4
         */
        val YELLOW = yellow

        /**
         * The color green.  In the default sRGB space.
         */
        val green = Color(0, 255, 0)

        /**
         * The color green.  In the default sRGB space.
         * @since 1.4
         */
        val GREEN = green

        /**
         * The color magenta.  In the default sRGB space.
         */
        val magenta = Color(255, 0, 255)

        /**
         * The color magenta.  In the default sRGB space.
         * @since 1.4
         */
        val MAGENTA = magenta

        /**
         * The color cyan.  In the default sRGB space.
         */
        val cyan = Color(0, 255, 255)

        /**
         * The color cyan.  In the default sRGB space.
         * @since 1.4
         */
        val CYAN = cyan

        /**
         * The color blue.  In the default sRGB space.
         */
        val blue = Color(0, 0, 255)

        /**
         * The color blue.  In the default sRGB space.
         * @since 1.4
         */
        val BLUE = blue
    }
}