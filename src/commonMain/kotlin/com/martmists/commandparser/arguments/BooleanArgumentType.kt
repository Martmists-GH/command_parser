package com.martmists.commandparser.arguments

import com.martmists.commandparser.dispatch.Context

class BooleanArgumentType<C: Context> private constructor() : ArgumentType<C, Boolean>() {
    override suspend fun parse(context: C, input: String): String? {
        val lower = input.lowercase()
        if (lower.startsWith("true")) {
            return input.substring(0, 4)
        } else if (lower.startsWith("false")) {
            return input.substring(0, 5)
        }
        return null
    }

    override suspend fun value(context: C, value: String): Boolean {
        return value.lowercase() == "true"
    }

    companion object {
        fun <C: Context> bool() = BooleanArgumentType<C>()
    }
}
