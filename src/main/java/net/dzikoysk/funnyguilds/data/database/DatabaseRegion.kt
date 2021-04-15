package net.dzikoysk.funnyguilds.data.database

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils
import java.sql.ResultSet

net.dzikoysk.funnyguilds.basic.guild.*
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerManager
import net.dzikoysk.funnyguilds.data.DataPersistenceHandler
import net.dzikoysk.funnyguilds.data.InvitationPersistenceHandler
import net.dzikoysk.funnyguilds.util.nms.DescriptionChanger
import net.dzikoysk.funnyguilds.command.CommandsConfiguration
import net.dzikoysk.funnyguilds.util.metrics.MetricsCollector
import net.dzikoysk.funnyguilds.system.GuildValidationHandler
import net.dzikoysk.funnyguilds.element.tablist.TablistBroadcastHandler
import net.dzikoysk.funnyguilds.basic.rank.RankRecalculationTask
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

object DatabaseRegion {
    fun deserialize(rs: ResultSet?): Region? {
        if (rs == null) {
            return null
        }
        try {
            val name = rs.getString("name")
            val center = rs.getString("center")
            val size = rs.getInt("size")
            val enlarge = rs.getInt("enlarge")
            val location = LocationUtils.parseLocation(center)
            if (name == null) {
                FunnyGuilds.Companion.getPluginLogger().error("Cannot deserialize region! Caused by: name == null")
                return null
            } else if (location == null) {
                FunnyGuilds.Companion.getPluginLogger().error("Cannot deserialize region ($name) ! Caused by: loc == null")
                return null
            }
            val values = arrayOfNulls<Any>(4)
            values[0] = name
            values[1] = location
            values[2] = size
            values[3] = enlarge
            return DeserializationUtils.deserializeRegion(values)
        } catch (ex: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not deserialize region", ex)
        }
        return null
    }

    fun save(region: Region?) {
        val statement = SQLBasicUtils.getInsert(SQLDataModel.Companion.tabRegions)
        statement!!["name"] = region!!.name
        statement["center"] = LocationUtils.toString(region.center)
        statement["size"] = region.size
        statement["enlarge"] = region.enlarge
        statement.executeUpdate()
    }

    fun delete(region: Region?) {
        val statement = SQLBasicUtils.getDelete(SQLDataModel.Companion.tabRegions)
        statement!!["name"] = region!!.name
        statement.executeUpdate()
    }
}