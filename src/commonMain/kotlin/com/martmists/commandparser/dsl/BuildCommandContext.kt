package com.martmists.commandparser.dsl

import com.martmists.commandparser.arguments.ArgumentType
import com.martmists.commandparser.dispatch.ArgumentNode
import com.martmists.commandparser.dispatch.Context
import com.martmists.commandparser.dispatch.LiteralNode
import com.martmists.commandparser.dispatch.Node

class BuildCommandContext<C : Context>(private val parent: Node<C>) {
    fun literal(vararg names: String, block: BuildCommandContext<C>.() -> Unit) {
        val node = LiteralNode<C>(names)
        val command = BuildCommandContext(node)
        command.block()
        parent.addChild(node)
    }

    fun <T : ArgumentType<C, R>, R> argument(
        name: String,
        type: T,
        block: BuildCommandContext<C>.(suspend C.() -> R) -> Unit
    ) {
        val node = ArgumentNode(name, type)
        val command = BuildCommandContext(node)
        command.block(node::delegate)
        parent.addChild(node)
    }

    fun check(block: suspend C.() -> Boolean) {
        parent.setCheck(block)
    }

    fun action(block: suspend C.() -> Unit) {
        parent.setAction(block)
    }
}
