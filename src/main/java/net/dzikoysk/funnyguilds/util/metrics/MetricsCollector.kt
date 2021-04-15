package net.dzikoysk.funnyguilds.util.metrics

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.basic.user.UserUtils

class MetricsCollector(private val plugin: FunnyGuilds) : Runnable {
    private var mcstats: MCStats? = null
    private var bstats: BStats? = null
    fun start() {
        plugin.server.scheduler.runTaskLaterAsynchronously(plugin, this, 20L)
    }

    override fun run() {
        // mcstats
        val mcstats = mcstats
        if (mcstats != null) {
            val global = mcstats.createGraph("Guilds and Users")
            global!!.addPlotter(object : MCStats.Plotter("Guilds") {
                override val value: Int
                    get() = GuildUtils.getGuilds().size
            })
            global.addPlotter(object : MCStats.Plotter("Users") {
                override val value: Int
                    get() = UserUtils.usersSize()
            })
            mcstats.start()
        }

        // bstats
        val bstats = bstats
        bstats?.addCustomChart(object : BStats.MultiLineChart("Guilds and Users") {
            override fun getValues(hashMap: HashMap<String?, Int>): HashMap<String?, Int>? {
                hashMap["Guilds"] = GuildUtils.getGuilds().size
                hashMap["Users"] = UserUtils.usersSize()
                return hashMap
            }
        })
    }

    init {
        try {
            mcstats = MCStats(plugin)
        } catch (ex: Exception) {
            mcstats = null
            FunnyGuilds.Companion.getPluginLogger().error("Could not initialize mcstats", ex)
        }
        try {
            bstats = BStats(plugin)
        } catch (ex: Exception) {
            bstats = null
            FunnyGuilds.Companion.getPluginLogger().error("Could not initialize bstats", ex)
        }
    }
}