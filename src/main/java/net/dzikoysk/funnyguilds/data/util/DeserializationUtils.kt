package net.dzikoysk.funnyguilds.data.util

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.*
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.basic.user.UserBan
import org.bukkit.Location
import java.util.*

object DeserializationUtils {
    fun deserializeGuild(values: Array<Any>?): Guild? {
        if (values == null) {
            FunnyGuilds.Companion.getPluginLogger().error("[Deserialize] Cannot deserialize guild! Caused by: null")
            return null
        }
        val guild: Guild = Guild.Companion.getOrCreate(values[0] as UUID)
        guild.name = values[1] as String
        guild.tag = if (FunnyGuilds.Companion.getInstance().getPluginConfiguration().guildTagKeepCase) values[2] as String else if (FunnyGuilds.Companion.getInstance()
                .getPluginConfiguration().guildTagUppercase
        ) (values[2] as String).toUpperCase() else (values[2] as String).toLowerCase()
        guild.owner = values[3] as User
        guild.home = values[4] as Location
        guild.region = values[5] as Region
        guild.members = values[6] as Set<User?>
        guild.allies = values[7] as MutableSet<Guild?>
        guild.enemies = values[8] as MutableSet<Guild?>
        guild.born = values[9] as Long
        guild.validity = values[10] as Long
        guild.attacked = values[11] as Long
        guild.lives = values[12] as Int
        guild.ban = values[13] as Long
        guild.deputies = values[14] as MutableSet<User?>
        guild.pvP = values[15] as Boolean
        guild.deserializationUpdate()
        return guild
    }

    fun deserializeRegion(values: Array<Any>?): Region? {
        if (values == null) {
            FunnyGuilds.Companion.getPluginLogger().error("Cannot deserialize region! Caused by: null")
            return null
        }
        val region: Region = Region.Companion.getOrCreate(values[0] as String)
        region.center = values[1] as Location
        region.size = values[2] as Int
        region.enlarge = values[3] as Int
        region.update()
        return region
    }

    fun deserializeUser(values: Array<Any>): User {
        val playerUniqueId = UUID.fromString(values[0] as String)
        val playerName = values[1] as String
        val user: User = User.Companion.create(playerUniqueId, playerName)
        user.rank.points = values[2] as Int
        user.rank.kills = values[3] as Int
        user.rank.deaths = values[4] as Int
        val banTime = values[5] as Long
        if (banTime > 0) {
            user.ban = UserBan(values[6] as String, banTime)
        }
        return user
    }
}