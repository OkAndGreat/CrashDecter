package com.redrock.exceptioncatcher.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process

/**
 * Author by OkAndGreat
 * Date on 2022/10/30 18:03.
 *  重启app进程
 */
class AppPhoenix : Activity() {

    companion object {
        private const val KEY_RESTART_INTENTS = "restart_intents"

        //启动Phoenix进程的app进程的pid
        private const val KEY_MAIN_PROCESS_PID = "main_process_pid"

        fun triggerRebirth(context: Context) {
            val packageName = context.packageName
            val defaultIntent = context.packageManager.getLaunchIntentForPackage(packageName)
                ?: throw IllegalStateException(
                    "找不到包名为"
                            + packageName + "的默认Activity"
                            + "。请检查该包下是否有一个Activity的intent filter中的category被指定为DEFAULT"
                )
            defaultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            val intent = Intent(context, AppPhoenix::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putParcelableArrayListExtra(
                KEY_RESTART_INTENTS, ArrayList(
                    listOf(defaultIntent)
                )
            )
            intent.putExtra(KEY_MAIN_PROCESS_PID, Process.myPid())
            context.startActivity(intent)
        }

        /**
         * 检查当前的app进程是不是重启过的进程
         */
        fun isPhoenixProcess(context: Context): Boolean {
            val currentPid = Process.myPid()
            val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val runningProcesses = manager.runningAppProcesses
            if (runningProcesses != null) {
                for (processInfo in runningProcesses) {
                    if (processInfo.pid == currentPid && processInfo.processName.endsWith(":phoenix")) {
                        return true
                    }
                }
            }
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //杀死原来的进程
        Process.killProcess(intent.getIntExtra(KEY_MAIN_PROCESS_PID, -1))

        val intents = intent.getParcelableArrayListExtra<Intent>(KEY_RESTART_INTENTS)
        startActivities(intents!!.toTypedArray())
        finish()

        //暂时不清楚这一句的用处
        Runtime.getRuntime().exit(0)

    }
}