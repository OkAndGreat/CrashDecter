package com.redrock.exceptioncatcher.CrashStrategy

import android.content.Context
import com.redrock.exceptioncatcher.CrashStrategy.ICrashStrategy

/**
 * Author by OkAndGreat
 * Date on 2022/10/31 20:52.
 *
 */
class DoNothingStrategy :ICrashStrategy{

    override fun onExceptionHappened(context: Context) {
        //do nothing
    }

}