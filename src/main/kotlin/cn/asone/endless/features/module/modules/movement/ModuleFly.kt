package cn.asone.endless.features.module.modules.movement

import cn.asone.endless.event.EventHook
import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.features.module.ModuleCategory
import cn.asone.endless.option.AbstractOption
import cn.asone.endless.option.BoolOption
import cn.asone.endless.option.FloatOption
import cn.asone.endless.option.ListOption

object ModuleFly : AbstractModule(
    "Fly",
    "",
    ModuleCategory.MOVEMENT
) {
    private val modeOption = ListOption("mode", arrayOf("Vanilla", "NCP", "AAC5"), "Vanilla")
    private val modeVanillaSpeed = FloatOption("Speed", 1F, 0F..10F)
    private val modeNCPSpeed = FloatOption("Speed", 1F, 0F..10F)
    private val modeAAC5Fast = BoolOption("Fast", false)
    private val modeAAC5Test = ListOption("Test", arrayOf("Test1", "Test2", "Test3"), "Test1")
    override val options: ArrayList<AbstractOption<*>>
        get() = arrayListOf(modeOption)
    override val handledEvents: ArrayList<EventHook> = arrayListOf()

    init {
        modeOption.subOptions["Vanilla"]!!.add(modeVanillaSpeed)
        modeOption.subOptions["NCP"]!!.add(modeNCPSpeed)
        modeOption.subOptions["AAC5"]!!.add(modeAAC5Fast)
        modeOption.subOptions["AAC5"]!!.add(modeAAC5Test)
    }
}