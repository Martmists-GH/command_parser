package com.martmists.commandparser.dispatch

import com.martmists.commandparser.arguments.ArgumentType
import com.martmists.commandparser.ext.strip

class ArgumentNode<C : Context, T>(val name: String, val type: ArgumentType<C, T>) : Node<C>() {
    override suspend fun match(ctx: C, input: String): Pair<Boolean, String> {
        val parsed = type.parse(ctx, input)
        return if (parsed != null) {
            Pair(true, input.substring(parsed.length))
        } else {
            Pair(false, input)
        }
    }

    internal fun delegate(ctx: C): T = ctx.argument(name)
}
