package cn.asone.endless.utils

import net.minecraft.block.Block

object BlockUtils {
    @JvmStatic
    fun getBlockName(id: Int): String = Block.getBlockById(id)!!.localizedName
}