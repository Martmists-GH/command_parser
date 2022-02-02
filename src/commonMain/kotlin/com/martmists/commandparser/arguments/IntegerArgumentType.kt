package com.martmists.commandparser.arguments

import com.martmists.commandparser.dispatch.Context
import kotlin.jvm.JvmOverloads

class IntegerArgumentType<C : Context> private constructor(private val radix: Int, private val range: IntRange) : ArgumentType<C, Int>() {
    override suspend fun parse(context: C, input: String): String? {
        val first = input.split(Regex("\\s+")).first()
        val num = first.toIntOrNull(radix)
        return if (num != null && num in range) first else null
    }

    override suspend fun value(context: C, value: String): Int {
        return value.toInt(radix)
    }

    companion object {
        @JvmOverloads
        fun <C : Context> int(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE, radix: Int = 10) = IntegerArgumentType<C>(radix, min .. max)

        @JvmOverloads
        fun <C : Context> int(range: IntRange, radix: Int = 10) = IntegerArgumentType<C>(radix, range)
    }
}
