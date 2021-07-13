package cn.endless.features.command

open class SubCommand(
        name: String,
        override val onExecute: (String?) -> Unit = {},
        vararg subCommands: SubCommand,
        val parameterType: ParameterType = ParameterType.NON_NULL
) : Command(name) {
    override val subCommands = subCommands.toMutableList()
}