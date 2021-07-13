package cn.endless.utils.io

import cn.endless.utils.mc
import net.minecraft.util.ChatComponentTranslation

fun chat(message: String) = mc.ingameGUI.chatGUI.printChatMessage(ChatComponentTranslation(message))