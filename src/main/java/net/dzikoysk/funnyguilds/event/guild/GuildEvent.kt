package net.dzikoysk.funnyguilds.event.guild

import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.event.FunnyEvent
import org.bukkit.Bukkit

abstract class GuildEvent : FunnyEvent {
    val guild: Guild?

    constructor(eventCause: EventCause?, doer: User?, guild: Guild?) : super(eventCause, doer, !Bukkit.isPrimaryThread()) {
        this.guild = guild
    }

    constructor(eventCause: EventCause?, doer: User?, guild: Guild?, isAsync: Boolean) : super(eventCause, doer, isAsync) {
        this.guild = guild
    }
}