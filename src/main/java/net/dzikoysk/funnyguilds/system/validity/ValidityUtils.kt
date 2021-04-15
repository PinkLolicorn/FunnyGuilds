package net.dzikoysk.funnyguilds.system.validity

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import org.bukkit.Bukkit

object ValidityUtils {
    fun broadcast(guild: Guild?) {
        if (guild == null || guild.name == null) {
            return
        }
        var message: String = FunnyGuilds.Companion.getInstance().getMessageConfiguration().broadcastValidity
            .replace("{GUILD}", guild.name)
            .replace("{TAG}", guild.tag)
            .replace("{GUILD}", guild.name)
        val region = guild.region
        message = if (region != null && region.center != null) {
            message
                .replace("{X}", Integer.toString(region.center.blockX))
                .replace("{Y}", Integer.toString(region.center.blockY))
                .replace("{Z}", Integer.toString(region.center.blockZ))
        } else {
            message
                .replace("{X}", "Brak informacji")
                .replace("{Y}", "Brak informacji")
                .replace("{Z}", "Brak informacji")
        }
        Bukkit.broadcastMessage(message)
    }
}