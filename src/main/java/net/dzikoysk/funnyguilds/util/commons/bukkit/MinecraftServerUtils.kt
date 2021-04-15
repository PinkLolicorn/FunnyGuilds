package net.dzikoysk.funnyguilds.util.commons.bukkit

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.util.nms.Reflections
import org.bukkit.Bukkit
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.text.DecimalFormat

object MinecraftServerUtils {
    private val FORMAT = DecimalFormat("##.##")
    private var serverInstance: Any? = null
    private var tpsField: Field? = null

    // 0 = last 1 min, 1 = last 5 min, 2 = last 15min
    fun getFormattedTPS(last: Int): String? {
        return try {
            if (tpsField != null) FORMAT.format(Math.min(20.0, (tpsField!![serverInstance] as DoubleArray)[last])) else "N/A"
        } catch (illegalAccessException: IllegalAccessException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not retrieve recent TPS", illegalAccessException)
            null
        }
    }

    fun getRecentTPS(last: Int): Double {
        return try {
            if (tpsField != null) Math.min(20.0, (tpsField!![serverInstance] as DoubleArray)[last]) else -1.0
        } catch (illegalAccessException: IllegalAccessException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not retrieve recent TPS", illegalAccessException)
            -1.0
        }
    }

    val reloadCount: Int
        get() {
            val server = Bukkit.getServer()
            return try {
                val reloadCountField = server.javaClass.getDeclaredField("reloadCount")
                reloadCountField.getInt(server)
            } catch (illegalAccessException: IllegalAccessException) {
                -1
            } catch (illegalAccessException: NoSuchFieldException) {
                -1
            }
        }

    init {
        try {
            val minecraftServerClass = Reflections.getNMSClass("MinecraftServer")
            serverInstance = Reflections.getMethod(Reflections.getNMSClass("MinecraftServer"), "getServer")!!.invoke(null)
            tpsField = net.dzikoysk.funnyguilds.util.commons.bukkit.minecraftServerClass.getDeclaredField("recentTps")
        } catch (ex: IllegalAccessException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not initialize MinecraftServerUtils", ex)
        } catch (ex: InvocationTargetException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not initialize MinecraftServerUtils", ex)
        } catch (noSuchFieldException: NoSuchFieldException) {
            tpsField = null
        }
    }
}