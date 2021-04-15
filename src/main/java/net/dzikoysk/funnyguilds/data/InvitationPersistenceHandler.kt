package net.dzikoysk.funnyguilds.data

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.data.util.InvitationList
import net.dzikoysk.funnyguilds.util.YamlWrapper
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.io.File
import java.util.*

class InvitationPersistenceHandler(private val funnyGuilds: FunnyGuilds) {
    private val invitationsFile: File

    @Volatile
    private var invitationPersistenceHandlerTask: BukkitTask? = null
    fun startHandler() {
        val interval = funnyGuilds.pluginConfiguration.dataInterval * 60L * 20L
        if (invitationPersistenceHandlerTask != null) {
            invitationPersistenceHandlerTask!!.cancel()
        }
        invitationPersistenceHandlerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(funnyGuilds, Runnable { saveInvitations() }, interval, interval)
    }

    fun stopHandler() {
        if (invitationPersistenceHandlerTask == null) {
            return
        }
        invitationPersistenceHandlerTask!!.cancel()
        invitationPersistenceHandlerTask = null
    }

    fun saveInvitations() {
        invitationsFile.delete()
        val wrapper = YamlWrapper(invitationsFile)
        for (guild in GuildUtils.getGuilds()) {
            val invitationList = InvitationList.getInvitationsFrom(guild)
            for (invitation in invitationList!!) {
                val allyInvitations: MutableList<String> = ArrayList()
                val playerInvitations: MutableList<String> = ArrayList()
                if (invitation!!.isToGuild) {
                    playerInvitations.add(invitation.getFor().toString())
                } else if (invitation.isToAlly) {
                    allyInvitations.add(invitation.getFor().toString())
                }
                wrapper[invitation.from.toString() + ".guilds"] = allyInvitations
                wrapper[invitation.from.toString() + ".players"] = playerInvitations
            }
        }
        wrapper.save()
    }

    fun loadInvitations() {
        if (!invitationsFile.exists()) {
            return
        }
        val pc = YamlWrapper(invitationsFile)
        for (key in pc.getKeys(false)) {
            val guild = GuildUtils.getByUUID(UUID.fromString(key))
            if (guild != null) {
                val allyInvitations = pc.getStringList("$key.guilds")
                val playerInvitations = pc.getStringList("$key.players")
                for (ally in allyInvitations) {
                    val allyGuild = GuildUtils.getByUUID(UUID.fromString(ally))
                    if (allyGuild != null) {
                        InvitationList.createInvitation(guild, allyGuild)
                    }
                }
                for (player in playerInvitations) {
                    InvitationList.createInvitation(guild, UUID.fromString(player))
                }
            }
        }
    }

    init {
        invitationsFile = File(funnyGuilds.pluginDataFolder, "invitations.yml")
    }
}