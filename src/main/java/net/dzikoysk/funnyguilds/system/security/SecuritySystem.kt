package net.dzikoysk.funnyguilds.system.security

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.system.security.cheat.SecurityFreeCam
import net.dzikoysk.funnyguilds.system.security.cheat.SecurityReach
import net.dzikoysk.funnyguilds.util.FunnyBox
import net.dzikoysk.funnyguilds.util.nms.Reflections
import org.bukkit.entity.Player

object SecuritySystem {
    private const val ADDITIONAL_SNEAKING_HEIGHT_CURSOR = 0.35
    val playersViolationLevel: MutableMap<User?, Int?> = HashMap()
    fun onHitCrystal(player: Player, guild: Guild?): Boolean {
        scan(player, SecurityType.GUILD, guild!!)
        return SecurityUtils.isBlocked(User.Companion.get(player))
    }

    private fun scan(player: Player, securityType: SecurityType, vararg values: Any) {
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        if (!FunnyGuilds.Companion.getInstance().getPluginConfiguration().regionsEnabled) {
            return
        }
        if (!config.systemSecurityEnable) {
            return
        }
        var guild: Guild? = null
        for (value in values) {
            if (value is Guild) {
                guild = value
            }
        }
        if (securityType == SecurityType.GUILD) {
            if (guild == null) {
                return
            }
            val center = guild.region.center
            val x = center!!.x + 0.5
            val y = center.y
            val z = center.z + 0.5
            val eye = player.eyeLocation
            val direction = eye.direction
            val origin = if (player.isSneaking && !Reflections.USE_PRE_9_METHODS) eye.add(0.0, ADDITIONAL_SNEAKING_HEIGHT_CURSOR, 0.0).toVector() else eye.toVector()
            val funnyBox = if ("ender_crystal".equals(config.createType, ignoreCase = true)) FunnyBox(x - 1.0, y - 1.0, z - 1.0, x + 1.0, y + 1.0, z + 1.0) else FunnyBox.Companion.of(
                player.world.getBlockAt(
                    center
                )
            )
            val rayTraceResult = funnyBox.rayTrace(origin, direction, 6.0) ?: return
            val hitPoint = rayTraceResult.hitPosition
            val distance = hitPoint!!.distance(origin)
            SecurityFreeCam.on(player, origin, hitPoint, distance)
            SecurityReach.on(player, distance)
            return
        }
        throw IllegalArgumentException("unknown securityType: $securityType")
    }
}