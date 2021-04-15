package net.dzikoysk.funnyguilds.event.guild

import net.dzikoysk.funnyguilds.event.FunnyEvent

org.bukkit.block.Blockimport org.bukkit.event.HandlerList
import java.lang.StackTraceElement

class GuildEntityExplodeEvent(cause: EventCause?, val affectedBlocks: List<Block?>) : FunnyEvent(cause, null) {
    override val defaultCancelMessage: String
        get() = "[FunnyGuilds] Region entity explode has been cancelled by the server!"

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }
}