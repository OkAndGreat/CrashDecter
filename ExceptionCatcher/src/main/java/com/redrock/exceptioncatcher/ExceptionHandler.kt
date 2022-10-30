package com.redrock.exceptioncatcher

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 23:07.
 *
 */
abstract class ExceptionHandler {
    fun uncaughtExceptionHappened(thread: Thread?, throwable: Throwable?) {
        try {
            //捕获监听中异常，防止使用方代码抛出异常时导致的反复调用
            onUncaughtExceptionHappened(thread, throwable)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun bandageExceptionHappened(throwable: Throwable?) {
        try {
            //捕获监听中异常，防止使用方代码抛出异常时导致的反复调用
            onBandageExceptionHappened(throwable)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun enterSafeMode() {
        try {
            onEnterSafeMode()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    fun mayBeBlackScreen(e: Throwable?) {
        try {
            onMayBeBlackScreen(e)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    protected abstract fun onUncaughtExceptionHappened(thread: Thread?, throwable: Throwable?)

    protected abstract fun onBandageExceptionHappened(throwable: Throwable?)

    protected abstract fun onEnterSafeMode()

    protected open fun onMayBeBlackScreen(e: Throwable?) {}
}