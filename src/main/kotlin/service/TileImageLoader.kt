package service

import entity.Element
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

private const val airBack = "/AIR_BACK.jpg"
private const val airFront = "/AIR_FRONT.jpg"
private const val earthBack = "/EARTH_BACK.jpg"
private const val earthFront = "/EARTH_FRONT.jpg"
private const val fireBack = "/FIRE_BACK.jpg"
private const val fireFront = "/FIRE_FRONT.jpg"
private const val waterBack = "/WATER_BACK.jpg"
private const val waterFront = "/WATER_FRONT.jpg"
private const val IMG_HEIGHT = 610
private const val IMG_WIDTH = 610

/**
 * The TileImageLoader has two usable functions to return the front or back image of a tile with the
 * given ID.
 */
class TileImageLoader {
    /**
     * returns a [BufferedImage] of the backside(without arrows) of the Tile with the given id
     * @param id of the tile
     * @return [BufferedImage] of the tile's back image
     */
    fun getBackImage(id: Int) : BufferedImage{
        //get the element of the tile
        val element = getElement(id)
        //get the image containing the tile
        val image = getImage(element, true)
        //get the position of the tile
        val position = getPosition(id)
        //cut and return the image of the tile
        return image.getSubimage(position.first * IMG_WIDTH,position.second * IMG_HEIGHT, IMG_WIDTH, IMG_HEIGHT)
    }
    /**
     * returns a [BufferedImage] of the front(with arrows) of the Tile with the given id
     * @param id of the tile
     * @return [BufferedImage] of the tile's front image
     */
    fun getFrontImage(id: Int) : BufferedImage{
        //get the element of the tile
        val element = getElement(id)
        //get the image containing the tile
        val image = getImage(element, false)
        //get the position of the tile
        val position = getPosition(id)
        //cut and return the image of the tile
        return image.getSubimage(position.first * IMG_WIDTH,position.second * IMG_HEIGHT, IMG_WIDTH, IMG_HEIGHT)
    }
    /*
    returns an image containing all tiles the given element and side
    flipped = true: back
    flipped = false: front
     */
    private fun getImage(element: Element, flipped: Boolean): BufferedImage{
        if (flipped){
            return when(element){
                Element.AIR -> ImageIO.read(TileImageLoader::class.java.getResource(airBack))
                Element.EARTH -> ImageIO.read(TileImageLoader::class.java.getResource(earthBack))
                Element.FIRE -> ImageIO.read(TileImageLoader::class.java.getResource(fireBack))
                Element.WATER -> ImageIO.read(TileImageLoader::class.java.getResource(waterBack))
            }
        }
        else{
            return when(element){
                Element.AIR -> ImageIO.read(TileImageLoader::class.java.getResource(airFront))
                Element.EARTH -> ImageIO.read(TileImageLoader::class.java.getResource(earthFront))
                Element.FIRE -> ImageIO.read(TileImageLoader::class.java.getResource(fireFront))
                Element.WATER -> ImageIO.read(TileImageLoader::class.java.getResource(waterFront))
            }
        }
    }
    /*
    returns the Element of the Tile with that id
     */
    private fun getElement(id: Int): Element{
        require(id < 73){"ID has to be 1-72"}
        require(id > 0){"ID has to be 1-72"}
        return when(id){
            in 1..18 -> Element.FIRE
            in 19..36 -> Element.EARTH
            in 37..54 -> Element.WATER
            else -> Element.AIR
        }
    }
    /*
    returns the position of the tile on the image
     */
    private fun getPosition(id: Int): Pair<Int, Int>{
        require(id < 73){"ID has to be 1-72"}
        require(id > 0){"ID has to be 1-72"}
        val cardNumber = (id -1) %18
        val a = when(cardNumber){
            0,10 -> 0
            1,11 -> 1
            2,12 -> 2
            3,13 -> 3
            4,14 -> 4
            5,15 -> 5
            6,16 -> 6
            7,17 -> 7
            8 -> 8
            else -> 9
        }
        val b = when(cardNumber){
            in 0..9 -> 0
            else -> 1
        }
        return Pair(a,b)
    }
}
