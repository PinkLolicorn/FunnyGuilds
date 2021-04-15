package net.dzikoysk.funnyguilds.event

import org.bukkit.Bukkit

object SimpleEventHandler {
    fun handle(event: FunnyEvent): Boolean {
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            event.notifyDoer()
            return false
        }
        return true
    }
}