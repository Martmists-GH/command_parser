package com.martmists.commandparser.dispatch

abstract class Node<C : Context> {
    private val children: MutableList<Node<C>> = mutableListOf()
    private var action: (suspend C.() -> Unit)? = null
    private var check: suspend C.() -> Boolean = {
        true
    }

    internal abstract suspend fun match(ctx: C, input: String): Pair<Boolean, String>

    internal fun addChild(node: Node<C>) {
        children.add(node)
    }

    fun children(): List<Node<C>> {
        return children
    }

    fun hasAction(): Boolean {
        return action != null
    }

    suspend fun doCheck(ctx: C): Boolean {
        return check(ctx)
    }

    internal fun setAction(block: suspend C.() -> Unit) {
        action = block
    }

    internal fun setCheck(block: suspend C.() -> Boolean) {
        check = block
    }

    internal open suspend fun tryDispatch(context: C, input: String): Boolean {
        val (matches, remaining) = match(context, input)
        if (matches) {
            if (this is ArgumentNode<C, *>) {
                if (remaining == input) {
                    context.addParameter(this.name, null)
                } else {
                    context.addParameter(this.name, this.type.value(context, input.removeSuffix(remaining).trim()))
                }
            }

            if (check(context)) {
                if ((remaining.isEmpty() || remaining.isBlank()) && action != null) {
                    action!!(context)
                    return true
                } else {
                    for (c in children) {
                        if (c.tryDispatch(context, remaining.trim())) {
                            return true
                        }
                    }
                }
            }

            if (this is ArgumentNode<*, *>) {
                context.removeParameter(this.name)
            }
        }
        return false
    }
}
