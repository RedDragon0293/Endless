package cn.asone.endless.features.command

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.utils.BlockUtils
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.StringUtils
import cn.asone.endless.value.*
import net.minecraft.block.Block

class ModuleCommand(val module: AbstractModule) : AbstractCommand(module.name) {
    override fun onExecute(command: String) {
        val valueNames = module.getAllValue()
                .joinToString(separator = "/") { it.name.toLowerCase() }

        val args = command.split(' ').toTypedArray()
        if (args.isEmpty() || (args.size == 1 && args[0] == "")) {
            chatSyntax("$valueNames <值>")
            return
        }
        val currentValue = module.getValue(args[0])
        if (currentValue == null) {
            ClientUtils.chatError("找不到选项.")
            chatSyntax("$valueNames <值>")
        } else {
            if (currentValue is BoolValue) {
                if (args.size == 2)
                    currentValue.set(args[1] == "true")
                else
                    currentValue.set(!currentValue.get())
                ClientUtils.chatSuccess("设置功能 §b${module.name} §7${args[0]}§a 的值为 §8${if (currentValue.get()) "true" else "false"} §a.")
                playEditSound()
            } else {
                if (args.size < 2) {
                    if (currentValue is IntValue || currentValue is FloatValue || currentValue is TextValue)
                        chatSyntax("${args[0].toLowerCase()} <值>")
                    else if (currentValue is ListValue)
                        chatSyntax("${args[0].toLowerCase()} <${currentValue.values.joinToString(separator = "/").toLowerCase()}>")
                    return
                }
                try {
                    when (currentValue) {
                        is BlockValue -> {
                            val id = try {
                                args[1].toInt()
                            } catch (exception: NumberFormatException) {
                                val tmpId = Block.getBlockFromName(args[1])?.let { Block.getIdFromBlock(it) }
                                if (tmpId == null || tmpId <= 0) {
                                    ClientUtils.chatError("方块 §7${args[1]} §c不存在!")
                                    return
                                }
                                tmpId
                            }

                            currentValue.set(id)
                            ClientUtils.chatSuccess("设置功能 §b${module.name} §7${args[0].toLowerCase()}§a 的方块为 §8${BlockUtils.getBlockName(id)}§a.")
                            playEditSound()
                            return
                        }
                        is IntValue -> currentValue.set(args[1].toInt())
                        is FloatValue -> currentValue.set(args[1].toFloat())
                        is ListValue -> {
                            if (!currentValue.contains(args[1])) {
                                chatSyntax("${args[0].toLowerCase()} <${currentValue.values.joinToString(separator = "/").toLowerCase()}>")
                                return
                            }
                            currentValue.set(args[1])
                        }
                        is TextValue -> currentValue.set(StringUtils.toCompleteString(args, 1))
                    }

                    ClientUtils.chatSuccess("设置功能 §b${module.name} §7${args[0]}§a 的值为 §8${currentValue.get()}§a.")
                    playEditSound()
                } catch (e: NumberFormatException) {
                    ClientUtils.chatError("§7${args[2]}§c 不能被转换为数字!")
                    e.printStackTrace()
                    return
                }
            }
        }
    }
}