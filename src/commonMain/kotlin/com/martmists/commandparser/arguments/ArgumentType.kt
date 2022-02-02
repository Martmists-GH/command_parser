package com.martmists.commandparser.arguments

import com.martmists.commandparser.dispatch.Context

abstract class ArgumentType<C : Context, T> {
    abstract suspend fun parse(context: C, input: String): String?

    abstract suspend fun value(context: C, value: String): T
}
