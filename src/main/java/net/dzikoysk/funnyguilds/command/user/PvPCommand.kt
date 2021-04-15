package net.dzikoysk.funnyguilds.command.user

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.command.CanManage
import net.dzikoysk.funnyguilds.command.DefaultValidation
import net.dzikoysk.funnyguilds.command.GuildValidation
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import org.bukkit.entity.Player
import org.panda_lang.utilities.commons.text.Formatter

@FunnyComponent
class PvPCommand {
    @FunnyCommand(name = "\${user.pvp.name}", description = "\${user.pvp.description}", aliases = ["\${user.pvp.aliases}"], permission = "funnyguilds.pvp", acceptsExceeded = true, playerOnly = true)
    fun execute(config: PluginConfiguration, messages: MessageConfiguration, player: Player, @CanManage user: User?, guild: Guild, args: Array<String?>) {
        if (args.size > 0) {
            DefaultValidation.`when`(!config.damageAlly, messages.generalAllyPvpDisabled)
            val targetAlliedGuild = GuildValidation.requireGuildByTag(args[0])
            val guildTagFormatter = Formatter().register("{TAG}", targetAlliedGuild.tag)
            DefaultValidation.`when`(!guild.allies!!.contains(targetAlliedGuild), guildTagFormatter.format(messages.allyDoesntExist))
            guild.setPvP(targetAlliedGuild, !guild.getPvP(targetAlliedGuild))
            player.sendMessage(guildTagFormatter.format(if (guild.getPvP(targetAlliedGuild)) messages.pvpAllyOn else messages.pvpAllyOff))
            return
        }
        guild.pvP = !guild.pvP
        player.sendMessage(if (guild.pvP) messages.pvpOn else messages.pvpOff)
    }
}