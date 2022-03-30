package com.martmists.commandparser.dispatch

import com.martmists.commandparser.permissions.PermissionNode

abstract class ContextWithPermissions(input: String) : Context(input) {
    abstract fun getPermissions(): List<PermissionNode>

    override fun hasPermission(node: String): Boolean {
        return getPermissions().any { it.match(node) }
    }
}
