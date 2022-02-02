package com.martmists.commandparser.arguments

import com.martmists.commandparser.dispatch.Context
import com.martmists.commandparser.ext.strip

class StringArgumentType<C: Context> private constructor(private val type: StringType) : ArgumentType<C, String>() {
    private enum class StringType {
        WORD,
        STRING,
        GREEDY,
    }

    private fun readString(input: String): String? {
        var inQuote = false
        var end = 0

        for (c in input) {
            end++
            if (c == '"') {
                if (inQuote) {
                    inQuote = false
                    break
                } else {
                    inQuote = true
                }
            } else if (c == ' ' && !inQuote) {
                end--
                break
            }
        }

        if (inQuote) {
            return null
        }

        return input.substring(0, end)
    }

    override suspend fun parse(context: C, input: String): String? {
        if (input.isBlank()) {
            return null
        }

        return when (type) {
            StringType.WORD -> input.split(Regex("\\s+"))[0]
            StringType.STRING -> readString(input)
            StringType.GREEDY -> input
        }
    }

    override suspend fun value(context: C, value: String): String {
        return value.removeSurrounding("\"")
    }

    companion object {
        fun <C: Context> word() = StringArgumentType<C>(StringType.WORD)
        fun <C: Context> string() = StringArgumentType<C>(StringType.STRING)
        fun <C: Context> greedy() = StringArgumentType<C>(StringType.GREEDY)
    }
}
