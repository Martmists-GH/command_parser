package com.martmists.commandparser.dsl

import com.martmists.commandparser.dispatch.Dispatcher
import com.martmists.commandparser.dispatch.Context
import com.martmists.commandparser.dispatch.LiteralNode

class BuildContext<C : Context>(private val dispatcher: Dispatcher<C>) {
    fun command(vararg names: String, block: BuildCommandContext<C>.() -> Unit) {
        val node = LiteralNode<C>(names)
        val command = BuildCommandContext<C>(node)
        command.block()
        dispatcher.addNode(node)
    }
}
