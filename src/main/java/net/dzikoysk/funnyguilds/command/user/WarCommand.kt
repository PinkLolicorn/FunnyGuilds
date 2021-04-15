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
import org.apache.commons.lang3.StringUtils
import org.bukkit.entity.Player
import org.panda_lang.utilities.commons.text.Formatter

@FunnyComponent
class WarCommand {
    @FunnyCommand(
        name = "\${user.war.name}",
        description = "\${user.war.description}",
        aliases = ["\${user.war.aliases}"],
        permission = "funnyguilds.war",
        completer = "guilds:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    fun execute(config: PluginConfiguration, messages: MessageConfiguration, player: Player, @IsOwner user: User?, guild: Guild, args: Array<String?>) {
        DefaultValidation.`when`(args.size < 1, messages.enemyCorrectUse)
        val enemyGuild = GuildValidation.requireGuildByTag(args[0])
        DefaultValidation.`when`(guild == enemyGuild, messages.enemySame)
        DefaultValidation.`when`(guild.allies!!.contains(enemyGuild), messages.enemyAlly)
        DefaultValidation.`when`(guild.enemies!!.contains(enemyGuild), messages.enemyAlready)
        DefaultValidation.`when`(guild.enemies!!.size >= config.maxEnemiesBetweenGuilds) { messages.enemyMaxAmount.replace("{AMOUNT}", Integer.toString(config.maxEnemiesBetweenGuilds)) }
        if (enemyGuild.enemies!!.size >= config.maxEnemiesBetweenGuilds) {
            val formatter = Formatter()
                .register("{GUILD}", enemyGuild.name)
                .register("{TAG}", enemyGuild.tag)
                .register("{AMOUNT}", config.maxEnemiesBetweenGuilds)
            player.sendMessage(formatter.format(messages.enemyMaxTargetAmount))
            return
        }
        val enemyOwner = enemyGuild.owner.player
        guild.addEnemy(enemyGuild)
        var allyDoneMessage = messages.enemyDone
        allyDoneMessage = StringUtils.replace(allyDoneMessage, "{GUILD}", enemyGuild.name)
        allyDoneMessage = StringUtils.replace(allyDoneMessage, "{TAG}", enemyGuild.tag)
        player.sendMessage(allyDoneMessage)
        if (enemyOwner != null) {
            var allyIDoneMessage = messages.enemyIDone
            allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{GUILD}", guild.name)
            allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{TAG}", guild.tag)
            enemyOwner.sendMessage(allyIDoneMessage)
        }
        val taskBuilder: ConcurrencyTaskBuilder = ConcurrencyTask.Companion.builder()
        for (member in guild.members) {
            taskBuilder.delegate(PrefixUpdateGuildRequest(member, enemyGuild))
        }
        for (member in enemyGuild.members) {
            taskBuilder.delegate(PrefixUpdateGuildRequest(member, guild))
        }
        FunnyGuilds.Companion.getInstance().getConcurrencyManager().postTask(taskBuilder.build())
    }
}