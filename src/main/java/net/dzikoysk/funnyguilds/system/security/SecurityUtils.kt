package net.dzikoysk.funnyguilds.system.security

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import org.apache.commons.lang3.StringUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object SecurityUtils {
    private const val COMPENSATION_RATIO = 0.0056
    fun compensationMs(millisecond: Double): Double {
        return millisecond * COMPENSATION_RATIO
    }

    fun sendToOperator(player: Player, cheat: String?, note: String?) {
        val messages: MessageConfiguration = FunnyGuilds.Companion.getInstance().getMessageConfiguration()
        var message: String? = messages.securitySystemPrefix + messages.securitySystemInfo
        var messageNote: String? = messages.securitySystemPrefix + messages.securitySystemNote
        message = StringUtils.replace(message, "{PLAYER}", player.name)
        message = StringUtils.replace(message, "{CHEAT}", cheat)
        messageNote = StringUtils.replace(messageNote, "{NOTE}", note)
        Bukkit.broadcast(ChatUtils.colored(message)!!, "funnyguilds.admin")
        Bukkit.broadcast(ChatUtils.colored(messageNote)!!, "funnyguilds.admin")
    }

    fun addViolationLevel(user: User?) {
        val playersViolationLevel = SecuritySystem.getPlayersViolationLevel()
        playersViolationLevel!![user] = playersViolationLevel!!.getOrDefault(user, 0)!! + 1
        Bukkit.getScheduler().runTaskLater(FunnyGuilds.Companion.getInstance(), Runnable { playersViolationLevel!!.remove(user) }, 18000)
    }

    fun isBlocked(user: User?): Boolean {
        return SecuritySystem.getPlayersViolationLevel().getOrDefault(user, 0) > 1
    }
}