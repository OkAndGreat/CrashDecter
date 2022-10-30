package com.redrock.exceptioncatcher

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 22:23.
 *
 */
internal inline fun <reified T : Any> noOpDelegate(): T = noOperationDelegate()

internal inline fun <reified T : Any> noOperationDelegate(): T {
    val javaClass = T::class.java
    return Proxy.newProxyInstance(
        javaClass.classLoader, arrayOf(javaClass), NO_OP_HANDLER
    ) as T
}

private val NO_OP_HANDLER = InvocationHandler { _, _, _ ->
    // no op
}