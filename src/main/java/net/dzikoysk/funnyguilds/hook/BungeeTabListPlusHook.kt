package net.dzikoysk.funnyguilds.hook

import codecrafter47.bungeetablistplus.api.bukkit.BungeeTabListPlusBukkitAPI
import codecrafter47.bungeetablistplus.api.bukkit.Variable
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.rank.RankUtils
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.element.tablist.variable.DefaultTablistVariables
import org.bukkit.entity.Player

object BungeeTabListPlusHook {
    fun initVariableHook() {
        val plugin: FunnyGuilds = FunnyGuilds.Companion.getInstance()
        for ((key, value) in DefaultTablistVariables.getFunnyVariables()) {
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, object : Variable("funnyguilds_$key") {
                override fun getReplacement(player: Player): String {
                    val user: User = User.Companion.get(player) ?: return ""
                    return value[user]
                }
            })
        }

        // Guild TOP, positions 1-100
        for (i in 1..100) {
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, object : Variable("funnyguilds_gtop-$i") {
                override fun getReplacement(player: Player): String {
                    val user: User = User.Companion.get(player)
                    return RankUtils.parseRank(user, "{GTOP-$i}")!!
                }
            })
        }

        // User TOP, positions 1-100
        for (i in 1..100) {
            BungeeTabListPlusBukkitAPI.registerVariable(plugin, object : Variable("funnyguilds_ptop-$i") {
                override fun getReplacement(player: Player): String {
                    return RankUtils.parseRank(null, "{PTOP-$i}")!!
                }
            })
        }
        FunnyGuilds.Companion.getPluginLogger().info("BungeeTabListPlus hook has been enabled!")
    }
}