package net.dzikoysk.funnyguilds.command.user

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.command.DefaultValidation
import net.dzikoysk.funnyguilds.command.GuildValidation
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.data.util.InvitationList
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberAcceptInviteEvent
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.panda_lang.utilities.commons.text.Formatter

@FunnyComponent
class JoinCommand {
    @FunnyCommand(
        name = "\${user.join.name}",
        description = "\${user.join.description}",
        aliases = ["\${user.join.aliases}"],
        permission = "funnyguilds.join",
        completer = "guilds:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    fun execute(config: PluginConfiguration, messages: MessageConfiguration, player: Player, user: User, args: Array<String?>) {
        DefaultValidation.`when`(user.hasGuild(), messages.joinHasGuild)
        val invitations = InvitationList.getInvitationsFor(player)
        DefaultValidation.`when`(invitations!!.size == 0, messages.joinHasNotInvitation)
        if (args.size < 1) {
            val guildNames = ChatUtils.toString(InvitationList.getInvitationGuildNames(player), false)
            for (msg in messages.joinInvitationList) {
                player.sendMessage(msg!!.replace("{GUILDS}", guildNames!!))
            }
            return
        }
        val guild = GuildValidation.requireGuildByTag(args[0])
        DefaultValidation.`when`(!InvitationList.hasInvitationFrom(player, GuildUtils.getByTag(guild.tag)), messages.joinHasNotInvitationTo)
        val requiredItems = config.joinItems
        if (!ItemUtils.playerHasEnoughItems(player, requiredItems)) {
            return
        }
        if (!SimpleEventHandler.handle(GuildMemberAcceptInviteEvent(EventCause.USER, user, guild, user))) {
            return
        }
        InvitationList.expireInvitation(guild, player)
        if (!SimpleEventHandler.handle(GuildMemberJoinEvent(EventCause.USER, user, guild, user))) {
            return
        }
        guild.addMember(user)
        user.guild = guild
        player.inventory.removeItem(*ItemUtils.toArray(requiredItems))
        val concurrencyManager: ConcurrencyManager = FunnyGuilds.Companion.getInstance().getConcurrencyManager()
        concurrencyManager.postRequests(PrefixGlobalAddPlayerRequest(user.name))
        val formatter = Formatter()
            .register("{GUILD}", guild.name)
            .register("{TAG}", guild.tag)
            .register("{PLAYER}", player.name)
        player.sendMessage(formatter.format(messages.joinToMember))
        Bukkit.broadcastMessage(formatter.format(messages.broadcastJoin))
        val owner = guild.owner.player
        owner?.sendMessage(formatter.format(messages.joinToOwner))
    }
}