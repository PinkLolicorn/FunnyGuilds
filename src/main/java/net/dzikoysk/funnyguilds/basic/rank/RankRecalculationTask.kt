package net.dzikoysk.funnyguilds.basic.rank

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.basic.user.UserUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.PermissionUtils
import java.util.*

class RankRecalculationTask : Runnable {
    override fun run() {
        val manager: RankManager = RankManager.Companion.getInstance()
        recalculateUsersRank(manager)
        recalculateGuildsRank(manager)
    }

    private fun recalculateUsersRank(manager: RankManager) {
        val usersRank: NavigableSet<Rank?> = TreeSet(Collections.reverseOrder())
        for (user in UserUtils.getUsers()) {
            val userRank = user.rank
            if (FunnyGuilds.Companion.getInstance().getPluginConfiguration().skipPrivilegedPlayersInRankPositions &&
                PermissionUtils.isPrivileged(user, "funnyguilds.ranking.exempt")
            ) {
                continue
            }
            usersRank.add(userRank)
        }
        var position = 0
        for (userRank in usersRank) {
            userRank.setPosition(++position)
        }
        manager.usersRank = usersRank
    }

    private fun recalculateGuildsRank(manager: RankManager) {
        val guildsRank: NavigableSet<Rank?> = TreeSet(Collections.reverseOrder())
        for (guild in GuildUtils.getGuilds()) {
            val guildRank = guild.rank
            if (guild!!.members.size < FunnyGuilds.Companion.getInstance().getPluginConfiguration().minMembersToInclude) {
                continue
            }
            guildsRank.add(guildRank)
        }
        var position = 0
        for (guildRank in guildsRank) {
            guildRank.setPosition(++position)
        }
        manager.guildsRank = guildsRank
    }
}