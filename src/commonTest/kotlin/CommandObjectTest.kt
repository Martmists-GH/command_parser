import com.martmists.commandparser.arguments.FloatArgumentType
import com.martmists.commandparser.dispatch.Command
import com.martmists.commandparser.dispatch.Context
import com.martmists.commandparser.dispatch.Dispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue


class MathCommand : Command<Context>("math") {
    init {
        addSubcommand(DivideCommand())
    }

    override suspend fun action(ctx: Context) {
        println("No math action chosen!")
        throw IllegalStateException()
    }
}


class DivideCommand : Command<Context>("div") {
    private val numerator by argument(FloatArgumentType.float())
    private val denominator by argument(FloatArgumentType.float())

    override suspend fun check(ctx: Context): Boolean {
        return denominator != 0f
    }

    override suspend fun action(ctx: Context) {
        println("$numerator / $denominator = ${numerator / denominator}")
    }
}


class CommandObjectTest {
    private val dispatcher = Dispatcher<Context>()
    init {
        dispatcher.addNode(MathCommand())
    }

    @Test
    fun test() = runTest {
        val ctx = Context("math div 1 2")
        assertTrue(dispatcher.dispatch(ctx))
    }
}
