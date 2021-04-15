package net.dzikoysk.funnyguilds.hook

import be.maximvdw.placeholderapi.PlaceholderAPI
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.rank.RankUtils
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.element.tablist.variable.DefaultTablistVariables
import org.bukkit.entity.Player
import org.panda_lang.utilities.commons.StringUtils

object MVdWPlaceholderAPIHook {
    fun initPlaceholderHook() {
        val plugin: FunnyGuilds = FunnyGuilds.Companion.getInstance()
        for ((key, value) in DefaultTablistVariables.getFunnyVariables()) {
            PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_$key") { event: PlaceholderReplaceEvent ->
                val target = event.offlinePlayer ?: return@registerPlaceholder StringUtils.EMPTY
                val user: User = User.Companion.get(target.uniqueId)
                value[user]
            }
        }

        // Guild TOP, positions 1-100
        for (i in 1..100) {
            PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_gtop-$i") { event: PlaceholderReplaceEvent ->
                val user: User = User.Companion.get(event.player)
                RankUtils.parseRank(user, "{GTOP-$i}")
            }
        }

        // User TOP, positions 1-100
        for (i in 1..100) {
            PlaceholderAPI.registerPlaceholder(plugin, "funnyguilds_ptop-$i") { event: PlaceholderReplaceEvent? -> RankUtils.parseRank(null, "{PTOP-$i}") }
        }
        FunnyGuilds.Companion.getPluginLogger().info("MVdWPlaceholderAPI hook has been enabled!")
    }

    fun replacePlaceholders(user: Player?, base: String?): String {
        return PlaceholderAPI.replacePlaceholders(user, base)
    }
}