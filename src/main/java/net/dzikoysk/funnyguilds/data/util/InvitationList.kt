package net.dzikoysk.funnyguilds.data.util

import com.google.common.collect.ImmutableList
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.basic.user.User
import org.apache.commons.lang3.builder.ToStringBuilder
import org.bukkit.entity.Player
import java.util.*
import java.util.stream.Collectors

object InvitationList {
    private val INVITATION_LIST: MutableSet<Invitation> = HashSet()
    fun createInvitation(from: Guild, to: Player?) {
        val invitation = Invitation(from, to)
        INVITATION_LIST.add(invitation)
    }

    fun createInvitation(from: Guild, to: Guild) {
        val invitation = Invitation(from, to)
        INVITATION_LIST.add(invitation)
    }

    fun createInvitation(from: Guild, player: UUID) {
        val invitation = Invitation(from, player)
        INVITATION_LIST.add(invitation)
    }

    fun expireInvitation(from: Guild, to: Player) {
        for (invitation in INVITATION_LIST) {
            if (invitation.isToGuild && invitation.from == from.uuid && invitation.`for` == to.uniqueId) {
                INVITATION_LIST.remove(invitation)
                break
            }
        }
    }

    fun expireInvitation(from: Guild, to: User) {
        for (invitation in INVITATION_LIST) {
            if (invitation.isToGuild && invitation.from == from.uuid && invitation.`for` == to.uuid) {
                INVITATION_LIST.remove(invitation)
                break
            }
        }
    }

    fun expireInvitation(from: Guild, to: Guild) {
        for (invitation in INVITATION_LIST) {
            if (invitation.isToAlly && invitation.from == from.uuid && invitation.`for` == to.uuid) {
                INVITATION_LIST.remove(invitation)
                break
            }
        }
    }

    fun hasInvitation(player: Player): Boolean {
        for (invitation in INVITATION_LIST) {
            if (invitation.isToGuild && invitation.`for` == player.uniqueId) {
                return true
            }
        }
        return false
    }

    fun hasInvitationFrom(player: Player, from: Guild?): Boolean {
        for (invitation in INVITATION_LIST) {
            if (invitation.isToGuild && invitation.from == from.getUUID() && invitation.`for` == player.uniqueId) {
                return true
            }
        }
        return false
    }

    fun hasInvitationFrom(player: User, from: Guild): Boolean {
        for (invitation in INVITATION_LIST) {
            if (invitation.isToGuild && invitation.from == from.uuid && invitation.`for` == player.uuid) {
                return true
            }
        }
        return false
    }

    fun hasInvitationFrom(guild: Guild, from: Guild): Boolean {
        for (invitation in INVITATION_LIST) {
            if (invitation.isToAlly && invitation.from == from.uuid && invitation.`for` == guild.uuid) {
                return true
            }
        }
        return false
    }

    fun hasInvitation(guild: Guild): Boolean {
        for (invitation in INVITATION_LIST) {
            if (invitation.isToAlly && invitation.`for` == guild.uuid) {
                return true
            }
        }
        return false
    }

    val invitations: List<Invitation>
        get() = ImmutableList.copyOf(INVITATION_LIST)

    fun getInvitationsFor(player: Player): List<Invitation> {
        return INVITATION_LIST
            .stream()
            .filter { inv: Invitation -> inv.isToGuild && inv.`for` == player.uniqueId }
            .collect(Collectors.toList())
    }

    fun getInvitationsFrom(guild: Guild?): List<Invitation> {
        return INVITATION_LIST
            .stream()
            .filter { inv: Invitation -> inv.from == guild.getUUID() }
            .collect(Collectors.toList())
    }

    fun getInvitationsFor(guild: Guild): List<Invitation> {
        return INVITATION_LIST
            .stream()
            .filter { inv: Invitation -> inv.isToAlly && inv.`for` == guild.uuid }
            .collect(Collectors.toList())
    }

    fun getInvitationGuildNames(player: Player): List<String?> {
        val guildNames: MutableList<String?> = ArrayList()
        for (invitation in INVITATION_LIST) {
            if (invitation.isToGuild && invitation.`for` == player.uniqueId) {
                guildNames.add(invitation.wrapFrom()!!.name)
            }
        }
        return guildNames
    }

    fun getInvitationGuildNames(guild: Guild): List<String?> {
        val guildNames: MutableList<String?> = ArrayList()
        for (invitation in INVITATION_LIST) {
            if (invitation.isToAlly && invitation.`for` == guild.uuid) {
                guildNames.add(invitation.wrapFrom()!!.name)
            }
        }
        return guildNames
    }

    class Invitation {
        val from: UUID?
        val `for`: UUID?
        private val type: InvitationType

        constructor(from: Guild, player: UUID) {
            this.from = from.uuid
            `for` = player
            type = InvitationType.TO_GUILD
        }

        constructor(from: Guild, to: Player?) {
            this.from = from.uuid
            `for` = to!!.uniqueId
            type = InvitationType.TO_GUILD
        }

        constructor(from: Guild, to: Guild) {
            this.from = from.uuid
            `for` = to.uuid
            type = InvitationType.TO_ALLY
        }

        val isToAlly: Boolean
            get() = type == InvitationType.TO_ALLY
        val isToGuild: Boolean
            get() = type == InvitationType.TO_GUILD

        fun wrapFrom(): Guild? {
            return GuildUtils.getByUUID(from)
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) {
                return true
            }
            if (o == null || javaClass != o.javaClass) {
                return false
            }
            val that = o as Invitation
            return from == that.from && `for` == that.`for` && type == that.type
        }

        override fun hashCode(): Int {
            return Objects.hash(from, `for`, type)
        }

        override fun toString(): String {
            return ToStringBuilder(this).append("from", from).append("to", `for`).append("type", type).toString()
        }
    }

    private enum class InvitationType {
        TO_GUILD, TO_ALLY
    }
}