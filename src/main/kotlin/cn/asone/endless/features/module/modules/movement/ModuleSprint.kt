package cn.asone.endless.features.module.modules.movement

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.UpdateEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.utils.MovementUtils
import cn.asone.endless.value.AbstractValue
import cn.asone.endless.value.BoolValue
import net.minecraft.potion.Potion

object ModuleSprint : AbstractModule(
    "Sprint",
    "Automatically sprints all the time.",
    ModuleCategory.MOVEMENT
) {
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(UpdateEvent::class.java)
    )
    private val allDirectionsValue = BoolValue("AllDirections", false)
    private val blindnessValue = BoolValue("Blindness", true)
    private val foodValue = BoolValue("Food", true)
    private val checkServerSide = BoolValue("CheckServerSide", false)
    private val checkServerSideGround = BoolValue("CheckServerSideOnlyGround", false)

    override val values: ArrayList<AbstractValue<*>> = arrayListOf(
        allDirectionsValue,
        blindnessValue,
        checkServerSide,
        foodValue
    )

    init {
        checkServerSide.subValue.add(checkServerSideGround)
    }

    override fun onUpdate() {
        if (allDirectionsValue.get()) {
            mc.thePlayer.sprinting = true
            return
        }
        if (!MovementUtils.isMoving() || mc.thePlayer.isSneaking
            || (blindnessValue.get() && mc.thePlayer.isPotionActive(Potion.blindness))
            || (foodValue.get() && !(mc.thePlayer.foodStats.foodLevel > 6.0F || mc.thePlayer.capabilities.allowFlying))
        ) {
            mc.thePlayer.sprinting = false
            return
        }

        if (mc.thePlayer.movementInput.moveForward >= 0.8F) mc.thePlayer.sprinting = true
    }
}