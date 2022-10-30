package com.redrock.crashdecter

import android.app.Application
import android.content.Context
import android.util.Log
import com.redrock.exceptioncatcher.CrashHandler
import com.redrock.exceptioncatcher.CrashListener
import me.weishu.reflection.Reflection


/**
 * Author by OkAndGreat
 * Date on 2022/10/27 21:48.
 *
 */
class myApp : Application() {
    companion object {
        const val TAG = "myApp"
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Reflection.unseal(this)
    }

    override fun onCreate() {
        super.onCreate()

        CrashHandler.init(this, object : CrashListener {
            override fun againStartApp() {
                //ProcessPhoenix.triggerRebirth(this@myApp);
            }

            override fun recordException(ex: Throwable?) {
                Log.d(TAG, "recordException: ")
            }

        })
    }
}