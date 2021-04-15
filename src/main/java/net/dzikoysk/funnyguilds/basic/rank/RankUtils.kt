package net.dzikoysk.funnyguilds.basic.rank

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.util.IntegerRange
import org.apache.commons.lang3.StringUtils

object RankUtils {
    fun parseRank(source: User?, rankTop: String?): String? {
        if (!rankTop!!.contains("TOP-")) {
            return null
        }
        val i = getIndex(rankTop)
        if (i <= 0) {
            FunnyGuilds.Companion.getPluginLogger().error("Index in TOP- must be greater or equal to 1!")
            return null
        }
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        if (rankTop.contains("GTOP")) {
            val guild: Guild = RankManager.Companion.getInstance().getGuild(i)
                ?: return StringUtils.replace(rankTop, "{GTOP-$i}", FunnyGuilds.Companion.getInstance().getMessageConfiguration().gtopNoValue)
            val points = guild.rank.points
            var pointsFormat = config.gtopPoints
            if (!pointsFormat!!.isEmpty()) {
                pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.Companion.inRangeToString<String?>(points, config.pointsFormat))
                pointsFormat = pointsFormat.replace("{POINTS}", points.toString())
            }
            var guildTag = guild.tag
            if (config.playerListUseRelationshipColors) {
                guildTag = StringUtils.replace(config.prefixOther, "{TAG}", guild.tag)
                if (source != null && source.hasGuild()) {
                    val sourceGuild = source.guild
                    if (sourceGuild!!.allies!!.contains(guild)) {
                        guildTag = StringUtils.replace(config.prefixAllies, "{TAG}", guild.tag)
                    } else if (sourceGuild.uuid == guild.uuid) {
                        guildTag = StringUtils.replace(config.prefixOur, "{TAG}", guild.tag)
                    }
                }
            }
            return StringUtils.replace(rankTop, "{GTOP-$i}", guildTag + pointsFormat)
        } else if (rankTop.contains("PTOP")) {
            val user: User = RankManager.Companion.getInstance().getUser(i)
                ?: return StringUtils.replace(rankTop, "{PTOP-$i}", FunnyGuilds.Companion.getInstance().getMessageConfiguration().ptopNoValue)
            val points = user.rank.points
            var pointsFormat = config.ptopPoints
            if (!pointsFormat!!.isEmpty()) {
                pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.Companion.inRangeToString<String?>(points, config.pointsFormat))
                pointsFormat = pointsFormat.replace("{POINTS}", points.toString())
            }
            return StringUtils.replace(rankTop, "{PTOP-$i}", (if (user.isOnline) config.ptopOnline else config.ptopOffline) + user.name + pointsFormat)
        }
        return null
    }

    fun getIndex(rank: String?): Int {
        val sb = StringBuilder()
        var open = false
        var start = false
        var result = -1
        for (c in rank!!.toCharArray()) {
            var end = false
            when (c) {
                '{' -> open = true
                '-' -> start = true
                '}' -> end = true
                else -> if (open && start) {
                    sb.append(c)
                }
            }
            if (end) {
                break
            }
        }
        try {
            result = sb.toString().toInt()
        } catch (e: NumberFormatException) {
            FunnyGuilds.Companion.getPluginLogger().parser("$rank contains an invalid number: $sb")
        }
        return result
    }
}