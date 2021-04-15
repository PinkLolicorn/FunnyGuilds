package net.dzikoysk.funnyguilds.util.commons

import org.bukkit.ChatColor
import java.util.*

object ChatUtils {
    fun colored(message: String?): String? {
        return if (message != null) ChatColor.translateAlternateColorCodes('&', message) else null
    }

    fun colored(messages: List<String>): List<String?> {
        val colored: MutableList<String?> = ArrayList()
        for (message in messages) {
            colored.add(colored(message))
        }
        return colored
    }

    fun toString(list: Collection<String?>?, send: Boolean): String {
        val builder = StringBuilder()
        for (s in list!!) {
            builder.append(s)
            builder.append(',')
            if (send) {
                builder.append(' ')
            }
        }
        var s = builder.toString()
        if (send) {
            if (s.length > 2) {
                s = s.substring(0, s.length - 2)
            } else if (s.length > 1) {
                s = s.substring(0, s.length - 1)
            }
        }
        return s
    }

    fun fromString(s: String?): List<String?> {
        var list: List<String?> = ArrayList()
        if (s == null || s.isEmpty()) {
            return list
        }
        list = Arrays.asList(*s.split(",".toRegex()).toTypedArray())
        return list
    }

    fun appendDigit(number: Int): String {
        return if (number > 9) "" + number else "0$number"
    }

    fun appendDigit(number: String): String {
        return if (number.length > 1) "" + number else "0$number"
    }

    fun getPercent(dividend: Double, divisor: Double): String {
        return getPercent(dividend / divisor)
    }

    fun getPercent(fraction: Double): String {
        return String.format(Locale.US, "%.1f", 100.0 * fraction)
    }
}