package net.dzikoysk.funnyguilds.util.commons.bukkit

import net.dzikoysk.funnyguilds.FunnyGuilds
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

object LocationUtils {
    fun flatDistance(a: Location, b: Location?): Double {
        return Math.sqrt(Math.pow(b!!.x - a.x, 2.0) + Math.pow(b.z - a.z, 2.0))
    }

    fun checkWorld(player: Player): Boolean {
        val blockedWorlds: List<String> = FunnyGuilds.Companion.getInstance().getPluginConfiguration().blockedWorlds
        return blockedWorlds != null && blockedWorlds.size > 0 && blockedWorlds.contains(player.world.name)
    }

    fun parseLocation(string: String?): Location? {
        if (string == null) {
            return null
        }
        val shs = string.split(",".toRegex()).toTypedArray()
        if (shs.size < 4) {
            return null
        }
        var world = Bukkit.getWorld(shs[0])
        if (world == null) {
            world = Bukkit.getWorlds()[0]
        }
        return Location(world, shs[1].toInt().toDouble(), shs[2].toInt().toDouble(), shs[3].toInt().toDouble())
    }

    fun equals(location: Location, to: Location): Boolean {
        return location.blockX == to.blockX && location.blockY == to.blockY && location.blockZ == to.blockZ
    }

    fun equalsFlat(location: Location, to: Location): Boolean {
        return location.blockX == to.blockX && location.blockZ == to.blockZ
    }

    fun toString(location: Location?): String? {
        return if (location == null) {
            null
        } else location.world!!.name +
                "," +
                location.blockX +
                "," +
                location.blockY +
                "," +
                location.blockZ
    }
}