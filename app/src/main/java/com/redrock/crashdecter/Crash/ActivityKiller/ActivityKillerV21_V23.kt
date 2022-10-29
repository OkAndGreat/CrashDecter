package com.redrock.crashdecter.Crash.ActivityKiller

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.IBinder
import android.os.Message
import com.redrock.crashdecter.Crash.IActivityKiller

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 22:39.
 *
 */
class ActivityKillerV21_V23 : IActivityKiller {
    override fun finishLaunchActivity(message: Message) {
        try {
            val activityClientRecord = message.obj
            val tokenField = activityClientRecord.javaClass.getDeclaredField("token")
            tokenField.isAccessible = true
            val binder = tokenField[activityClientRecord] as IBinder
            finish(binder)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun finishResumeActivity(message: Message) {
        try {
            finish((message.obj as IBinder))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun finishPauseActivity(message: Message) {
        try {
            finish((message.obj as IBinder))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun finishStopActivity(message: Message) {
        try {
            finish((message.obj as IBinder))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    @Throws(Exception::class)
    private fun finish(binder: IBinder) {
        val activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative")
        val getDefaultMethod = activityManagerNativeClass.getDeclaredMethod("getDefault")
        val activityManager = getDefaultMethod.invoke(null)
        val finishActivityMethod = activityManager.javaClass.getDeclaredMethod(
            "finishActivity",
            IBinder::class.java,
            Int::class.javaPrimitiveType,
            Intent::class.java,
            Boolean::class.javaPrimitiveType
        )
        finishActivityMethod.invoke(
            activityManager,
            binder, Activity.RESULT_CANCELED, null, false
        )
    }
}