@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@file:OptIn(ExperimentalCoroutinesApi::class)

import com.martmists.commandparser.arguments.IntegerArgumentType
import com.martmists.commandparser.arguments.StringArgumentType
import com.martmists.commandparser.dispatch.Context
import com.martmists.commandparser.dispatch.Dispatcher
import com.martmists.commandparser.dsl.build
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CommandTests {
    private val dispatcher = Dispatcher<Context>()

    init {
        build(dispatcher) {
            command("test") {
                action {
                    println("success")
                }

                argument("arg", IntegerArgumentType.int(radix=2)) { bin ->
                    action {
                        println(bin())
                    }
                }

                argument("arg", IntegerArgumentType.int(radix=16)) { hex ->
                    action {
                        println(hex())
                    }
                }
            }

            command("test2") {
                argument("num1", IntegerArgumentType.int()) { num1 ->
                    argument("num2", IntegerArgumentType.int()) { num2 ->
                        check {
                            num1() < num2()
                        }
                        action {
                            println(num1() + num2())
                        }
                    }
                }
            }

            command("strings") {
                argument("string", StringArgumentType.word()) { string ->
                    argument("string2", StringArgumentType.string()) { string2 ->
                        argument("string3", StringArgumentType.string()) { string3 ->
                            argument("string4", StringArgumentType.greedy()) { string4 ->
                                action {
                                    println(listOf(string(), string2(), string3(), string4()))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testNoArgs() = runTest {
        val ctx = Context("test")
        assertTrue(dispatcher.dispatch(ctx))
    }

    @Test
    fun testArg() = runTest {
        val ctx = Context("test 1011")
        assertTrue(dispatcher.dispatch(ctx))
        assertEquals(ctx.argument("arg"), 0b1011)
    }

    @Test
    fun testArgOverload() = runTest {
        val ctx = Context("test 1f")
        assertTrue(dispatcher.dispatch(ctx))
        assertEquals(ctx.argument("arg"), 0x1f)
    }

    @Test
    fun testFailCheck() = runTest {
        val ctx = Context("test2 10 5")
        assertFalse(dispatcher.dispatch(ctx))
    }

    @Test
    fun testPassCheck() = runTest {
        val ctx = Context("test2 5 10")
        assertTrue(dispatcher.dispatch(ctx))
        assertEquals(ctx.argument("num1"), 5)
        assertEquals(ctx.argument("num2"), 10)
    }

    @Test
    fun testStrings() = runTest {
        val ctx = Context("strings word \"quoted string\" unquoted_string greedy remaining contents")
        assertTrue(dispatcher.dispatch(ctx))
        assertEquals(ctx.argument("string"), "word")
        assertEquals(ctx.argument("string2"), "quoted string")
        assertEquals(ctx.argument("string3"), "unquoted_string")
        assertEquals(ctx.argument("string4"), "greedy remaining contents")
    }
}
