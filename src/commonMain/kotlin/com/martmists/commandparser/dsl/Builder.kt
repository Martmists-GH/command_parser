package com.martmists.commandparser.dsl

import com.martmists.commandparser.dispatch.Context
import com.martmists.commandparser.dispatch.Dispatcher

fun <C : Context> build(dispatcher: Dispatcher<C>, block: BuildContext<C>.() -> Unit) {
    val context = BuildContext(dispatcher)
    context.block()
}
