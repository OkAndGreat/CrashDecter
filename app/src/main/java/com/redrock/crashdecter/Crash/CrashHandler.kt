package com.redrock.crashdecter.Crash

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 22:19.
 *
 */
@SuppressLint("StaticFieldLeak")
object CrashHandler : Thread.UncaughtExceptionHandler {
    const val TAG = "CrashHandler"

    private lateinit var mContext: Context

    private var listener: CrashListener? = null

    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

    fun init(ctx: Application, listener: CrashListener?) {
        LifecycleCallback.init(ctx)
        CrashHelper.install(ctx)
        mContext = ctx
        this.listener = listener
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        var isHandle = handleException(e)
        listener?.recordException(e)

        if (mDefaultHandler != null && !isHandle) {
            mDefaultHandler?.uncaughtException(t, e)
        } else {
            if (mContext is Application) {
                listener?.againStartApp()
            }
        }

        CrashHelper.setSafe(t, e)
    }

    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        //收集crash信息
        ex.localizedMessage ?: return false
        ex.printStackTrace()
        return true
    }

}