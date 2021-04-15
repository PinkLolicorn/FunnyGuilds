package net.dzikoysk.funnyguilds.system.war

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent
import net.dzikoysk.funnyguilds.event.guild.GuildLivesChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class WarSystem {
    fun attack(player: Player, guild: Guild?) {
        val user: User = User.Companion.get(player)
        if (!user.hasGuild()) {
            WarUtils.message(player, 0)
            return
        }
        val attacker = user.guild
        if (attacker == guild) {
            return
        }
        if (attacker!!.allies!!.contains(guild)) {
            WarUtils.message(player, 1)
            return
        }
        if (!FunnyGuilds.Companion.getInstance().getPluginConfiguration().warEnabled) {
            WarUtils.message(player, 5)
            return
        }
        if (!guild!!.canBeAttacked()) {
            WarUtils.message(player, 2, guild.attacked + FunnyGuilds.Companion.getInstance().getPluginConfiguration().warWait - System.currentTimeMillis())
            return
        }
        guild.attacked = System.currentTimeMillis()
        if (SimpleEventHandler.handle(GuildLivesChangeEvent(EventCause.SYSTEM, user, guild, guild.lives - 1))) {
            guild.removeLive()
        }
        if (guild.lives < 1) {
            conquer(attacker, guild, user)
        } else {
            for (member in attacker.members) {
                val memberPlayer = member.player
                if (memberPlayer != null) {
                    WarUtils.message(memberPlayer, 3, guild)
                }
            }
            for (member in guild.members) {
                val memberPlayer = member.player
                if (memberPlayer != null) {
                    WarUtils.message(memberPlayer, 4, attacker)
                }
            }
        }
    }

    fun conquer(conqueror: Guild?, loser: Guild?, attacker: User?) {
        if (!SimpleEventHandler.handle(GuildDeleteEvent(EventCause.SYSTEM, attacker, loser))) {
            loser!!.addLive()
            return
        }
        var message = WarUtils.getWinMessage(conqueror, loser)
        for (user in conqueror!!.members) {
            val player = user.player
            player?.sendMessage(message!!)
        }
        message = WarUtils.getLoseMessage(conqueror, loser)
        for (user in loser!!.members) {
            val player = user.player
            player?.sendMessage(message)
        }
        GuildUtils.deleteGuild(loser)
        conqueror.addLive()
        message = WarUtils.getBroadcastMessage(conqueror, loser)
        Bukkit.broadcastMessage(message)
    }

    companion object {
        private var instance: WarSystem?
        fun getInstance(): WarSystem? {
            if (instance == null) {
                WarSystem()
            }
            return instance
        }
    }

    init {
        instance = this
    }
}