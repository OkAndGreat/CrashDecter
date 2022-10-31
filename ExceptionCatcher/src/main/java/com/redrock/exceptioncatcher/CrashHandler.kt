package com.redrock.exceptioncatcher

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.redrock.exceptioncatcher.CrashStrategy.ICrashStrategy

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 22:19.
 *
 */
@SuppressLint("StaticFieldLeak")
object CrashHandler : Thread.UncaughtExceptionHandler {

    private lateinit var mContext: Context

    private var crashListener: CrashListener? = null

    private lateinit var crashStrategy: ICrashStrategy

    fun init(ctx: Application, strategy: ICrashStrategy, listener: CrashListener?) {
        LifecycleCallback.init(ctx)
        ActivityLifeCycleCrashHandler.install(ctx, listener)
        mContext = ctx
        crashListener = listener
        crashStrategy = strategy
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        crashListener?.onExceptionCaught(e)
        crashStrategy.onExceptionHappened(mContext)
        ActivityLifeCycleCrashHandler.onExceptionCaught(t, e)
    }

}