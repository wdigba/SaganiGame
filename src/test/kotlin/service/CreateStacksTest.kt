package service

import java.io.FileNotFoundException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * [CreateStacksTest] tests [GameService].createStacks()
 */
class CreateStacksTest {
    private val rootService = RootService()

    /**
     * correct Test
     */
    @Test
    fun correctTest() {
        // function call
        val stacks = rootService.gameService.createStacks()
        // tests
        for (id in 1..72) {
            assertEquals(id, stacks[id - 1].id)
        }
    }

    /**
     * test with wrong fileName
     */
    @Test
    fun wrongFileNameTest() {
        // illegal function call
        assertFailsWith<FileNotFoundException> { rootService.gameService.createStacks("") }
    }
}
