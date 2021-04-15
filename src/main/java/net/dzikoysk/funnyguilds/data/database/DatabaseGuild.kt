package net.dzikoysk.funnyguilds.data.database

import com.google.common.collect.Sets
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.basic.user.UserUtils
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils
import net.dzikoysk.funnyguilds.data.database.element.SQLTable
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils
import java.sql.ResultSet
import java.util.*

object DatabaseGuild {
    fun deserialize(rs: ResultSet?): Guild? {
        if (rs == null) {
            return null
        }
        var id: String? = null
        var name: String? = null
        try {
            id = rs.getString("uuid")
            name = rs.getString("name")
            val tag = rs.getString("tag")
            val os = rs.getString("owner")
            val dp = rs.getString("deputy")
            val home = rs.getString("home")
            val regionName = rs.getString("region")
            val membersString = rs.getString("members")
            val pvp = rs.getBoolean("pvp")
            var born = rs.getLong("born")
            var validity = rs.getLong("validity")
            val attacked = rs.getLong("attacked")
            val ban = rs.getLong("ban")
            var lives = rs.getInt("lives")
            if (name == null || tag == null || os == null) {
                FunnyGuilds.Companion.getPluginLogger().error("Cannot deserialize guild! Caused by: uuid/name/tag/owner is null")
                return null
            }
            var uuid = UUID.randomUUID()
            if (id != null && !id.isEmpty()) {
                uuid = UUID.fromString(id)
            }
            val owner: User = User.Companion.get(os)
            var deputies: Set<User?>? = HashSet()
            if (dp != null && !dp.isEmpty()) {
                deputies = UserUtils.getUsers(ChatUtils.fromString(dp))
            }
            var members: Set<User?>? = HashSet()
            if (membersString != null && membersString != "") {
                members = UserUtils.getUsers(ChatUtils.fromString(membersString))
            }
            if (born == 0L) {
                born = System.currentTimeMillis()
            }
            if (validity == 0L) {
                validity = System.currentTimeMillis() + FunnyGuilds.Companion.getInstance().getPluginConfiguration().validityStart
            }
            if (lives == 0) {
                lives = FunnyGuilds.Companion.getInstance().getPluginConfiguration().warLives
            }
            val values = arrayOfNulls<Any>(17)
            values[0] = uuid
            values[1] = name
            values[2] = tag
            values[3] = owner
            values[4] = LocationUtils.parseLocation(home)
            values[5] = RegionUtils.get(regionName)
            values[6] = members
            values[7] = Sets.newHashSet<Any>()
            values[8] = Sets.newHashSet<Any>()
            values[9] = born
            values[10] = validity
            values[11] = attacked
            values[12] = lives
            values[13] = ban
            values[14] = deputies
            values[15] = pvp
            return DeserializationUtils.deserializeGuild(values)
        } catch (ex: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not deserialize guild (id: $id, name: $name)", ex)
        }
        return null
    }

    fun save(guild: Guild?) {
        val members = ChatUtils.toString(UserUtils.getNames(guild!!.members), false)
        val deputies = ChatUtils.toString(UserUtils.getNames(guild.deputies), false)
        val allies = ChatUtils.toString(GuildUtils.getNames(guild.allies), false)
        val enemies = ChatUtils.toString(GuildUtils.getNames(guild.enemies), false)
        val statement = SQLBasicUtils.getInsert(SQLDataModel.Companion.tabGuilds)
        statement!!["uuid"] = guild.uuid.toString()
        statement["name"] = guild.name
        statement["tag"] = guild.tag
        statement["owner"] = guild.owner!!.name
        statement["home"] = LocationUtils.toString(guild.home)
        statement["region"] = RegionUtils.toString(guild.region)
        statement["regions"] = "#abandoned"
        statement["members"] = members
        statement["deputy"] = deputies
        statement["allies"] = allies
        statement["enemies"] = enemies
        statement["points"] = guild.rank.points
        statement["lives"] = guild.lives
        statement["born"] = guild.born
        statement["validity"] = guild.validity
        statement["attacked"] = guild.attacked
        statement["ban"] = guild.ban
        statement["pvp"] = guild.pvP
        statement["info"] = ""
        statement.executeUpdate()
    }

    fun delete(guild: Guild) {
        val statement = SQLBasicUtils.getDelete(SQLDataModel.Companion.tabGuilds)
        statement!!["uuid"] = guild.uuid.toString()
        statement.executeUpdate()
    }

    fun updatePoints(guild: Guild) {
        val table: SQLTable = SQLDataModel.Companion.tabGuilds
        val statement = SQLBasicUtils.getUpdate(table, table.getSQLElement("points"))
        statement!!["points"] = guild.rank.points
        statement["uuid"] = guild.uuid.toString()
        statement.executeUpdate()
    }
}