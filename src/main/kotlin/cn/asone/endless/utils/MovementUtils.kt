package cn.asone.endless.utils

object MovementUtils {
    @JvmStatic
    fun isMoving() = mc.thePlayer != null && (mc.thePlayer!!.movementInput.moveForward != 0F || mc.thePlayer!!.movementInput.moveStrafe != 0F)
}