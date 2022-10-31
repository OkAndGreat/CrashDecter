package com.redrock.crashdecter

import android.app.Application
import android.content.Context
import android.util.Log
import com.redrock.exceptioncatcher.CrashHandler
import com.redrock.exceptioncatcher.CrashListener
import com.redrock.exceptioncatcher.CrashStrategy.BackToMainActivityStrategy
import com.zxy.recovery.callback.RecoveryCallback


/**
 * Author by OkAndGreat
 * Date on 2022/10/27 21:48.
 *
 */
class myApp : Application() {
    var myCallback = object : RecoveryCallback {
        override fun stackTrace(stackTrace: String?) {
            Log.d(TAG, "stackTrace: $stackTrace")
        }

        override fun cause(cause: String?) {
            Log.d(TAG, "cause: $cause")
        }

        override fun exception(
            throwExceptionType: String?,
            throwClassName: String?,
            throwMethodName: String?,
            throwLineNumber: Int
        ) {
            Log.d(TAG, "exception: ")
        }

        override fun throwable(throwable: Throwable?) {
            Log.d(TAG, "throwable: ")
        }

    }

    companion object {
        const val TAG = "myApp"
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()

        CrashHandler.init(this, BackToMainActivityStrategy(), object : CrashListener {
            override fun onExceptionCaught(throwable: Throwable) {
                Log.d(TAG, "onExceptionCaught: ")
            }

            override fun onEnterFakeLoop() {
                Log.d(TAG, "onEnterFakeLoop: ")
            }

            override fun onChoreographerExceptionCaught(throwable: Throwable) {
                Log.d(TAG, "onChoreographerExceptionCaught: ")
            }

            override fun onBandageExceptionCaught(throwable: Throwable) {
                Log.d(TAG, "onBandageExceptionCaught: ")
            }

        })

    }
}