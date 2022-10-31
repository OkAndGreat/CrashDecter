package com.redrock.exceptioncatcher.CrashStrategy

import android.content.Context

/**
 * Author by OkAndGreat
 * Date on 2022/10/31 20:47.
 * 发生异常时的处理策略
 */
interface ICrashStrategy {

    fun onExceptionHappened(context: Context)

}