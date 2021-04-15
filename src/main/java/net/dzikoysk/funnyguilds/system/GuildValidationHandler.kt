package net.dzikoysk.funnyguilds.system

import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent
import net.dzikoysk.funnyguilds.system.validity.ValidityUtils

class GuildValidationHandler : Runnable {
    private var banGuildsCounter = 0
    private var validateGuildsCounter = 0
    override fun run() {
        if (++validateGuildsCounter >= 10) {
            validateGuildLifetime()
        }
        if (++banGuildsCounter >= 7) {
            validateGuildBans()
        }
    }

    private fun validateGuildLifetime() {
        for (guild in GuildUtils.getGuilds()) {
            if (guild!!.isValid) {
                continue
            }
            if (!SimpleEventHandler.handle(GuildDeleteEvent(EventCause.SYSTEM, null, guild))) {
                continue
            }
            ValidityUtils.broadcast(guild)
            GuildUtils.deleteGuild(guild)
        }
        validateGuildsCounter = 0
    }

    private fun validateGuildBans() {
        for (guild in GuildUtils.getGuilds()) {
            if (guild!!.ban > System.currentTimeMillis()) {
                continue
            }
            guild.ban = 0
            guild.markChanged()
        }
        banGuildsCounter = 0
    }
}