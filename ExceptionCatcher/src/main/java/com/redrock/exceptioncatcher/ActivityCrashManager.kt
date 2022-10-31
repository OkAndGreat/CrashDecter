package com.redrock.exceptioncatcher

import android.app.Activity
import java.util.*

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 22:25.
 *
 */
object ActivityCrashManager {

    private val activityStack: Stack<Activity> by lazy {
        Stack<Activity>()
    }

    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activityStack.remove(activity)
    }

    fun killTopActivity() {
        val a = activityStack.pop()
        a.finish()
    }

    fun backToMainActivity() {
        while (activityStack.size > 1) {
            val a = activityStack.pop()
            a.finish()
        }
    }
}