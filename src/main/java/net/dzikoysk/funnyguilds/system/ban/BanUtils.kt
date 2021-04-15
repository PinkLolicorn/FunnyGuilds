package net.dzikoysk.funnyguilds.system.ban

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.basic.user.UserBan
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import org.apache.commons.lang3.StringUtils
import org.bukkit.ChatColor
import java.util.*

object BanUtils {
    fun ban(guild: Guild, time: Long, reason: String?) {
        guild.ban = time + System.currentTimeMillis()
        for (user in guild.members) {
            val player = user.player
            ban(user, time, reason)
            if (player != null && player.isOnline) {
                player.kickPlayer(getBanMessage(user))
            }
        }
    }

    fun ban(user: User?, time: Long, reason: String?) {
        var time = time
        time += System.currentTimeMillis()
        user.setBan(UserBan(reason, time))
    }

    fun unban(guild: Guild) {
        for (user in guild.members) {
            unban(user)
        }
    }

    fun unban(user: User?) {
        user.setBan(null)
    }

    fun checkIfBanShouldExpire(user: User) {
        if (!user.isBanned) {
            return
        }
        if (System.currentTimeMillis() > user.ban.banTime) {
            user.ban = null
        }
    }

    fun getBanMessage(user: User?): String? {
        var message: String? = FunnyGuilds.Companion.getInstance().getMessageConfiguration().banMessage
        if (!user!!.isBanned) {
            return StringUtils.EMPTY
        }
        val userBan = user.ban
        message = StringUtils.replace(
            message, "{NEWLINE}", """
     ${ChatColor.RESET}
     
     """.trimIndent()
        )
        message = StringUtils.replace(message, "{DATE}", FunnyGuilds.Companion.getInstance().getPluginConfiguration().dateFormat.format(Date(userBan.banTime)))
        message = StringUtils.replace(message, "{REASON}", userBan!!.reason)
        message = StringUtils.replace(message, "{PLAYER}", user.name)
        return ChatUtils.colored(message)
    }
}