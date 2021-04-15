package net.dzikoysk.funnyguilds.hook

import com.google.common.collect.Maps
import me.robin.leaderheads.datacollectors.DataCollector
import me.robin.leaderheads.objects.BoardType
import net.dzikoysk.funnyguilds.basic.rank.RankManager
import net.dzikoysk.funnyguilds.basic.user.User

object LeaderHeadsHook {
    fun initLeaderHeadsHook() {
        TopRankCollector()
    }

    class TopRankCollector : DataCollector(
        "funnyguilds-top-rank",
        "FunnyGuilds",
        BoardType.DEFAULT,
        "Top rankingu",
        "/toprank", emptyList(),
        true,
        String::class.java
    ) {
        override fun requestAll(): List<Entry<*, Double>> {
            val topUsers: MutableList<Entry<*, Double>> = ArrayList<Entry<*, Double>>()
            for (i in 1..10) {
                val user: User = RankManager.Companion.getInstance().getUser(i)
                topUsers.add(Maps.immutableEntry(user.name, user.rank.points as Double))
            }
            return topUsers
        }
    }
}