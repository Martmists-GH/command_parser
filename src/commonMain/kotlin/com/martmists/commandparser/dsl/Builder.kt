package com.martmists.commandparser.dsl

import com.martmists.commandparser.dispatch.Dispatcher
import com.martmists.commandparser.dispatch.Context

fun <C : Context> build(dispatcher: Dispatcher<C>, block: BuildContext<C>.() -> Unit) {
    val context = BuildContext(dispatcher)
    context.block()
}
