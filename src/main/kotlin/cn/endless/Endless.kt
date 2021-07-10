package cn.endless

import kotlinx.serialization.Serializable

@Serializable
object Endless {
    const val CLIENT_NAME = "Endless"
    const val CLIENT_VERSION = "1.0.0"

    @JvmStatic
    fun startClient() {
    }

    fun stopClient() {
    }
}