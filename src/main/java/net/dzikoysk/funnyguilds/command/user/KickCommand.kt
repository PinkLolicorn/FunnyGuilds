package net.dzikoysk.funnyguilds.command.user

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.command.CanManage
import net.dzikoysk.funnyguilds.command.DefaultValidation
import net.dzikoysk.funnyguilds.command.UserValidation
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemovePlayerRequest
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.panda_lang.utilities.commons.text.Formatter

@FunnyComponent
class KickCommand {
    @FunnyCommand(
        name = "\${user.kick.name}",
        description = "\${user.kick.description}",
        aliases = ["\${user.kick.aliases}"],
        permission = "funnyguilds.kick",
        completer = "members:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    fun execute(messages: MessageConfiguration, player: Player, @CanManage user: User?, guild: Guild, args: Array<String?>) {
        DefaultValidation.`when`(args.size < 1, messages.generalNoNickGiven)
        val formerUser = UserValidation.requireUserByName(args[0])
        DefaultValidation.`when`(!formerUser.hasGuild(), messages.generalPlayerHasNoGuild)
        DefaultValidation.`when`(guild != formerUser.guild, messages.kickOtherGuild)
        DefaultValidation.`when`(formerUser.isOwner, messages.kickOwner)
        if (!SimpleEventHandler.handle(GuildMemberKickEvent(EventCause.USER, user, guild, formerUser))) {
            return
        }
        val concurrencyManager: ConcurrencyManager = FunnyGuilds.Companion.getInstance().getConcurrencyManager()
        concurrencyManager.postRequests(PrefixGlobalRemovePlayerRequest(formerUser.name))
        guild.removeMember(formerUser)
        formerUser.removeGuild()
        if (formerUser.isOnline) {
            concurrencyManager.postRequests(PrefixGlobalUpdatePlayer(player))
        }
        val formatter = Formatter()
            .register("{PLAYER}", formerUser.name)
            .register("{GUILD}", guild.name)
            .register("{TAG}", guild.tag)
        player.sendMessage(formatter.format(messages.kickToOwner))
        Bukkit.broadcastMessage(formatter.format(messages.broadcastKick))
        val formerPlayer = formerUser.player
        formerPlayer?.sendMessage(formatter.format(messages.kickToPlayer))
    }
}