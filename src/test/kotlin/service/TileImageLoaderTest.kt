package service

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

/**
 * [TileImageLoaderTest] tests [TileImageLoader]
 */
class TileImageLoaderTest {
    /**
     * Test if the [TileImageLoader] can load all images
     */
    @Test
    fun testOne() {
        val tileImageLoader = TileImageLoader()
        assertFails { tileImageLoader.getFrontImage(0) }
        assertFails { tileImageLoader.getFrontImage(73) }
        assertFails { tileImageLoader.getBackImage(0) }
        assertFails { tileImageLoader.getBackImage(73) }

        //back + four elements
        assertDoesNotThrow { tileImageLoader.getBackImage(1) }
        assertDoesNotThrow { tileImageLoader.getBackImage(20)  }
        assertDoesNotThrow { tileImageLoader.getBackImage(39)  }
        assertDoesNotThrow { tileImageLoader.getBackImage(58)  }
        //front + four elements
        assertDoesNotThrow { tileImageLoader.getFrontImage(5)  }
        assertDoesNotThrow { tileImageLoader.getFrontImage(24)  }
        assertDoesNotThrow { tileImageLoader.getFrontImage(43)  }
        assertDoesNotThrow { tileImageLoader.getFrontImage(62)  }
    }
}