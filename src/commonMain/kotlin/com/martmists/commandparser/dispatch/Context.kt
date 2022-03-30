package com.martmists.commandparser.dispatch

open class Context(val input: String) {
    private val arguments = mutableMapOf<String, Any?>()

    internal fun <T> argument(name: String): T {
        return arguments[name] as T
    }

    internal fun <T> optionalArgument(name: String): T? {
        return arguments[name] as T?
    }

    internal fun addParameter(name: String, value: Any?) {
        arguments[name] = value
    }

    internal fun removeParameter(name: String) {
        arguments.remove(name)
    }

    open fun hasPermission(node: String): Boolean {
        return true
    }
}
