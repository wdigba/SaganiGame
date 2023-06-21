package service

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
