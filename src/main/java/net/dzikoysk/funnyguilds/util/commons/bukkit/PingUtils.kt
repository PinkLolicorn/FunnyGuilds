package net.dzikoysk.funnyguilds.util.commons.bukkit

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.util.nms.Reflections
import org.bukkit.entity.Player
import java.lang.reflect.Field
import java.lang.reflect.Method

object PingUtils {
    private var craftPlayerClass: Class<*>? = null
    private var getHandleMethod: Method? = null
    private var pingField: Field? = null
    fun getPing(player: Player?): Int {
        var ping = 0
        if (player == null) {
            return ping
        }
        try {
            val cp = craftPlayerClass!!.cast(player)
            val handle = getHandleMethod!!.invoke(cp)
            ping = pingField!![handle] as Int
        } catch (ex: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not retrieve player's ping", ex)
        }
        return Math.max(0, ping)
    }

    init {
        craftPlayerClass = Reflections.getCraftBukkitClass("entity.CraftPlayer")
        getHandleMethod = Reflections.getMethod(craftPlayerClass, "getHandle")
        pingField = Reflections.getField(Reflections.getNMSClass("EntityPlayer"), "ping")
    }
}