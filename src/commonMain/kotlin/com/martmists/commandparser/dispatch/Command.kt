package com.martmists.commandparser.dispatch

import com.martmists.commandparser.arguments.ArgumentType
import kotlin.reflect.KProperty

abstract class Command<C: Context>(names: Array<out String>) : LiteralNode<C>(names) {
    constructor(name: String) : this(arrayOf(name))

    private var tail: Node<C>
    lateinit var context: C

    init {
        tail = this
        setCheck(::check)
        setAction(::action)
    }

    protected inner class Delegate<T>(private val name: String) {
        operator fun getValue(thisRef: Command<C>, property: KProperty<*>): T {
            return context.argument(name) as T
        }
    }

    protected inner class DelegateProvider<T>(private val type: ArgumentType<C, T>) {
        operator fun provideDelegate(thisRef: Command<C>, prop: KProperty<*>): Delegate<T> {
            ArgumentNode(prop.name, type).also {
                tail.addChild(it)
                tail.setCheck { true }
                tail = it
                it.setAction(thisRef::action)
                it.setCheck(thisRef::check)
            }
            return Delegate(prop.name)
        }
    }

    protected fun <T> argument(type: ArgumentType<C, T>) = DelegateProvider(type)

    protected fun addSubcommand(subcommand: Command<C>) {
        addChild(subcommand)
    }

    open suspend fun check(ctx: C): Boolean = true

    abstract suspend fun action(ctx: C)

    override suspend fun tryDispatch(context: C, input: String): Boolean {
        this.context = context
        return super.tryDispatch(context, input)
    }
}
