package net.dzikoysk.funnyguilds.command.user

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import org.bukkit.command.CommandSender
import java.util.function.Consumer

@FunnyComponent
class GuildCommand {
    @FunnyCommand(name = "\${user.guild.name}", description = "\${user.guild.description}", aliases = ["\${user.guild.aliases}"], permission = "funnyguilds.guild", acceptsExceeded = true)
    fun execute(messages: MessageConfiguration, sender: CommandSender) {
        messages.helpList.forEach(Consumer { s: String? -> sender.sendMessage(s!!) })
    }
}