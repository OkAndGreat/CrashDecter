package com.redrock.exceptioncatcher.ActivityKiller

import android.os.Message

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 22:34.
 *
 */
interface IActivityKiller {

    fun finishLaunchActivity(message: Message)

    fun finishResumeActivity(message: Message)

    fun finishPauseActivity(message: Message)

    fun finishStopActivity(message: Message)

}