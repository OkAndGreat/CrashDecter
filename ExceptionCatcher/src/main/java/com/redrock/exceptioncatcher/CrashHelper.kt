package com.redrock.exceptioncatcher

import android.content.Context
import android.os.Build
import android.os.Looper
import com.redrock.exceptioncatcher.ActivityKiller.ActivityKillerV21_V23
import com.redrock.exceptioncatcher.ActivityKiller.ActivityKillerV24_V25
import com.redrock.exceptioncatcher.ActivityKiller.ActivityKillerV26
import com.redrock.exceptioncatcher.ActivityKiller.ActivityKillerV28
import me.weishu.reflection.Reflection
import kotlin.properties.Delegates

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 22:30.
 *
 */
object CrashHelper {
    private var sInstalled = false

    private lateinit var sActivityKiller: IActivityKiller

    private var sIsSafeMode by Delegates.notNull<Boolean>()

    private var sExceptionHandler: ExceptionHandler? = null

    fun install(ctx: Context) {
        if (sInstalled) {
            return
        }
        try {
            Reflection.unseal(ctx)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        sInstalled = true
        initActivityKiller()
    }

    private fun initActivityKiller() {
        if (Build.VERSION.SDK_INT >= 28) {
            sActivityKiller = ActivityKillerV28()
        } else if (Build.VERSION.SDK_INT >= 26) {
            sActivityKiller = ActivityKillerV26()
        } else if (Build.VERSION.SDK_INT == 25 || Build.VERSION.SDK_INT == 24) {
            sActivityKiller = ActivityKillerV24_V25()
        } else if (Build.VERSION.SDK_INT <= 23) {
            sActivityKiller = ActivityKillerV21_V23()
        }
        try {
            hookmH()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    private fun hookmH() {

    }

    fun setExceptionHandler(handler: ExceptionHandler) {
        sExceptionHandler = handler
    }

    fun setSafe(thread: Thread, ex: Throwable) {
        if (thread === Looper.getMainLooper().thread) {
            isChoreographerException(ex)
            safeMode()
        }
    }

    private fun isChoreographerException(e: Throwable?) {
        if (e == null || sExceptionHandler == null) {
            return
        }
        val elements = e.stackTrace ?: return
        for (i in elements.size - 1 downTo 0) {
            if (elements.size - i > 20) {
                return
            }
            val element = elements[i]
            if ("android.view.Choreographer" == element.className && "Choreographer.java" == element.fileName && "doFrame" == element.methodName) {
                sExceptionHandler!!.mayBeBlackScreen(e)
                return
            }
        }
    }

    fun safeMode() {
        sIsSafeMode = true
        sExceptionHandler?.enterSafeMode()

        while (true) {
            try {
                Looper.loop()
            } catch (e: Throwable) {
                isChoreographerException(e)
                sExceptionHandler?.bandageExceptionHappened(e)
            }
        }
    }
}