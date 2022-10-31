package com.redrock.exceptioncatcher

/**
 * Author by OkAndGreat
 * Date on 2022/10/28 22:20.
 * 当捕捉到异常后提供给使用者的操作接口
 */
interface CrashListener {

    /**
     * 只要有异常捕获到了就会触发此监听 使用者可以在这里定义之后的操作 比如重启app
     */
    fun onExceptionCaught(throwable: Throwable)

    /**
     * 第一次捕捉到异常进入假的消息循环触发此回调
     */
    fun onEnterFakeLoop()

    /**
     * ChoreographerException可能导致黑屏 所以需要区别对待 不应该简单的被捕获
     */
    fun onChoreographerExceptionCaught(throwable: Throwable)

    /**
     * 当“绷带”Exception被捕获 “绷带”Exception顾名思义就是已经进入了Fake Loop后捕获到的异常
     */
    fun onBandageExceptionCaught(throwable: Throwable)
}