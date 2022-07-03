package cn.asone.endless.features.command

import cn.asone.endless.features.module.AbstractModule
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.StringUtils
import cn.asone.endless.option.*

class ModuleCommand(val module: AbstractModule) : AbstractCommand(module.name) {
    /**
     * 解析一个选项 (Option) 的流程:
     * 1. 判断是否有参数, 没有参数输出提示
     * 2. 有参数则判断是否参数为子选项 (SubOption) 的名称, 若不是则根据参数设置值
     * 3. 是子选项的名称则递推至第一步
     */
    //.sprint checkServerSide checkServerSideOnlyGround true
    override fun onExecute(command: String) {
        val optionListsString = module.options.joinToString(separator = "/") { it.name.lowercase() }
        if (command == "") {
            chatSyntax("$optionListsString <值>")
            return
        }
        val args: MutableList<String> = command.split(' ').toMutableList()
        val currentOption = module.getOption(args[0])
        if (currentOption == null) {
            ClientUtils.chatError("找不到选项.")
            chatSyntax("$optionListsString <值>")
        } else {
            args.removeAt(0)
            processOption(currentOption, args, mutableListOf())
        }
    }

    private fun processOption(host: AbstractOption<*>, args: MutableList<String>, stack: MutableList<String>) {
        if (host is BoolOption) {
            if (args.isNotEmpty()) {
                val subOption = host.subOptions.find { it.name.equals(args[0], true) }
                if (subOption != null) {
                    args.removeAt(0)
                    stack.add(host.name)
                    processOption(subOption, args, stack)
                } else {
                    if (args[0].equals("true", ignoreCase = true) || args[0].equals("false", ignoreCase = true)) {
                        host.set(args[0].equals("true", ignoreCase = true))
                        ClientUtils.chatSuccess(
                            "设置功能 §b${module.name} §7${
                                if (stack.isNotEmpty()) stack.joinToString(
                                    separator = " ",
                                    postfix = " "
                                ) else ""
                            }${host.name}§a 的值为 §8${if (host.get()) "true" else "false"} §a."
                        )
                        playEditSound()
                    } else {
                        val optionListsString = host.subOptions.joinToString(separator = "/") { it.name.lowercase() }
                        chatSyntax(
                            "${
                                if (stack.isNotEmpty()) stack.joinToString(
                                    separator = " ",
                                    postfix = " "
                                ) else ""
                            }${host.name} $optionListsString <值>"
                        )
                    }
                }
            } else {
                ClientUtils.chatInfo("${host.name} 值: ${host.get()}")
                chatSyntax(
                    "${
                        if (stack.isNotEmpty()) stack.joinToString(
                            separator = " ",
                            postfix = " "
                        ) else ""
                    }${host.name} true/false"
                )
            }
        } else if (args.isEmpty()) {
            ClientUtils.chatInfo("${host.name} 值: ${host.get()}")
            if (host is IntOption || host is FloatOption || host is TextOption) {
                chatSyntax(
                    "${
                        if (stack.isNotEmpty()) stack.joinToString(
                            separator = " ",
                            postfix = " "
                        ) else ""
                    }${host.name} <值>"
                )
            } else if (host is ListOption) {
                chatSyntax(
                    "${
                        if (stack.isNotEmpty()) stack.joinToString(
                            separator = " ",
                            postfix = " "
                        ) else ""
                    }${host.name} <${
                        host.availableValues.joinToString(separator = "/").lowercase()
                    }>"
                )
            }
        } else {
            try {
                when (host) {
                    is IntOption -> {
                        host.set(args[0].toInt())
                        ClientUtils.chatSuccess(
                            "设置功能 §b${module.name} §7${
                                if (stack.isNotEmpty()) stack.joinToString(
                                    separator = " ",
                                    postfix = " "
                                ) else ""
                            }${host.name}§a 的值为 §8${host.get()}§a."
                        )
                        playEditSound()
                    }
                    is FloatOption -> {
                        host.set(args[0].toFloat())
                        ClientUtils.chatSuccess(
                            "设置功能 §b${module.name} §7${
                                if (stack.isNotEmpty()) stack.joinToString(
                                    separator = " ",
                                    postfix = " "
                                ) else ""
                            }${host.name}§a 的值为 §8${host.get()}§a."
                        )
                        playEditSound()
                    }
                    is TextOption -> {
                        host.set(StringUtils.toCompleteString(args.toTypedArray(), 0))
                        ClientUtils.chatSuccess(
                            "设置功能 §b${module.name} §7${
                                if (stack.isNotEmpty()) stack.joinToString(
                                    separator = " ",
                                    postfix = " "
                                ) else ""
                            }${host.name}§a 的值为 §8${host.get()}§a."
                        )
                        playEditSound()
                    }
                    /*                                      0                1         2
                     * .moduleName listOptionName availableElementName subOptionName value
                     */
                    is ListOption -> {
                        if (!host.contains(args[0])) {
                            ClientUtils.chatError("无效的参数.")
                            chatSyntax(
                                "${
                                    if (stack.isNotEmpty()) stack.joinToString(
                                        separator = " ",
                                        postfix = " "
                                    ) else ""
                                }${host.name} <${
                                    host.availableValues.joinToString(
                                        separator = "/"
                                    ).lowercase()
                                }>"
                            )
                            return
                        } else {
                            if (args.size >= 2) {
                                val subOption = host.subOptions[host.availableValues.find { it.equals(args[0], ignoreCase = true) }]!!.find { it.name.equals(args[1], true) }
                                if (subOption != null) {
                                    stack.add(host.name)
                                    stack.add(args[0])
                                    args.removeAt(0)
                                    args.removeAt(0)
                                    processOption(subOption, args, stack)
                                } else {
                                    ClientUtils.chatError("找不到选项.")
                                    chatSyntax(
                                        "${
                                            if (stack.isNotEmpty()) stack.joinToString(
                                                separator = " ",
                                                postfix = " "
                                            ) else ""
                                        }${host.name} ${args[0]} <${
                                            host.subOptions[host.availableValues.find { it.equals(args[0], ignoreCase = true) }]!!.joinToString(
                                                separator = "/", transform = { 
                                                    it.name
                                            }
                                            ).lowercase()
                                        }>"
                                    )
                                }
                            } else {
                                host.set(args[0])
                                ClientUtils.chatSuccess(
                                    "设置功能 §b${module.name} §7${
                                        if (stack.isNotEmpty()) stack.joinToString(
                                            separator = " ", postfix = " "
                                        ) else ""
                                    }${host.name}§a 的值为 §8${host.get()}§a."
                                )
                                playEditSound()
                            }
                        }
                    }
                }
            } catch (e: NumberFormatException) {
                ClientUtils.chatError("§7${args[0]}§c 类型错误!")
                e.printStackTrace()
                return
            }
        }
    }

}