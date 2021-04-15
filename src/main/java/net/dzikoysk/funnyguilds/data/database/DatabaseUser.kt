package net.dzikoysk.funnyguilds.data.database

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils
import net.dzikoysk.funnyguilds.data.database.element.SQLTable
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils
import java.sql.ResultSet

net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.element.gui.GuiActionHandler
import net.dzikoysk.funnyguilds.listener.EntityDamage
import net.dzikoysk.funnyguilds.listener.EntityInteract
import net.dzikoysk.funnyguilds.listener.PlayerChat
import net.dzikoysk.funnyguilds.listener.PlayerDeath
import net.dzikoysk.funnyguilds.listener.PlayerJoin
import net.dzikoysk.funnyguilds.listener.PlayerLogin
import net.dzikoysk.funnyguilds.listener.PlayerQuit
import net.dzikoysk.funnyguilds.listener.TntProtection
import net.dzikoysk.funnyguilds.listener.BlockFlow
import net.dzikoysk.funnyguilds.listener.region.EntityPlace
import net.dzikoysk.funnyguilds.listener.region.BlockBreak
import net.dzikoysk.funnyguilds.listener.region.BlockIgnite
import net.dzikoysk.funnyguilds.listener.region.BucketAction
import net.dzikoysk.funnyguilds.listener.region.EntityExplode
import net.dzikoysk.funnyguilds.listener.region.HangingBreak
import net.dzikoysk.funnyguilds.listener.region.HangingPlace
import net.dzikoysk.funnyguilds.listener.region.PlayerCommand
import net.dzikoysk.funnyguilds.listener.region.PlayerInteract
import net.dzikoysk.funnyguilds.listener.region.EntityProtect
import net.dzikoysk.funnyguilds.listener.region.PlayerMove
import net.dzikoysk.funnyguilds.listener.region.BlockPhysics
import net.dzikoysk.funnyguilds.listener.region.PlayerRespawnimport

java.lang.Exception
import java.lang.StackTraceElement

object DatabaseUser {
    fun deserialize(rs: ResultSet?): User? {
        if (rs == null) {
            return null
        }
        try {
            val uuid = rs.getString("uuid")
            val name = rs.getString("name")
            val points = rs.getInt("points")
            val kills = rs.getInt("kills")
            val deaths = rs.getInt("deaths")
            val ban = rs.getLong("ban")
            val reason = rs.getString("reason")
            val values = arrayOfNulls<Any>(7)
            values[0] = uuid
            values[1] = name
            values[2] = points
            values[3] = kills
            values[4] = deaths
            values[5] = ban
            values[6] = reason
            return DeserializationUtils.deserializeUser(values)
        } catch (ex: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not deserialize user", ex)
        }
        return null
    }

    fun save(user: User?) {
        val statement = SQLBasicUtils.getInsert(SQLDataModel.Companion.tabUsers)
        statement!!["uuid"] = user.getUUID().toString()
        statement["name"] = user!!.name
        statement["points"] = user.rank.points
        statement["kills"] = user.rank.kills
        statement["deaths"] = user.rank.deaths
        statement["ban"] = if (user.isBanned) user.ban.banTime else 0
        statement["reason"] = if (user.isBanned) user.ban.reason else null
        statement.executeUpdate()
    }

    fun updatePoints(user: User) {
        val table: SQLTable = SQLDataModel.Companion.tabUsers
        val statement = SQLBasicUtils.getUpdate(table, table.getSQLElement("points"))
        statement!!["points"] = user.rank.points
        statement["uuid"] = user.uuid.toString()
        statement.executeUpdate()
    }
}