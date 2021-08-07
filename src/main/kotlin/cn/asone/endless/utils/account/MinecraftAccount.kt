package cn.asone.endless.utils.account

class MinecraftAccount(val username: String, val password: String = "", var inGameName: String = "") {

    val isCracked: Boolean
        get() = password.isEmpty()
}