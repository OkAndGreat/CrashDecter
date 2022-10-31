package com.redrock.exceptioncatcher.CrashStrategy

import android.content.Context
import com.redrock.exceptioncatcher.ActivityCrashManager

/**
 * Author by OkAndGreat
 * Date on 2022/10/31 20:54.
 *
 */
class BackToMainActivityStrategy : ICrashStrategy {

    override fun onExceptionHappened(context: Context) {
        ActivityCrashManager.backToMainActivity()
    }

}
