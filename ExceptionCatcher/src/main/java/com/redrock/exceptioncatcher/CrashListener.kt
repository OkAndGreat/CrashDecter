package com.redrock.exceptioncatcher

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 22:20.
 *
 */
interface CrashListener {

    fun againStartApp()

    fun recordException(ex: Throwable?)
}