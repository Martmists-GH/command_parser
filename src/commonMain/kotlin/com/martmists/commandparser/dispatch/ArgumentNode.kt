package com.martmists.commandparser.dispatch

import com.martmists.commandparser.arguments.ArgumentType

class ArgumentNode<C : Context, T> internal constructor(
    val name: String,
    val type: ArgumentType<C, T>,
    private val optional: Boolean,
    private val default: T?
) : Node<C>() {
    constructor(name: String, type: ArgumentType<C, T>) : this(name, type, false, null)

    override suspend fun match(ctx: C, input: String): Pair<Boolean, String> {
        val parsed = type.parse(ctx, input)
        return if (parsed != null) {
            Pair(true, input.substring(parsed.length))
        } else {
            if (optional) {
                Pair(true, input)
            } else {
                Pair(false, input)
            }
        }
    }

    internal fun delegate(ctx: C): T = ctx.argument(name)

    internal fun delegateDefault(ctx: C): T = ctx.optionalArgument<T>(name) ?: default!!

    internal fun delegateOptional(ctx: C): T? = ctx.optionalArgument<T>(name)
}
