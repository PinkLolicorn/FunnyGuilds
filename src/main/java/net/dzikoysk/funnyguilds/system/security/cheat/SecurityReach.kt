package net.dzikoysk.funnyguilds.system.security.cheat

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.system.security.SecurityUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.MinecraftServerUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.PingUtils
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.text.DecimalFormat

object SecurityReach {
    private val FORMAT = DecimalFormat("##.##")
    private const val CREATIVE_REACH = 4.5
    private const val SURVIVAL_REACH = 3.0
    private const val IMPORTANCE_OF_PING = 0.93
    private const val IMPORTANCE_OF_TPS = 10.0
    fun on(player: Player, distance: Double) {
        val messages: MessageConfiguration = FunnyGuilds.Companion.getInstance().getMessageConfiguration()
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        val ping = PingUtils.getPing(player).toDouble()
        val tpsDelayMs = 1000.0 / MinecraftServerUtils.getRecentTPS(0) - 50.0
        var compensation = if (player.gameMode == GameMode.CREATIVE) CREATIVE_REACH else SURVIVAL_REACH
        compensation += config.reachCompensation
        compensation += SecurityUtils.compensationMs(IMPORTANCE_OF_PING * ping)
        compensation += SecurityUtils.compensationMs(IMPORTANCE_OF_TPS * tpsDelayMs)
        if (distance < compensation) {
            return
        }
        val message = messages.securitySystemReach.replace("{DISTANCE}", FORMAT.format(distance))
        SecurityUtils.addViolationLevel(User.Companion.get(player))
        SecurityUtils.sendToOperator(player, "Reach", message)
    }
}