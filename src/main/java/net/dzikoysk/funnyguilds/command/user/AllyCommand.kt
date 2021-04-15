package net.dzikoysk.funnyguilds.command.user

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.command.DefaultValidation
import net.dzikoysk.funnyguilds.command.GuildValidation
import net.dzikoysk.funnyguilds.command.IsOwner
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixUpdateGuildRequest
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.data.util.InvitationList
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.ally.GuildAcceptAllyInvitationEvent
import net.dzikoysk.funnyguilds.event.guild.ally.GuildRevokeAllyInvitationEvent
import net.dzikoysk.funnyguilds.event.guild.ally.GuildSendAllyInvitationEvent
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import org.apache.commons.lang3.StringUtils
import org.bukkit.entity.Player
import org.panda_lang.utilities.commons.text.Formatter

@FunnyComponent
class AllyCommand {
    @FunnyCommand(
        name = "\${user.ally.name}",
        description = "\${user.ally.description}",
        aliases = ["\${user.ally.aliases}"],
        permission = "funnyguilds.ally",
        completer = "guilds:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    fun execute(config: PluginConfiguration, messages: MessageConfiguration, player: Player, @IsOwner user: User?, guild: Guild, args: Array<String?>) {
        val invitations = InvitationList.getInvitationsFor(guild)
        if (args.size < 1) {
            DefaultValidation.`when`(invitations!!.size == 0, messages.allyHasNotInvitation)
            val guildNames = ChatUtils.toString(InvitationList.getInvitationGuildNames(guild), false)
            for (msg in messages.allyInvitationList) {
                player.sendMessage(msg!!.replace("{GUILDS}", guildNames!!))
            }
            return
        }
        val invitedGuild = GuildValidation.requireGuildByTag(args[0])
        val invitedOwner = invitedGuild.owner.player
        DefaultValidation.`when`(guild == invitedGuild, messages.allySame)
        DefaultValidation.`when`(guild.allies!!.contains(invitedGuild), messages.allyAlly)
        if (guild.enemies!!.contains(invitedGuild)) {
            guild.removeEnemy(invitedGuild)
            var allyDoneMessage = messages.enemyEnd
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{GUILD}", invitedGuild.name)
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{TAG}", invitedGuild.tag)
            player.sendMessage(allyDoneMessage)
            if (invitedOwner != null) {
                var allyIDoneMessage = messages.enemyIEnd
                allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{GUILD}", guild.name)
                allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{TAG}", guild.tag)
                invitedOwner.sendMessage(allyIDoneMessage)
            }
        }
        DefaultValidation.`when`(guild.allies!!.size >= config.maxAlliesBetweenGuilds) { messages.inviteAllyAmount.replace("{AMOUNT}", Integer.toString(config.maxAlliesBetweenGuilds)) }
        if (invitedGuild.allies!!.size >= config.maxAlliesBetweenGuilds) {
            val formatter = Formatter()
                .register("{GUILD}", invitedGuild.name)
                .register("{TAG}", invitedGuild.tag)
                .register("{AMOUNT}", config.maxAlliesBetweenGuilds)
            player.sendMessage(formatter.format(messages.inviteAllyTargetAmount))
            return
        }
        if (InvitationList.hasInvitationFrom(guild, invitedGuild)) {
            if (!SimpleEventHandler.handle(GuildAcceptAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
                return
            }
            InvitationList.expireInvitation(invitedGuild, guild)
            guild.addAlly(invitedGuild)
            invitedGuild.addAlly(guild)
            var allyDoneMessage = messages.allyDone
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{GUILD}", invitedGuild.name)
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{TAG}", invitedGuild.tag)
            player.sendMessage(allyDoneMessage)
            if (invitedOwner != null) {
                var allyIDoneMessage = messages.allyIDone
                allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{GUILD}", guild.name)
                allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{TAG}", guild.tag)
                invitedOwner.sendMessage(allyIDoneMessage)
            }
            val taskBuilder: ConcurrencyTaskBuilder = ConcurrencyTask.Companion.builder()
            for (member in guild.members) {
                taskBuilder.delegate(PrefixUpdateGuildRequest(member, invitedGuild))
            }
            for (member in invitedGuild.members) {
                taskBuilder.delegate(PrefixUpdateGuildRequest(member, guild))
            }
            FunnyGuilds.Companion.getInstance().getConcurrencyManager().postTask(taskBuilder.build())
            return
        }
        if (InvitationList.hasInvitationFrom(invitedGuild, guild)) {
            if (!SimpleEventHandler.handle(GuildRevokeAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
                return
            }
            InvitationList.expireInvitation(guild, invitedGuild)
            var allyReturnMessage = messages.allyReturn
            allyReturnMessage = StringUtils.replace(allyReturnMessage, "{GUILD}", invitedGuild.name)
            allyReturnMessage = StringUtils.replace(allyReturnMessage, "{TAG}", invitedGuild.tag)
            player.sendMessage(allyReturnMessage)
            if (invitedOwner != null) {
                var allyIReturnMessage = messages.allyIReturn
                allyIReturnMessage = StringUtils.replace(allyIReturnMessage, "{GUILD}", guild.name)
                allyIReturnMessage = StringUtils.replace(allyIReturnMessage, "{TAG}", guild.tag)
                invitedOwner.sendMessage(allyIReturnMessage)
            }
            return
        }
        if (!SimpleEventHandler.handle(GuildSendAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
            return
        }
        InvitationList.createInvitation(guild, invitedGuild)
        var allyInviteDoneMessage = messages.allyInviteDone
        allyInviteDoneMessage = StringUtils.replace(allyInviteDoneMessage, "{GUILD}", invitedGuild.name)
        allyInviteDoneMessage = StringUtils.replace(allyInviteDoneMessage, "{TAG}", invitedGuild.tag)
        player.sendMessage(allyInviteDoneMessage)
        if (invitedOwner != null) {
            var allyToInvitedMessage = messages.allyToInvited
            allyToInvitedMessage = StringUtils.replace(allyToInvitedMessage, "{GUILD}", guild.name)
            allyToInvitedMessage = StringUtils.replace(allyToInvitedMessage, "{TAG}", guild.tag)
            invitedOwner.sendMessage(allyToInvitedMessage)
        }
    }
}