package net.dzikoysk.funnyguilds.basic.rank

import com.google.common.collect.Iterables
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.util.commons.bukkit.PermissionUtils

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
import net.dzikoysk.funnyguilds.listener.region.PlayerRespawn
import java.lang.StackTraceElementimport

java.util.*
class RankManager private constructor() {
    var usersRank: NavigableSet<Rank?> = TreeSet(Collections.reverseOrder())
    var guildsRank: NavigableSet<Rank?> = TreeSet(Collections.reverseOrder())
    fun update(user: User) {
        if (user.uuid.version() == 2) {
            return
        }
        if (FunnyGuilds.Companion.getInstance().getPluginConfiguration().skipPrivilegedPlayersInRankPositions &&
            PermissionUtils.isPrivileged(user, "funnyguilds.ranking.exempt")
        ) {
            return
        }
        usersRank.add(user.rank)
    }

    fun update(guild: Guild) {
        if (guild.members.size < FunnyGuilds.Companion.getInstance().getPluginConfiguration().minMembersToInclude) {
            return
        }
        guildsRank.add(guild.rank)
    }

    fun getUser(i: Int): User? {
        return if (i - 1 < usersRank.size) {
            Iterables.get(usersRank, i - 1).user
        } else null
    }

    fun getGuild(i: Int): Guild? {
        return if (i - 1 < guildsRank.size) {
            Iterables.get(guildsRank, i - 1).guild
        } else null
    }

    fun users(): Int {
        return usersRank.size
    }

    fun guilds(): Int {
        return guildsRank.size
    }

    companion object {
        val instance = RankManager()
    }
}