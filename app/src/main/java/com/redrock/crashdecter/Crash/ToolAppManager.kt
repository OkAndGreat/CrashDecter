package com.redrock.crashdecter.Crash

import android.app.Activity
import java.util.*

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 22:25.
 *
 */
object ToolAppManager {

    private val activityStack: Stack<Activity> by lazy {
        Stack<Activity>()
    }

    fun addActivity(activity: Activity){
        activityStack.add(activity)
    }

    fun removeActivity(activity: Activity){
        activityStack.remove(activity)
    }
}