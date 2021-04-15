package net.dzikoysk.funnyguilds.command.user

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent
import net.dzikoysk.funnyguilds.command.DefaultValidation
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import org.bukkit.command.CommandSender
import org.panda_lang.utilities.commons.StringUtils
import java.time.LocalTime

@FunnyComponent
class TntCommand {
    @FunnyCommand(name = "\${user.tnt.name}", description = "\${user.tnt.description}", aliases = ["\${user.tnt.aliases}"], permission = "funnyguilds.tnt", acceptsExceeded = true)
    fun execute(config: PluginConfiguration, messages: MessageConfiguration, sender: CommandSender) {
        DefaultValidation.`when`(!config.guildTNTProtectionEnabled, messages.tntProtectDisable)
        val now = LocalTime.now()
        val start = config.guildTNTProtectionStartTime
        val end = config.guildTNTProtectionEndTime
        var message = messages.tntInfo
        val isWithinTimeframe = if (config.guildTNTProtectionPassingMidnight) now.isAfter(start) || now.isBefore(end) else now.isAfter(start) && now.isBefore(end)
        message = StringUtils.replace(message, "{PROTECTION_START}", config.guildTNTProtectionStartTime_)
        message = StringUtils.replace(message, "{PROTECTION_END}", config.guildTNTProtectionEndTime_)
        sender.sendMessage(message)
        sender.sendMessage(if (isWithinTimeframe) messages.tntNowDisabled else messages.tntNowEnabled)
    }
}