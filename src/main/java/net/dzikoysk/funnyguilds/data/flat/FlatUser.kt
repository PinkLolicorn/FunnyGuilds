package net.dzikoysk.funnyguilds.data.flat

import net.dzikoysk.funnyguilds.data.util.DeserializationUtils
import net.dzikoysk.funnyguilds.util.YamlWrapper

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

java.io.*
import java.lang.StackTraceElement

class FlatUser(private val user: User?) {
    fun serialize(flatDataModel: FlatDataModel): Boolean {
        val file = flatDataModel.getUserFile(user)
        if (file!!.isDirectory) {
            return false
        }
        val wrapper = YamlWrapper(file)
        wrapper["uuid"] = user.getUUID().toString()
        wrapper["name"] = user!!.name
        wrapper["points"] = user.rank.points
        wrapper["kills"] = user.rank.kills
        wrapper["deaths"] = user.rank.deaths
        if (user.isBanned) {
            wrapper["ban"] = user.ban.banTime
            wrapper["reason"] = user.ban.reason
        }
        wrapper.save()
        return true
    }

    companion object {
        fun deserialize(file: File): User? {
            if (file.isDirectory) {
                return null
            }
            val wrapper = YamlWrapper(file)
            val id = wrapper.getString("uuid")
            val name = wrapper.getString("name")
            val points = wrapper.getInt("points")
            val kills = wrapper.getInt("kills")
            val deaths = wrapper.getInt("deaths")
            val ban = wrapper.getLong("ban")
            val reason = wrapper.getString("reason")!!
            if (id == null || name == null) {
                return null
            }
            val values = arrayOfNulls<Any>(7)
            values[0] = id
            values[1] = name
            values[2] = points
            values[3] = kills
            values[4] = deaths
            values[5] = ban
            values[6] = reason
            return DeserializationUtils.deserializeUser(values)
        }
    }
}