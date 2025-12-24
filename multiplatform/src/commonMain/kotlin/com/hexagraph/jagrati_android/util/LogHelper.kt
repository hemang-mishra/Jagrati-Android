package com.hexagraph.jagrati_android.util


expect object LogHelper {
    fun logException(exception: Throwable, tag: String = "Crashlytics")
    fun log(tag: String = "Crashlytics", message: String)
    fun setUserId(userId: String)
    fun setCustomKey(key: String, value: String)
    fun setCustomKey(key: String, value: Int)
    fun setCustomKey(key: String, value: Boolean)
    fun clearUserId()
    fun testCrash()
    fun logError(tag: String, message: String, throwable: Throwable? = null)
}
