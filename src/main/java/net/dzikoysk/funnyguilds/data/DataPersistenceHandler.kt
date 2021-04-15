package net.dzikoysk.funnyguilds.data

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.concurrency.requests.DataSaveRequest
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask

class DataPersistenceHandler(private val funnyGuilds: FunnyGuilds) {
    @Volatile
    private var dataPersistenceHandlerTask: BukkitTask? = null
    fun startHandler() {
        val interval = funnyGuilds.pluginConfiguration.dataInterval * 60L * 20L
        if (dataPersistenceHandlerTask != null) {
            dataPersistenceHandlerTask!!.cancel()
        }
        dataPersistenceHandlerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
            funnyGuilds,
            Runnable { funnyGuilds.concurrencyManager.postRequests(DataSaveRequest(false)) }, interval, interval
        )
    }

    fun stopHandler() {
        if (dataPersistenceHandlerTask == null) {
            return
        }
        dataPersistenceHandlerTask!!.cancel()
        dataPersistenceHandlerTask = null
    }

    fun reloadHandler() {
        stopHandler()
        startHandler()
    }
}