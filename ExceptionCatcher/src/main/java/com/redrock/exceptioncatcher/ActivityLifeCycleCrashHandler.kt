package com.redrock.exceptioncatcher

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.redrock.exceptioncatcher.ActivityKiller.*
import me.weishu.reflection.Reflection

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 22:30.
 *
 */
internal object ActivityLifeCycleCrashHandler {
    private var mInstalled = false

    private lateinit var mActivityKiller: IActivityKiller

    private var mCrashListener: CrashListener? = null

    private var mIsInFakeLoop = false

    //mH中消息类型定义的一些常量
    private const val LAUNCH_ACTIVITY = 100
    private const val PAUSE_ACTIVITY = 101
    private const val PAUSE_ACTIVITY_FINISHING = 102
    private const val STOP_ACTIVITY_HIDE = 104
    private const val RESUME_ACTIVITY = 107
    private const val DESTROY_ACTIVITY = 109
    private const val EXECUTE_TRANSACTION = 159

    fun install(ctx: Context, listener: CrashListener?) {
        if (mInstalled) {
            return
        }
        try {
            Reflection.unseal(ctx)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        mInstalled = true
        mCrashListener = listener
        initActivityKiller()
    }

    private fun initActivityKiller() {
        if (Build.VERSION.SDK_INT >= 28) {
            mActivityKiller = ActivityKillerV28()
        } else if (Build.VERSION.SDK_INT >= 26) {
            mActivityKiller = ActivityKillerV26()
        } else if (Build.VERSION.SDK_INT == 25 || Build.VERSION.SDK_INT == 24) {
            mActivityKiller = ActivityKillerV24_V25()
        } else if (Build.VERSION.SDK_INT <= 23) {
            mActivityKiller = ActivityKillerV21_V23()
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
                if (msg.what == EXECUTE_TRANSACTION) {
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        mActivityKiller.finishLaunchActivity(msg)
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
                        mActivityKiller.finishLaunchActivity(msg)
                        notifyException(throwable)
                    }
                    return@Callback true
                }
                RESUME_ACTIVITY -> {
                    //回到activity onRestart onStart onResume
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        mActivityKiller.finishResumeActivity(msg)
                        notifyException(throwable)
                    }
                    return@Callback true
                }
                PAUSE_ACTIVITY_FINISHING -> {
                    //按返回键 onPause
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        mActivityKiller.finishPauseActivity(msg)
                        notifyException(throwable)
                    }
                    return@Callback true
                }
                PAUSE_ACTIVITY -> {
                    //开启新页面时，旧页面执行 activity.onPause
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        mActivityKiller.finishPauseActivity(msg)
                        notifyException(throwable)
                    }
                    return@Callback true
                }
                STOP_ACTIVITY_HIDE -> {
                    //开启新页面时，旧页面执行 activity.onStop
                    try {
                        mhHandler.handleMessage(msg)
                    } catch (throwable: Throwable) {
                        mActivityKiller.finishStopActivity(msg)
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
        if (isInFakeLoop()) {
            mCrashListener?.onBandageExceptionCaught(throwable)
        } else {
            enterFakeLoop()
        }
    }


    fun onExceptionCaught(thread: Thread, ex: Throwable) {
        if (thread === Looper.getMainLooper().thread) {
            isChoreographerException(ex)
            if (!mIsInFakeLoop) {
                enterFakeLoop()
            }
        }
    }


    private fun enterFakeLoop() {
        mIsInFakeLoop = true
        mCrashListener?.onEnterFakeLoop()

        while (true) {
            try {
                Looper.loop()
            } catch (e: Throwable) {
                isChoreographerException(e)
                mCrashListener?.onBandageExceptionCaught(e)
            }
        }
    }

    fun isInFakeLoop(): Boolean {
        return mIsInFakeLoop
    }

    private fun isChoreographerException(e: Throwable?) {
        if (e == null) {
            return
        }
        val elements = e.stackTrace ?: return
        for (i in elements.size - 1 downTo 0) {
            if (elements.size - i > 20) {
                return
            }
            val element = elements[i]
            if ("android.view.Choreographer" == element.className && "Choreographer.java" == element.fileName && "doFrame" == element.methodName) {
                mCrashListener?.onChoreographerExceptionCaught(e)
                return
            }
        }
    }
}