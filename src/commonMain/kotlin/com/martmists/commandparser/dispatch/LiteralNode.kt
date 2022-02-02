package com.martmists.commandparser.dispatch

class LiteralNode<C : Context>(names: Array<out String>) : Node<C>() {
    private val sortedNames = names.sortedBy { it.length }.reversed()

    fun getNames() = sortedNames

    override suspend fun match(ctx: C, input: String): Pair<Boolean, String> {
        for (name in sortedNames) {
            if (input.startsWith(name)) {
                return Pair(true, input.substring(name.length))
            }
        }

        return Pair(false, input)
    }
}
