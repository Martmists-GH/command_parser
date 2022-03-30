package com.martmists.commandparser.permissions

import kotlin.jvm.JvmStatic

class PermissionNode(private val name: String, private val supportsWildcards: Boolean = true) {
    private val children: MutableList<PermissionNode> = mutableListOf()

    fun match(node: String): Boolean {
        if (supportsWildcards && name == "*") {
            return true
        }
        val parts = node.split(".").toMutableList()
        val key = parts.removeFirst()
        if (name == key) {
            if (parts.isEmpty()) {
                return true
            }
            return children.any {
                it.match(parts.joinToString("."))
            }
        }
        return false
    }

    fun addChildrenFromNode(node: String) {
        if (node.isNotEmpty() && name != "*") {
            val split = node.split(".").toMutableList()
            val name = split.removeFirst()
            val remaining = split.joinToString(".")
            val child = children.firstOrNull { it.name == name } ?: let {
                val n = PermissionNode(name)
                children.add(n)
                n
            }
            child.addChildrenFromNode(remaining)
        }
    }

    companion object {
        @JvmStatic
        fun fromList(vararg nodes: String): List<PermissionNode> {
            val mapping = mutableMapOf<String, PermissionNode>()
            nodes.forEach {
                val split = it.split(".").toMutableList()
                val name = split.removeFirst()
                val remaining = split.joinToString(".")
                val child = mapping.getOrPut(name) { PermissionNode(name) }
                child.addChildrenFromNode(remaining)
            }

            return mapping.values.toList()
        }
    }
}
