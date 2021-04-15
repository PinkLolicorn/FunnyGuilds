package net.dzikoysk.funnyguilds.util.commons

import net.dzikoysk.funnyguilds.FunnyGuilds
import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtils {
    fun parseTime(string: String?): Long {
        if (string == null || string.isEmpty()) {
            return 0
        }
        val type = Stack<Char>()
        val value = StringBuilder()
        var calc = false
        var time: Long = 0
        for (c in string.toCharArray()) {
            when (c) {
                'd', 'h', 'm', 's' -> {
                    if (!calc) {
                        type.push(c)
                    }
                    try {
                        val i = value.toString().toInt().toLong()
                        when (type.pop()) {
                            'd' -> time += i * 86400000L
                            'h' -> time += i * 3600000L
                            'm' -> time += i * 60000L
                            's' -> time += i * 1000L
                        }
                    } catch (e: NumberFormatException) {
                        FunnyGuilds.Companion.getPluginLogger().parser("Unknown number: $value")
                        return time
                    }
                    type.push(c)
                    calc = true
                }
                else -> value.append(c)
            }
        }
        return time
    }

    fun getDurationBreakdown(millis: Long): String {
        var millis = millis
        if (millis == 0L) {
            return "0"
        }
        val days = TimeUnit.MILLISECONDS.toDays(millis)
        if (days > 0) {
            millis -= TimeUnit.DAYS.toMillis(days)
        }
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        if (hours > 0) {
            millis -= TimeUnit.HOURS.toMillis(hours)
        }
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        if (minutes > 0) {
            millis -= TimeUnit.MINUTES.toMillis(minutes)
        }
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        if (seconds > 0) {
            millis -= TimeUnit.SECONDS.toMillis(seconds)
        }
        val sb = StringBuilder()
        if (days > 0) {
            sb.append(days)
            if (days == 1L) {
                sb.append(" dzien ")
            } else {
                sb.append(" dni ")
            }
        }
        if (hours > 0) {
            sb.append(hours)
            val last = hours % 10
            val lastTwo = hours % 100
            if (hours == 1L) {
                sb.append(" godzine ")
            } else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
                sb.append(" godziny ")
            } else {
                sb.append(" godzin ")
            }
        }
        if (minutes > 0) {
            sb.append(minutes)
            val last = minutes % 10
            val lastTwo = minutes % 100
            if (minutes == 1L) {
                sb.append(" minute ")
            } else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
                sb.append(" minuty ")
            } else {
                sb.append(" minut ")
            }
        }
        if (seconds > 0) {
            sb.append(seconds)
            val last = seconds % 10
            val lastTwo = seconds % 100
            if (seconds == 1L) {
                sb.append(" sekunde ")
            } else if (last < 5 && (lastTwo < 11 || lastTwo > 14)) {
                sb.append(" sekundy ")
            } else {
                sb.append(" sekund ")
            }
        }
        return sb.toString()
    }
}