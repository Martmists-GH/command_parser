package com.martmists.commandparser.arguments

import com.martmists.commandparser.dispatch.Context

class FloatArgumentType<C : Context> private constructor(private val range: ClosedFloatingPointRange<Float>) :
    ArgumentType<C, Float>() {
    override suspend fun parse(context: C, input: String): String? {
        val first = input.split(Regex("\\s+")).first()
        val num = first.toFloatOrNull()
        return if (num != null && num in range) first else null
    }

    override suspend fun value(context: C, value: String): Float {
        return value.toFloat()
    }

    companion object {
        fun <C : Context> float(min: Float = Float.NEGATIVE_INFINITY, max: Float = Float.POSITIVE_INFINITY) =
            FloatArgumentType<C>(min..max)

        fun <C : Context> float(range: ClosedFloatingPointRange<Float>) = FloatArgumentType<C>(range)
    }
}
