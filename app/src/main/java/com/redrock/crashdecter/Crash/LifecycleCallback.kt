package com.redrock.crashdecter.Crash

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 22:21.
 *
 */
object LifecycleCallback : Application.ActivityLifecycleCallbacks by noOpDelegate() {
    public fun init(application: Application){
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ToolAppManager.addActivity(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        ToolAppManager.removeActivity(activity)
    }
}