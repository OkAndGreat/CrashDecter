package com.redrock.exceptioncatcher

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
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

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    @Throws(Exception::class)
    private fun hookmH() {
        val LAUNCH_ACTIVITY = 100
        val PAUSE_ACTIVITY = 101
        val PAUSE_ACTIVITY_FINISHING = 102
        val STOP_ACTIVITY_HIDE = 104
        val RESUME_ACTIVITY = 107
        val DESTROY_ACTIVITY = 109
        val NEW_INTENT = 112
        val RELAUNCH_ACTIVITY = 126
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val activityThread =
            activityThreadClass.getDeclaredMethod("currentActivityThread").invoke(null)
        val mhField = activityThreadClass.getDeclaredField("mH")
        mhField.isAccessible = true
        val mhHandler = mhField[activityThread] as Handler
        val callbackField = Handler::class.java.getDeclaredField("mCallback")
        callbackField.isAccessible = true
        callbackField[mhHandler] = Handler.Callback { msg ->
            if (Build.VERSION.SDK_INT >= 28) {
                //android P 生命周期全部走这
                val EXECUTE_TRANSACTION = 159
                if (msg.what == EXECUTE_TRANSACTION) {
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        sActivityKiller.finishLaunchActivity(msg)
                        notifyException(throwable)
                    }
                    return@Callback true
                }
                return@Callback false
            }
            when (msg.what) {
                LAUNCH_ACTIVITY -> {
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        sActivityKiller.finishLaunchActivity(msg)
                        notifyException(throwable)
                    }
                    return@Callback true
                }
                RESUME_ACTIVITY -> {
                    //回到activity onRestart onStart onResume
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        sActivityKiller.finishResumeActivity(msg)
                        notifyException(throwable)
                    }
                    return@Callback true
                }
                PAUSE_ACTIVITY_FINISHING -> {
                    //按返回键 onPause
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        sActivityKiller.finishPauseActivity(msg)
                        notifyException(throwable)
                    }
                    return@Callback true
                }
                PAUSE_ACTIVITY -> {
                    //开启新页面时，旧页面执行 activity.onPause
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        sActivityKiller.finishPauseActivity(msg)
                        notifyException(throwable)
                    }
                    return@Callback true
                }
                STOP_ACTIVITY_HIDE -> {
                    //开启新页面时，旧页面执行 activity.onStop
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        sActivityKiller.finishStopActivity(msg)
                        notifyException(throwable)
                    }
                    return@Callback true
                }
                DESTROY_ACTIVITY -> {
                    // 关闭activity onStop  onDestroy
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        notifyException(throwable)
                    }
                    return@Callback true
                }
            }
            false
        }
    }

    private fun notifyException(throwable: Throwable) {
        if (sExceptionHandler == null) {
            return
        }
        if (isSafeMode()) {
            sExceptionHandler!!.bandageExceptionHappened(throwable)
        } else {
            sExceptionHandler!!.uncaughtExceptionHappened(Looper.getMainLooper().thread, throwable)
            safeMode()
        }
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

    fun isSafeMode(): Boolean {
        return sIsSafeMode
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
}