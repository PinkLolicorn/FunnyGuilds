package net.dzikoysk.funnyguilds.data.flat

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.BasicType
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.basic.user.UserUtils
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils
import net.dzikoysk.funnyguilds.util.YamlWrapper
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils
import org.bukkit.Location
import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FlatGuild(private val guild: Guild?) {
    fun serialize(flatDataModel: FlatDataModel): Boolean {
        if (guild!!.name == null) {
            FunnyGuilds.Companion.getPluginLogger().error("[Serialize] Cannot serialize guild! Caused by: name is null")
            return false
        }
        if (guild.tag == null) {
            FunnyGuilds.Companion.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.name + "! Caused by: tag is null")
            return false
        }
        if (guild.owner == null) {
            FunnyGuilds.Companion.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.name + "! Caused by: owner is null")
            return false
        }
        if (guild.region == null && FunnyGuilds.Companion.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.Companion.getPluginLogger().error("[Serialize] Cannot serialize guild: " + guild.name + "! Caused by: region is null")
            return false
        }
        val file = flatDataModel.loadCustomFile(BasicType.GUILD, guild.name)
        val wrapper = YamlWrapper(file)
        wrapper["uuid"] = guild.uuid.toString()
        wrapper["name"] = guild.name
        wrapper["tag"] = guild.tag
        wrapper["owner"] = guild.owner!!.name
        wrapper["home"] = LocationUtils.toString(guild.home)
        wrapper["members"] = UserUtils.getNames(guild.members)
        wrapper["region"] = RegionUtils.toString(guild.region)
        wrapper["regions"] = null
        wrapper["allies"] = GuildUtils.getNames(guild.allies)
        wrapper["enemies"] = GuildUtils.getNames(guild.enemies)
        wrapper["born"] = guild.born
        wrapper["validity"] = guild.validity
        wrapper["attacked"] = guild.attacked
        wrapper["lives"] = guild.lives
        wrapper["ban"] = guild.ban
        wrapper["pvp"] = guild.pvP
        wrapper["deputy"] = ChatUtils.toString(UserUtils.getNames(guild.deputies), false)
        wrapper.save()
        return true
    }

    companion object {
        fun deserialize(file: File?): Guild? {
            val configuration: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
            val wrapper = YamlWrapper(file)
            val id = wrapper.getString("uuid")
            val name = wrapper.getString("name")
            val tag = wrapper.getString("tag")
            val ownerName = wrapper.getString("owner")
            val deputyName = wrapper.getString("deputy")
            val hs = wrapper.getString("home")
            val regionName = wrapper.getString("region")
            val pvp = wrapper.getBoolean("pvp")
            var born = wrapper.getLong("born")
            var validity = wrapper.getLong("validity")
            val attacked = wrapper.getLong("attacked")
            val ban = wrapper.getLong("ban")
            var lives = wrapper.getInt("lives")
            if (name == null) {
                FunnyGuilds.Companion.getPluginLogger().error("[Deserialize] Cannot deserialize guild! Caused by: name is null")
                return null
            }
            if (tag == null) {
                FunnyGuilds.Companion.getPluginLogger().error("[Deserialize] Cannot deserialize guild: $name! Caused by: tag is null")
                return null
            }
            if (ownerName == null) {
                FunnyGuilds.Companion.getPluginLogger().error("[Deserialize] Cannot deserialize guild: $name! Caused by: owner is null")
                return null
            }
            if (regionName == null && configuration.regionsEnabled) {
                FunnyGuilds.Companion.getPluginLogger().error("[Deserialize] Cannot deserialize guild: $name! Caused by: region is null")
                return null
            }
            var memberNames = loadSet(wrapper, "members")
            val allyNames: Set<String?>? = loadSet(wrapper, "allies")
            val enemyNames: Set<String?>? = loadSet(wrapper, "enemies")
            val region = RegionUtils.get(regionName)
            if (region == null && configuration.regionsEnabled) {
                FunnyGuilds.Companion.getPluginLogger().error("[Deserialize] Cannot deserialize guild: $name! Caused by: region (object) is null")
                return null
            }
            var uuid = UUID.randomUUID()
            if (id != null && !id.isEmpty()) {
                uuid = UUID.fromString(id)
            }
            val owner: User = User.Companion.get(ownerName)
            var deputies: Set<User?>? = ConcurrentHashMap.newKeySet(1)
            if (deputyName != null && !deputyName.isEmpty()) {
                deputies = UserUtils.getUsers(ChatUtils.fromString(deputyName))
            }
            var home: Location? = null
            if (region != null) {
                home = region.center
                if (hs != null) {
                    home = LocationUtils.parseLocation(hs)
                }
            }
            if (memberNames == null || memberNames.isEmpty()) {
                memberNames = HashSet()
                memberNames.add(ownerName)
            }
            val members = UserUtils.getUsers(memberNames)
            val allies = loadGuilds(allyNames)
            val enemies = loadGuilds(enemyNames)
            if (born == 0L) {
                born = System.currentTimeMillis()
            }
            if (validity == 0L) {
                validity = System.currentTimeMillis() + configuration.validityStart
            }
            if (lives == 0) {
                lives = configuration.warLives
            }
            val values = arrayOfNulls<Any>(17)
            values[0] = uuid
            values[1] = name
            values[2] = tag
            values[3] = owner
            values[4] = home
            values[5] = region
            values[6] = members
            values[7] = allies
            values[8] = enemies
            values[9] = born
            values[10] = validity
            values[11] = attacked
            values[12] = lives
            values[13] = ban
            values[14] = deputies
            values[15] = pvp
            return DeserializationUtils.deserializeGuild(values)
        }

        private fun loadSet(data: YamlWrapper, key: String): MutableSet<String?>? {
            val collection = data[key]!!
            if (collection is List<*>) {
                return HashSet(collection as List<String?>)
            } else if (collection is Set<*>) {
                return collection as MutableSet<String?>
            }
            return null
        }

        private fun loadGuilds(guilds: Collection<String?>?): Set<Guild> {
            val set: MutableSet<Guild> = HashSet()
            if (guilds == null) {
                return set
            }
            for (guildName in guilds) {
                val guild = GuildUtils.getByName(guildName)
                if (guild != null) {
                    set.add(guild)
                }
            }
            return set
        }
    }
}