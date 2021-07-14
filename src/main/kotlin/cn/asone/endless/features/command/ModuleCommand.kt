package cn.asone.endless.features.command

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.utils.BlockUtils
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.StringUtils
import cn.asone.endless.value.*
import net.minecraft.block.Block

class ModuleCommand(val module: AbstractModule) : AbstractCommand(module.name) {
    override fun onExecute(command: String) {
        val valueNames = module.values
                .joinToString(separator = "/") { it.name.toLowerCase() }

        val args = command.split(' ').toTypedArray()
        if (args.isEmpty() || (args.size == 1 && args[0] == "")) {
            chatSyntax("$valueNames <值>")
            return
        }
        val currentValue = module.getValue(args[0])
        if (currentValue == null) {
            /*
             *  处理subValue
             */
            for (moduleValue in module.values) {
                if (moduleValue is BoolValue) {
                    for (subValue in moduleValue.subValue) {
                        if (subValue.name.equals(args[0], true)) {
                            processValue(subValue, args)
                            return
                        }
                    }
                } else if (moduleValue is ListValue) {
                    for (subValue in moduleValue.subValue) {
                        for (value in subValue.value) {
                            if (value.name.equals(args[0], true)) {
                                processValue(value, args)
                                return
                            }
                        }
                    }
                }
            }
            ClientUtils.chatError("找不到选项.")
            chatSyntax("$valueNames <值>")
        } else {
            processValue(currentValue, args)
        }
    }

    private fun processValue(value: Value<*>, args: Array<String>) {
        if (value is BoolValue) {
            if (args.size == 2)
                value.set(args[1] == "true")
            else
                value.set(!value.get())
            ClientUtils.chatSuccess("设置功能 §b${module.name} §7${args[0]}§a 的值为 §8${if (value.get()) "true" else "false"} §a.")
            playEditSound()
        } else {
            if (args.size < 2) {
                if (value is IntValue || value is FloatValue || value is TextValue)
                    chatSyntax("${args[0].toLowerCase()} <值>")
                else if (value is ListValue)
                    chatSyntax("${args[0].toLowerCase()} <${value.values.joinToString(separator = "/").toLowerCase()}>")
                return
            }
            try {
                when (value) {
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

                        value.set(id)
                        ClientUtils.chatSuccess("设置功能 §b${module.name} §7${args[0].toLowerCase()}§a 的方块为 §8${BlockUtils.getBlockName(id)} §a.")
                        playEditSound()
                        return
                    }
                    is IntValue -> value.set(args[1].toInt())
                    is FloatValue -> value.set(args[1].toFloat())
                    is ListValue -> {
                        if (!value.contains(args[1])) {
                            chatSyntax("${args[0].toLowerCase()} <${value.values.joinToString(separator = "/").toLowerCase()}>")
                            return
                        }
                        value.set(args[1])
                    }
                    is TextValue -> value.set(StringUtils.toCompleteString(args, 1))
                }

                ClientUtils.chatSuccess("设置功能 §b${module.name} §7${args[0]}§a 的值为 §8${value.get()} §a.")
                playEditSound()
            } catch (e: NumberFormatException) {
                ClientUtils.chatError("§7${args[2]}§c 不能被转换为数字!")
                e.printStackTrace()
                return
            }
        }
    }
}