package net.dzikoysk.funnyguilds.system.protection

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils
import net.dzikoysk.funnyguilds.basic.user.User
import org.apache.commons.lang3.tuple.Pair
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

object ProtectionSystem {
    fun isProtected(player: Player?, location: Location?, includeBuildLock: Boolean): Boolean {
        if (player == null || location == null) {
            return false
        }
        if (player.hasPermission("funnyguilds.admin.build")) {
            return false
        }
        val region = RegionUtils.getAt(location) ?: return false
        val guild = region.guild
        if (guild == null || guild.name == null) {
            return false
        }
        val user: User = User.Companion.get(player)
        if (!guild.members.contains(user)) {
            return true
        }
        if (includeBuildLock && !guild.canBuild()) {
            player.sendMessage(
                FunnyGuilds.Companion.getInstance().getMessageConfiguration().regionExplodeInteract.replace(
                    "{TIME}",
                    java.lang.Long.toString(TimeUnit.MILLISECONDS.toSeconds(guild.build - System.currentTimeMillis()))
                )
            )
            return true
        }
        if (location == region.heart) {
            val heartMaterial: Pair<Material, Byte> = FunnyGuilds.Companion.getInstance().getPluginConfiguration().createMaterial
            return heartMaterial != null && heartMaterial.left != Material.AIR
        }
        return false
    }

    fun isProtected(player: Player?, loc: Location?): Boolean {
        return isProtected(player, loc, false)
    }
}