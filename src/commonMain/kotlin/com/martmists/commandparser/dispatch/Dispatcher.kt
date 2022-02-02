package com.martmists.commandparser.dispatch

import com.martmists.commandparser.ext.strip

class Dispatcher<C : Context> {
    private val commands = mutableListOf<LiteralNode<C>>()

    fun addNode(node: LiteralNode<C>) {
        commands.add(node)
    }

    suspend fun dispatch(context: C): Boolean {
        for (command in commands) {
            if (command.tryDispatch(context, context.input.strip())) {
                return true
            }
        }
        return false
    }
}
