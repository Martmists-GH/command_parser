@file:OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")

import com.martmists.commandparser.arguments.ArgumentType
import com.martmists.commandparser.arguments.FloatArgumentType
import com.martmists.commandparser.arguments.IntegerArgumentType
import com.martmists.commandparser.dispatch.Context
import com.martmists.commandparser.dispatch.Dispatcher
import com.martmists.commandparser.dsl.build
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MyContext(input: String) : Context(input)

data class Coordinate(val x: Int, val y: Int)

class CoordinateArgumentType<C : Context> : ArgumentType<C, Coordinate>() {
    private val regex = """\((\d+),\s*(\d+)\)""".toRegex()

    override suspend fun parse(context: C, input: String): String? {
        val match = regex.matchAt(input, 0) ?: return null
        return match.value
    }

    override suspend fun value(context: C, value: String): Coordinate {
        val groups = regex.matchEntire(value)!!.groupValues
        return Coordinate(groups[1].toInt(), groups[2].toInt())
    }
}


class ReadmeTest {
    private val dispatcher = Dispatcher<MyContext>()

    init {
        build(dispatcher) {
            command("hello") {
                action {
                    println("Hello world!")
                }
            }
        }

        build(dispatcher) {
            command("add") {
                argument("num1", IntegerArgumentType.int()) { num1 ->
                    argument("num2", IntegerArgumentType.int()) { num2 ->
                        action {
                            println("${num1()} + ${num2()} = ${num1() + num2()}")
                        }
                    }
                }

                argument("float1", FloatArgumentType.float()) { float1 ->
                    argument("float2", FloatArgumentType.float()) { float2 ->
                        action {
                            println("${float1()} + ${float2()} = ${float1() + float2()}")
                        }
                    }
                }
            }
        }

        build(dispatcher) {
            command("divide") {
                argument("num1", IntegerArgumentType.int()) { num1 ->
                    argument("num2", IntegerArgumentType.int()) { num2 ->
                        check { num2() != 0 }

                        action {
                            println("${num1()} / ${num2()} = ${num1().toFloat() / num2()}")
                        }

                        argument("num3", IntegerArgumentType.int()) { num3 ->
                            check { num3() > 0 }

                            action {
                                println("${num1()} / (${num2()}^${num3()}) = ${num1().toFloat() / num2().toFloat().pow(num3())}")
                            }
                        }
                    }
                }
            }
        }

        build(dispatcher) {
            command("git") {
                literal("add") {
                    action {
                        println("Adding files...")
                    }
                }

                literal("commit") {
                    action {
                        println("Committing...")
                    }
                }

                literal("push") {
                    action {
                        println("Pushing...")
                    }
                }
            }
        }

        build(dispatcher) {
            command("info", "i") {
                literal("register", "reg", "r") {
                    action {
                        println("Dumping register info...")
                    }
                }
            }
        }

        build(dispatcher) {
            command("move") {
                argument("coord", CoordinateArgumentType()) { coord ->
                    action {
                        println("Moving to ${coord().x}, ${coord().y}")
                    }
                }
            }
        }
    }


    @Test
    fun testHello() = runTest {
        assertTrue(dispatcher.dispatch(MyContext("hello")))
        assertFalse(dispatcher.dispatch(MyContext("hello world")))
    }

    @Test
    fun testAdd() = runTest {
        assertTrue(dispatcher.dispatch(MyContext("add 1 2")))
        assertTrue(dispatcher.dispatch(MyContext("add 1.0 2.0")))
    }

    @Test
    fun testDivide() = runTest {
        assertFalse(dispatcher.dispatch(MyContext("divide 1 0")))
        assertTrue(dispatcher.dispatch(MyContext("divide 1 2")))
        assertTrue(dispatcher.dispatch(MyContext("divide 1 2 3")))
        assertFalse(dispatcher.dispatch(MyContext("divide 1 2 0")))
        assertFalse(dispatcher.dispatch(MyContext("divide 1 0 3")))
    }

    @Test
    fun testGit() = runTest {
        assertTrue(dispatcher.dispatch(MyContext("git add")))
        assertTrue(dispatcher.dispatch(MyContext("git commit")))
        assertTrue(dispatcher.dispatch(MyContext("git push")))
    }

    @Test
    fun testInfo() = runTest {
        assertTrue(dispatcher.dispatch(MyContext("info register")))
        assertTrue(dispatcher.dispatch(MyContext("info r")))
        assertTrue(dispatcher.dispatch(MyContext("i reg")))
    }

    @Test
    fun testMove() = runTest {
        assertTrue(dispatcher.dispatch(MyContext("move (1, 2)")))
        assertFalse(dispatcher.dispatch(MyContext("move (1.0, 2.0)")))
    }
}