package cn.asone.endless.features.module.modules.movement

import cn.asone.endless.event.EventHook
import cn.asone.endless.event.UpdateEvent
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.utils.MovementUtils
import cn.asone.endless.option.AbstractOption
import cn.asone.endless.option.BoolOption
import net.minecraft.potion.Potion

object ModuleSprint : AbstractModule(
    "Sprint",
    "Automatically sprints all the time.",
    ModuleCategory.MOVEMENT
) {
    override val handledEvents: ArrayList<EventHook> = arrayListOf(
        EventHook(UpdateEvent::class.java)
    )
    private val allDirectionsOption = BoolOption("AllDirections", false)
    private val blindnessOption = BoolOption("Blindness", true)
    private val foodOption = BoolOption("Food", true)
    private val checkServerSide = BoolOption("CheckServerSide", false)
    private val checkServerSideGround = BoolOption("CheckServerSideOnlyGround", false)

    override val options: ArrayList<AbstractOption<*>> = arrayListOf(
        allDirectionsOption,
        blindnessOption,
        checkServerSide,
        foodOption
    )

    init {
        checkServerSide.subOptions.add(checkServerSideGround)
    }

    override fun onUpdate() {
        if (allDirectionsOption.get()) {
            mc.thePlayer.sprinting = true
            return
        }
        if (!MovementUtils.isMoving() || mc.thePlayer.isSneaking
            || (blindnessOption.get() && mc.thePlayer.isPotionActive(Potion.blindness))
            || (foodOption.get() && !(mc.thePlayer.foodStats.foodLevel > 6.0F || mc.thePlayer.capabilities.allowFlying))
        ) {
            mc.thePlayer.sprinting = false
            return
        }

        if (mc.thePlayer.movementInput.moveForward >= 0.8F) mc.thePlayer.sprinting = true
    }
}