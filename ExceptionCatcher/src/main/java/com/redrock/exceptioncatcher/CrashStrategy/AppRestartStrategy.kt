package com.redrock.exceptioncatcher.CrashStrategy

import android.content.Context
import com.redrock.exceptioncatcher.util.AppPhoenix

/**
 * Author by OkAndGreat
 * Date on 2022/10/31 20:48.
 *
 */
class AppRestartStrategy : ICrashStrategy {

    override fun onExceptionHappened(context: Context) {
        AppPhoenix.triggerRebirth(context)
    }
}