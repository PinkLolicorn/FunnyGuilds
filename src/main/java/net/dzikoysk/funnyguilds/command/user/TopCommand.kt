package net.dzikoysk.funnyguilds.command.user

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent
import net.dzikoysk.funnyguilds.basic.rank.RankUtils
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@FunnyComponent
class TopCommand {
    @FunnyCommand(name = "\${user.top.name}", description = "\${user.top.description}", aliases = ["\${user.top.aliases}"], permission = "funnyguilds.top", acceptsExceeded = true)
    fun execute(messages: MessageConfiguration, sender: CommandSender) {
        val user: User? = if (sender is Player) User.Companion.get(sender) else null
        for (messageLine in messages.topList) {
            val parsedRank = RankUtils.parseRank(user, messageLine)
            sender.sendMessage((parsedRank ?: messageLine)!!)
        }
    }
}