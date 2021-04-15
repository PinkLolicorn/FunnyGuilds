package net.dzikoysk.funnyguilds.event

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

org.bukkit.event.Cancellableimport org.bukkit.event.Event
import java.lang.StackTraceElement

abstract class FunnyEvent : Event, Cancellable {
    val eventCause: EventCause?
    val doer: User?
    var cancelMessage: String? = null
        get() = if (field == null || field!!.isEmpty()) {
            defaultCancelMessage
        } else field
    private var cancelled = false

    constructor(eventCause: EventCause?, doer: User?) {
        this.eventCause = eventCause
        this.doer = doer
    }

    constructor(eventCause: EventCause?, doer: User?, isAsync: Boolean) : super(isAsync) {
        this.eventCause = eventCause
        this.doer = doer
    }

    abstract val defaultCancelMessage: String
    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

    fun notifyDoer() {
        if (doer != null && doer.isOnline) {
            doer.player.sendMessage(cancelMessage)
        }
    }

    enum class EventCause {
        ADMIN, CONSOLE, SYSTEM, USER, UNKNOWN
    }
}