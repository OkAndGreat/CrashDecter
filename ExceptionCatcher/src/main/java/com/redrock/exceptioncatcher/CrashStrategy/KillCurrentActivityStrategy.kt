package com.redrock.exceptioncatcher.CrashStrategy

import android.content.Context
import com.redrock.exceptioncatcher.ActivityCrashManager

/**
 * Author by OkAndGreat
 * Date on 2022/10/31 20:53.
 * 杀死抛出异常的Activity
 */
class KillCurrentActivityStrategy : ICrashStrategy {

    override fun onExceptionHappened(context: Context) {
        ActivityCrashManager.killTopActivity()
    }

}