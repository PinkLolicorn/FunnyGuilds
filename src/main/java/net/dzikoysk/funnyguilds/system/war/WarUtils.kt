package net.dzikoysk.funnyguilds.system.war

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.util.commons.*
import org.apache.commons.lang3.StringUtils
import org.bukkit.entity.Player

object WarUtils {
    fun message(player: Player, i: Int, vararg values: Any) {
        val messages: MessageConfiguration = FunnyGuilds.Companion.getInstance().getMessageConfiguration()
        var message: String? = null
        when (i) {
            0 -> message = messages.warHasNotGuild
            1 -> message = messages.warAlly
            2 -> {
                message = messages.warWait
                message = StringUtils.replace(message, "{TIME}", TimeUtils.getDurationBreakdown(values[0] as Long))
            }
            3 -> {
                message = messages.warAttacker
                message = StringUtils.replace(message, "{ATTACKED}", (values[0] as Guild).tag)
            }
            4 -> {
                message = messages.warAttacked
                message = StringUtils.replace(message, "{ATTACKER}", (values[0] as Guild).tag)
            }
            5 -> message = messages.warDisabled
        }
        player.sendMessage(message!!)
    }

    fun getWinMessage(conqueror: Guild?, loser: Guild?): String {
        return FunnyGuilds.Companion.getInstance().getMessageConfiguration().warWin
            .replace("{WINNER}", conqueror.getTag())
            .replace("{LOSER}", loser.getTag())
    }

    fun getLoseMessage(conqueror: Guild?, loser: Guild?): String {
        return FunnyGuilds.Companion.getInstance().getMessageConfiguration().warLose
            .replace("{WINNER}", conqueror.getTag())
            .replace("{LOSER}", loser.getTag())
    }

    fun getBroadcastMessage(conqueror: Guild?, loser: Guild?): String {
        return FunnyGuilds.Companion.getInstance().getMessageConfiguration().broadcastWar
            .replace("{WINNER}", conqueror.getTag())
            .replace("{LOSER}", loser.getTag())
    }
}