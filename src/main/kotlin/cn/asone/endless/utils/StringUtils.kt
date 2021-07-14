package cn.asone.endless.utils

object StringUtils {
    @JvmStatic
    fun toCompleteString(args: Array<String>, start: Int): String {
        return if (args.size <= start) "" else java.lang.String.join(" ", *args.copyOfRange(start, args.size))
    }
}