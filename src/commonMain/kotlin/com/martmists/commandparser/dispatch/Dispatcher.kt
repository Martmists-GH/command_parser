package com.martmists.commandparser.dispatch

class Dispatcher<C : Context> {
    private val commands = mutableListOf<LiteralNode<C>>()

    internal fun addNode(node: LiteralNode<C>) {
        commands.add(node)
    }

    fun getCommands(): List<LiteralNode<C>> = commands.toList()

    suspend fun dispatch(context: C): Boolean {
        for (command in commands) {
            if (command.tryDispatch(context, context.input.trim())) {
                return true
            }
        }
        return false
    }
}
