package net.dzikoysk.funnyguilds.command.user

import net.dzikoysk.funnycommands.resources.ValidationException
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.basic.user.UserUtils
import net.dzikoysk.funnyguilds.command.GuildValidation
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.util.IntegerRange
import net.dzikoysk.funnyguilds.util.commons.*
import org.apache.commons.lang3.StringUtils
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.panda_lang.utilities.commons.ObjectUtils
import org.panda_lang.utilities.commons.function.Option
import java.util.*
import java.util.function.Function

@FunnyComponent
class InfoCommand {
    @FunnyCommand(
        name = "\${user.info.name}",
        description = "\${user.info.description}",
        aliases = ["\${user.info.aliases}"],
        permission = "funnyguilds.info",
        completer = "guilds:3",
        acceptsExceeded = true
    )
    fun execute(config: PluginConfiguration, messages: MessageConfiguration, sender: CommandSender, args: Array<String?>) {
        val tag = Option.`when`(args.size > 0) { args[0] }
            .orElse {
                Option.`when`(sender is Player) {
                    ObjectUtils.cast(
                        Player::class.java, sender
                    )
                }
                    .map(Function<Player, User?> { player: Player? -> User.Companion.get(player) })
                    .filter { obj: User? -> obj!!.hasGuild() }
                    .map { obj: User? -> obj!!.guild }
                    .map { obj: Guild? -> obj.getTag() }
            }
            .orThrow { ValidationException(messages.infoTag) }
        val guild = GuildValidation.requireGuildByTag(tag)
        val validity = config.dateFormat!!.format(Date(guild.validity))
        val now = System.currentTimeMillis()
        val protectionEndTime = guild.protectionEndTime
        val additionalProtectionEndTime = guild.additionalProtectionEndTime
        for (messageLine in messages.infoList) {
            messageLine = StringUtils.replace(messageLine, "{GUILD}", guild.name)
            messageLine = StringUtils.replace(messageLine, "{TAG}", guild.tag)
            messageLine = StringUtils.replace(messageLine, "{OWNER}", guild.owner!!.name)
            messageLine = StringUtils.replace(messageLine, "{MEMBERS-ONLINE}", guild.onlineMembers.size.toString())
            messageLine = StringUtils.replace(messageLine, "{MEMBERS-ALL}", guild.members.size.toString())
            messageLine = StringUtils.replace(messageLine, "{MEMBERS}", ChatUtils.toString(UserUtils.getOnlineNames(guild.members), true))
            messageLine = StringUtils.replace(messageLine, "{DEPUTIES}", if (guild.deputies.isEmpty()) "Brak" else ChatUtils.toString(UserUtils.getNames(guild.deputies), true))
            messageLine = StringUtils.replace(messageLine, "{REGION-SIZE}", if (config.regionsEnabled) guild.region.size.toString() else messages.gRegionSizeNoValue)
            messageLine = StringUtils.replace(messageLine, "{GUILD-PROTECTION}", if (protectionEndTime < now) "Brak" else TimeUtils.getDurationBreakdown(protectionEndTime - now))
            messageLine =
                StringUtils.replace(messageLine, "{GUILD-ADDITIONAL-PROTECTION}", if (additionalProtectionEndTime < now) "Brak" else TimeUtils.getDurationBreakdown(additionalProtectionEndTime - now))
            val rank = guild.rank
            messageLine = StringUtils.replace(messageLine, "{POINTS-FORMAT}", IntegerRange.Companion.inRangeToString<String?>(rank!!.points, config.pointsFormat))
            messageLine = StringUtils.replace(messageLine, "{POINTS}", Integer.toString(rank.points))
            messageLine = StringUtils.replace(messageLine, "{KILLS}", Integer.toString(rank.kills))
            messageLine = StringUtils.replace(messageLine, "{DEATHS}", Integer.toString(rank.deaths))
            messageLine = StringUtils.replace(messageLine, "{KDR}", String.format(Locale.US, "%.2f", rank.kdr))
            messageLine = StringUtils.replace(messageLine, "{VALIDITY}", validity)
            messageLine = StringUtils.replace(messageLine, "{LIVES}", Integer.toString(guild.lives))
            messageLine = if (guild.members.size >= config.minMembersToInclude) {
                StringUtils.replace(messageLine, "{RANK}", rank.position.toString())
            } else {
                StringUtils.replace(messageLine, "{RANK}", messages.minMembersToIncludeNoValue)
            }
            if (!guild.allies!!.isEmpty()) {
                messageLine = StringUtils.replace(messageLine, "{ALLIES}", ChatUtils.toString(GuildUtils.getNames(guild.allies), true))
                messageLine = StringUtils.replace(messageLine, "{ALLIES-TAGS}", ChatUtils.toString(GuildUtils.getTags(guild.allies), true))
            } else {
                messageLine = StringUtils.replace(messageLine, "{ALLIES}", messages.alliesNoValue)
                messageLine = StringUtils.replace(messageLine, "{ALLIES-TAGS}", messages.alliesNoValue)
            }
            if (!guild.enemies!!.isEmpty()) {
                messageLine = StringUtils.replace(messageLine, "{ENEMIES}", ChatUtils.toString(GuildUtils.getNames(guild.enemies), true))
                messageLine = StringUtils.replace(messageLine, "{ENEMIES-TAGS}", ChatUtils.toString(GuildUtils.getTags(guild.enemies), true))
            } else {
                messageLine = StringUtils.replace(messageLine, "{ENEMIES}", messages.enemiesNoValue)
                messageLine = StringUtils.replace(messageLine, "{ENEMIES-TAGS}", messages.enemiesNoValue)
            }
            if (messageLine.contains("<online>")) {
                val color = ChatColor.getLastColors(messageLine.split("<online>".toRegex()).toTypedArray()[0])
                messageLine = StringUtils.replace(messageLine, "<online>", ChatColor.GREEN.toString() + "")
                messageLine = StringUtils.replace(messageLine, "</online>", color)
            }
            sender.sendMessage(messageLine)
        }
    }
}