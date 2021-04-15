package net.dzikoysk.funnyguilds.command.user

import net.dzikoysk.funnycommands.stereotypes.FunnyComponent
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.command.DefaultValidation
import net.dzikoysk.funnyguilds.command.GuildValidation
import net.dzikoysk.funnyguilds.command.IsOwner
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixUpdateGuildRequest
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.ally.GuildBreakAllyEvent
import net.dzikoysk.funnyguilds.util.commons.ChatUtils

net.dzikoysk.funnycommands.stereotypes.FunnyCommand
import net.dzikoysk.funnyguilds.command.GuildsCompleter
import net.dzikoysk.funnyguilds.command.MembersCompleter
import net.dzikoysk.funnyguilds.command.FunnyGuildsExceptionHandler
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist
import java.util.function.BiFunction
import java.time.LocalDateTime
import net.dzikoysk.funnyguilds.element.tablist.variable.impl.GuildDependentTablistVariable
import net.dzikoysk.funnyguilds.element.tablist.variable.VariableParsingResult
import net.dzikoysk.funnyguilds.element.tablist.variable.impl.TimeFormattedVariable
import net.dzikoysk.funnyguilds.element.tablist.variable.TablistVariablesParser
import java.time.format.TextStyle
import net.dzikoysk.funnyguilds.element.tablist.variable.impl.SimpleTablistVariable
import net.dzikoysk.funnyguilds.util.IntegerRange.MissingFormatException
import net.dzikoysk.funnyguilds.element.notification.NotificationUtil
import java.text.MessageFormat
import net.dzikoysk.funnyguilds.element.notification.bossbar.provider.v1_8.BossBarProviderImpl
import net.dzikoysk.funnyguilds.element.notification.bossbar.provider.DefaultBossBarProvider
import net.dzikoysk.funnyguilds.element.DummyManager
import net.dzikoysk.funnyguilds.element.IndividualPrefixManager
import net.dzikoysk.funnyguilds.listener.region.BlockPlace
import org.bukkit.event.entity.EntityPlaceEvent
import net.dzikoysk.funnyguilds.listener.region.GuildHeartProtectionHandler
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerRegistration
import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildPointsRequest
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateUserPointsRequest
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyRequest
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyExceptionHandler
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyExceptionHandler
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest
import net.dzikoysk.funnyguilds.util.commons.ConfigHelper
import java.util.concurrent.ExecutorService
import java.lang.InterruptedException
import java.util.concurrent.Executors
import net.dzikoysk.funnyguilds.FunnyGuildsVersion
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerManager
import net.dzikoysk.funnyguilds.data.DataPersistenceHandler
import net.dzikoysk.funnyguilds.data.InvitationPersistenceHandler
import net.dzikoysk.funnyguilds.util.nms.DescriptionChanger
import net.dzikoysk.funnyguilds.command.CommandsConfiguration
import net.dzikoysk.funnyguilds.util.metrics.MetricsCollector
import net.dzikoysk.funnyguilds.system.GuildValidationHandler
import net.dzikoysk.funnyguilds.element.tablist.TablistBroadcastHandler
import net.dzikoysk.funnyguilds.basic.rank.RankRecalculationTaskimport

net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.element.gui.GuiActionHandler
import net.dzikoysk.funnyguilds.listener.EntityDamage
import net.dzikoysk.funnyguilds.listener.EntityInteract
import net.dzikoysk.funnyguilds.listener.PlayerChat
import net.dzikoysk.funnyguilds.listener.PlayerDeath
import net.dzikoysk.funnyguilds.listener.PlayerJoin
import net.dzikoysk.funnyguilds.listener.PlayerLogin
import net.dzikoysk.funnyguilds.listener.PlayerQuit
import net.dzikoysk.funnyguilds.listener.TntProtection
import net.dzikoysk.funnyguilds.listener.BlockFlow
import net.dzikoysk.funnyguilds.listener.region.EntityPlace
import net.dzikoysk.funnyguilds.listener.region.BlockBreak
import net.dzikoysk.funnyguilds.listener.region.BlockIgnite
import net.dzikoysk.funnyguilds.listener.region.BucketAction
import net.dzikoysk.funnyguilds.listener.region.EntityExplode
import net.dzikoysk.funnyguilds.listener.region.HangingBreak
import net.dzikoysk.funnyguilds.listener.region.HangingPlace
import net.dzikoysk.funnyguilds.listener.region.PlayerCommand
import net.dzikoysk.funnyguilds.listener.region.PlayerInteract
import net.dzikoysk.funnyguilds.listener.region.EntityProtect
import net.dzikoysk.funnyguilds.listener.region.PlayerMove
import net.dzikoysk.funnyguilds.listener.region.BlockPhysics
import net.dzikoysk.funnyguilds.listener.region.PlayerRespawnimport

org.bukkit.entity.Player
import java.lang.StackTraceElement

@FunnyComponent
class BreakCommand {
    @FunnyCommand(
        name = "\${user.break.name}",
        description = "\${user.break.description}",
        aliases = ["\${user.break.aliases}"],
        permission = "funnyguilds.break",
        completer = "guilds:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    fun execute(messages: MessageConfiguration, player: Player, @IsOwner user: User?, guild: Guild, args: Array<String?>) {
        DefaultValidation.`when`(guild.allies!!.isEmpty(), messages.breakHasNotAllies)
        if (args.size < 1) {
            val list = messages.breakAlliesList
            val iss = ChatUtils.toString(GuildUtils.getNames(guild.allies), true)
            for (msg in list!!) {
                player.sendMessage(msg!!.replace("{GUILDS}", iss!!))
            }
            return
        }
        val oppositeGuild = GuildValidation.requireGuildByTag(args[0])
        DefaultValidation.`when`(!guild.allies!!.contains(oppositeGuild)) { messages.breakAllyExists.replace("{GUILD}", oppositeGuild.name!!).replace("{TAG}", guild.tag) }
        if (!SimpleEventHandler.handle(GuildBreakAllyEvent(EventCause.USER, user, guild, oppositeGuild))) {
            return
        }
        val owner = oppositeGuild.owner.player
        owner?.sendMessage(messages.breakIDone.replace("{GUILD}", guild.name!!).replace("{TAG}", guild.tag))
        guild.removeAlly(oppositeGuild)
        oppositeGuild.removeAlly(guild)
        val concurrencyManager: ConcurrencyManager = FunnyGuilds.Companion.getInstance().getConcurrencyManager()
        val taskBuilder: ConcurrencyTaskBuilder = ConcurrencyTask.Companion.builder()
        for (member in guild.members) {
            taskBuilder.delegate(PrefixUpdateGuildRequest(member, oppositeGuild))
        }
        for (member in oppositeGuild.members) {
            taskBuilder.delegate(PrefixUpdateGuildRequest(member, guild))
        }
        val task = taskBuilder.build()
        concurrencyManager.postTask(task)
        player.sendMessage(messages.breakDone.replace("{GUILD}", oppositeGuild.name!!).replace("{TAG}", oppositeGuild.tag))
    }
}