package net.dzikoysk.funnyguilds.hook

import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.rank.RankUtils
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.element.tablist.variable.DefaultTablistVariables
import org.bukkit.entity.Player
import java.util.regex.Pattern

object PlaceholderAPIHook {
    private val PLACEHOLDER_PATTERN = Pattern.compile("[%]([^%]+)[%]")
    private val FUNNYGUILDS_VERSION: String = FunnyGuilds.Companion.getInstance().getDescription().getVersion()
    fun initPlaceholderHook() {
        FunnyGuildsPlaceholder().register()
        FunnyGuilds.Companion.getPluginLogger().info("PlaceholderAPI hook has been enabled!")
    }

    fun replacePlaceholders(user: Player?, base: String?): String {
        return PlaceholderAPI.setPlaceholders(user, base, PLACEHOLDER_PATTERN)
    }

    private class FunnyGuildsPlaceholder : PlaceholderExpansion() {
        override fun onPlaceholderRequest(player: Player, identifier: String): String {
            if (player == null) {
                return ""
            }
            val user: User = User.Companion.get(player) ?: return ""
            val variable = DefaultTablistVariables.getFunnyVariables()[identifier.toLowerCase()]
            return if (variable != null) {
                variable[user]
            } else RankUtils.parseRank(user, "{" + identifier.toUpperCase() + "}")!!
        }

        override fun getAuthor(): String {
            return "FunnyGuilds Team"
        }

        override fun getIdentifier(): String {
            return "funnyguilds"
        }

        override fun getPlugin(): String {
            return "FunnyGuilds"
        }

        override fun getVersion(): String {
            return FUNNYGUILDS_VERSION
        }

        override fun persist(): Boolean {
            return true
        }
    }
}