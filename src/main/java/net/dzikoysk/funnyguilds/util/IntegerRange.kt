package net.dzikoysk.funnyguilds.util

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import org.apache.commons.lang3.StringUtils
import org.panda_lang.utilities.commons.function.Option
import java.util.*

class IntegerRange(val minRange: Int, val maxRange: Int) {

    class MissingFormatException(value: Int, rangeType: String) : RuntimeException("No format for value $value and range $rangeType found!")
    companion object {
        fun <V> inRange(value: Int, rangeMap: Map<IntegerRange?, V>?): Option<V> {
            for ((range, value1) in rangeMap!!) {
                if (value >= range!!.minRange && value <= range.maxRange) {
                    return Option.of(value1)
                }
            }
            return Option.none()
        }

        fun <V> inRangeToString(value: Int, rangeMap: Map<IntegerRange?, V>?): String {
            return inRange(value, rangeMap)
                .map { o: V -> Objects.toString(o) }
                .orElseGet(Integer.toString(value))
        }

        fun parseIntegerRange(rangeEntries: List<String>, color: Boolean): Map<IntegerRange, String?> {
            val parsed: MutableMap<IntegerRange, String?> = HashMap()
            for (rangeEntry in rangeEntries) {
                val rangeParts = rangeEntry.split(" ".toRegex(), 2).toTypedArray()
                if (rangeParts.size != 2) {
                    FunnyGuilds.Companion.getPluginLogger().parser("\"$rangeEntry\" is not a valid range String!")
                    continue
                }
                val rangeValue = rangeParts[0].trim { it <= ' ' }
                val splitOperator = if (rangeValue.startsWith("-")) rangeValue.indexOf('-', 1) else rangeValue.indexOf('-')
                if (splitOperator == -1) {
                    FunnyGuilds.Companion.getPluginLogger().parser("\"$rangeEntry\" is not a valid integer range String!")
                    continue
                }
                val minRangeValue = rangeValue.substring(0, splitOperator).trim { it <= ' ' }
                val maxRangeValue = rangeValue.substring(splitOperator + 1).trim { it <= ' ' }
                var minRange: Int
                var maxRange: Int
                minRange = try {
                    if (minRangeValue == "-*") Int.MIN_VALUE else minRangeValue.toInt()
                } catch (numberFormatException: NumberFormatException) {
                    FunnyGuilds.Companion.getPluginLogger().parser("\"$minRangeValue\" of integer range String \"$rangeEntry\" is not a valid integer!")
                    continue
                }
                maxRange = try {
                    if (maxRangeValue == "*") Int.MAX_VALUE else maxRangeValue.toInt()
                } catch (numberFormatException: NumberFormatException) {
                    FunnyGuilds.Companion.getPluginLogger().parser("\"$maxRangeValue\" of integer range String \"$rangeEntry\" is not a valid integer!")
                    continue
                }
                var valueString = StringUtils.join(rangeParts, " ", 1, rangeParts.size)
                if (rangeEntry.endsWith(" ")) {
                    valueString += " "
                }
                parsed[IntegerRange(minRange, maxRange)] = if (color) ChatUtils.colored(valueString) else valueString
            }
            return parsed
        }
    }
}