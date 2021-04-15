package net.dzikoysk.funnyguilds.listenerimport

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.rank.RankSystem
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildPointsRequest
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateUserPointsRequest
import net.dzikoysk.funnyguilds.concurrency.requests.dummy.DummyGlobalUpdateUserRequest
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateUserRequest
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.element.notification.NotificationUtil
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent
import net.dzikoysk.funnyguilds.event.rank.RankChangeEvent
import net.dzikoysk.funnyguilds.hook.*
import net.dzikoysk.funnyguilds.util.IntegerRange
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import net.dzikoysk.funnyguilds.util.commons.MapUtil
import net.dzikoysk.funnyguilds.util.commons.bukkit.MaterialUtils
import net.dzikoysk.funnyguilds.util.nms.PacketSender
import org.apache.commons.lang3.StringUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.panda_lang.utilities.commons.text.Formatter
import java.util.*

net.dzikoysk.funnyguilds.data .flat.FlatDataModel
import net.dzikoysk.funnyguilds.util.YamlWrapper
import net.dzikoysk.funnyguilds.data.util.DeserializationUtils
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils
import net.dzikoysk.funnyguilds.basic.user.UserUtils
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import net.dzikoysk.funnyguilds.data.flat.FlatGuild
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import net.dzikoysk.funnyguilds.data.flat.FlatUser
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseFixAlliesRequest
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest
import net.dzikoysk.funnyguilds.data.flat.FlatPatcher
import net.dzikoysk.funnyguilds.data.util.InvitationList.Invitation
import net.dzikoysk.funnyguilds.data.util.InvitationList
import com.google.common.collect.ImmutableList
import java.util.stream.Collectors
import net.dzikoysk.funnyguilds.data.util.InvitationList.InvitationType
import net.dzikoysk.funnyguilds.data.util.ConfirmationList
import net.dzikoysk.funnyguilds.basic.user.UserBan
import org.diorite.cfg.annotations.CfgClass
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault
import org.diorite.cfg.annotations.CfgComment
import org.diorite.cfg.annotations.CfgExclude
import net.dzikoysk.funnyguilds.util.Cooldown
import java.text.SimpleDateFormat
import org.diorite.cfg.annotations.CfgName
import org.diorite.cfg.annotations.CfgStringStyle
import org.diorite.cfg.annotations.CfgStringStyle.StringStyle
import net.dzikoysk.funnyguilds.basic.guild.GuildRegex
import org.diorite.cfg.annotations.CfgCollectionStyle
import org.diorite.cfg.annotations.CfgCollectionStyle.CollectionStyle
import java.time.LocalTime
import com.google.common.collect.ImmutableMap
import net.dzikoysk.funnyguilds.basic.rank.RankSystem
import net.dzikoysk.funnyguilds.util.IntegerRange
import net.dzikoysk.funnyguilds.element.notification.NotificationStyle
import net.dzikoysk.funnyguilds.element.notification.bossbar.provider.BossBarOptions
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration.MySQL
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils
import net.dzikoysk.funnyguilds.basic.rank.RankUtils
import java.lang.IndexOutOfBoundsException
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemBuilder
import net.dzikoysk.funnyguilds.util.commons.bukkit.MaterialUtils
import kotlin.collections.MutableMap.MutableEntry
import java.lang.NumberFormatException
import java.util.EnumMap
import java.time.format.DateTimeFormatter
import net.dzikoysk.funnyguilds.util.nms.Reflections
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration.Commands.AdminCommands
import kotlin.jvm.JvmOverloads
import net.dzikoysk.funnyguilds.data.database.element.SQLElement
import net.dzikoysk.funnyguilds.data.database.element.SQLTable
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement
import net.dzikoysk.funnyguilds.data.database.Database
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.ResultSet
import kotlin.Throws
import com.zaxxer.hikari.HikariDataSource
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils
import net.dzikoysk.funnyguilds.data.database.SQLDataModel
import net.dzikoysk.funnyguilds.data.database.DatabaseUser
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild
import kotlin.jvm.Volatile
import java.lang.Runnable
import net.dzikoysk.funnyguilds.concurrency.requests.DataSaveRequest
import net.dzikoysk.funnyguilds.hook.worldedit.WorldEditHook
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.jnbt.NBTInputStream
import java.util.zip.GZIPInputStream
import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.session.PasteBuilder
import com.sk89q.worldedit.function.operation.Operations
import java.lang.InstantiationException
import java.lang.RuntimeException
import java.lang.IllegalAccessException
import java.lang.reflect.InvocationTargetException
import com.sk89q.worldedit.MaxChangedBlocksException
import java.io.IOException
import com.sk89q.worldedit.extent.Extent
import java.lang.NoSuchMethodException
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.WorldEditException
import com.sk89q.worldguard.protection.ApplicableRegionSet
import net.dzikoysk.funnyguilds.hook.worldguard.WorldGuardHook
import java.lang.invoke.MethodHandles
import net.dzikoysk.funnyguilds.hook.worldguard.WorldGuard6Hook
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.managers.RegionManager
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException
import java.lang.IllegalArgumentException
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import com.sk89q.worldguard.WorldGuard
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import net.dzikoysk.funnyguilds.hook.worldguard.WorldGuard7Hook
import java.lang.ClassNotFoundException
import net.dzikoysk.funnyguilds.hook.worldedit.WorldEdit6Hook
import net.dzikoysk.funnyguilds.hook.worldedit.WorldEdit7Hook
import net.dzikoysk.funnyguilds.FunnyGuildsLogger
import net.dzikoysk.funnyguilds.hook.LeaderHeadsHook.TopRankCollector
import me.robin.leaderheads.datacollectors.DataCollector
import me.robin.leaderheads.objects.BoardType
import net.dzikoysk.funnyguilds.basic.rank.RankManager
import net.dzikoysk.funnyguilds.hook.PlaceholderAPIHook.FunnyGuildsPlaceholder
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import net.dzikoysk.funnyguilds.element.tablist.variable.TablistVariable
import net.dzikoysk.funnyguilds.element.tablist.variable.DefaultTablistVariables
import codecrafter47.bungeetablistplus.api.bukkit.BungeeTabListPlusBukkitAPI
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent
import net.dzikoysk.funnyguilds.util.nms.Reflections.InvalidMarker
import net.dzikoysk.funnyguilds.util.commons.SafeUtils
import net.dzikoysk.funnyguilds.util.commons.SafeUtils.SafeInitializer
import java.lang.Void
import net.dzikoysk.funnyguilds.util.nms.PacketSender
import net.dzikoysk.funnyguilds.util.nms.PacketCreator
import java.lang.ThreadLocal
import net.dzikoysk.funnyguilds.util.nms.EggTypeChanger
import java.lang.SecurityException
import net.dzikoysk.funnyguilds.util.nms.PacketExtension
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.dzikoysk.funnyguilds.concurrency.requests.WarUseRequest
import io.netty.channel.ChannelPipeline
import net.dzikoysk.funnyguilds.util.nms.BlockDataChanger
import net.dzikoysk.funnyguilds.util.nms.GuildEntityHelper
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.util.commons.spigot.ItemComponentUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.NotePitch
import net.dzikoysk.funnyguilds.util.commons.bukkit.PingUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.SpaceUtils
import org.panda_lang.utilities.commons.function.QuadFunction
import java.text.DecimalFormat
import net.dzikoysk.funnyguilds.util.commons.bukkit.MinecraftServerUtils
import java.lang.NoSuchFieldException
import net.md_5.bungee.api.chat.BaseComponent
import java.io.FileNotFoundException
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.util.Collections
import java.util.function.BinaryOperator
import net.dzikoysk.funnyguilds.util.commons.MapUtil
import java.util.Locale
import org.diorite.cfg.system.TemplateCreator
import org.apache.logging.log4j.core.Appender
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender
import net.dzikoysk.funnyguilds.util.metrics.BStats
import net.dzikoysk.funnyguilds.util.metrics.BStats.Country
import java.io.DataOutputStream
import net.dzikoysk.funnyguilds.util.metrics.MCStats
import java.io.BufferedReader
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import net.dzikoysk.funnyguilds.util.telemetry.FunnyTelemetry
import net.dzikoysk.funnyguilds.util.telemetry.PasteType
import net.dzikoysk.funnyguilds.util.telemetry.FunnybinResponse
import org.diorite.utils.network.DioriteURLUtils
import net.dzikoysk.funnyguilds.util.FunnyBox
import net.dzikoysk.funnyguilds.basic.rank.Rank
import java.util.NavigableSet
import net.dzikoysk.funnyguilds.util.commons.bukkit.PermissionUtils
import com.google.common.collect.Iterables
import net.dzikoysk.funnyguilds.basic.AbstractBasic
import net.dzikoysk.funnyguilds.basic.user.UserCache
import net.dzikoysk.funnyguilds.element.notification.bossbar.provider.BossBarProvider
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateUserRequest
import com.google.common.cache.CacheBuilder
import net.dzikoysk.funnyguilds.element.IndividualPrefix
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemoveGuildRequest
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.rank.RankEvent
import net.dzikoysk.funnyguilds.event.rank.RankChangeEvent
import net.dzikoysk.funnyguilds.event.rank.KillsChangeEvent
import net.dzikoysk.funnyguilds.event.rank.DeathsChangeEvent
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent
import net.dzikoysk.funnyguilds.event.guild.GuildEvent
import net.dzikoysk.funnyguilds.event.guild.ally.GuildAllyEvent
import net.dzikoysk.funnyguilds.event.guild.ally.GuildBreakAllyEvent
import net.dzikoysk.funnyguilds.event.guild.ally.GuildSendAllyInvitationEvent
import net.dzikoysk.funnyguilds.event.guild.ally.GuildAcceptAllyInvitationEvent
import net.dzikoysk.funnyguilds.event.guild.ally.GuildRevokeAllyInvitationEvent
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberEvent
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaveEvent
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberDeputyEvent
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberInviteEvent
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaderEvent
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberAcceptInviteEvent
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberRevokeInviteEvent
import net.dzikoysk.funnyguilds.event.guild.GuildBanEvent
import net.dzikoysk.funnyguilds.event.guild.GuildMoveEvent
import net.dzikoysk.funnyguilds.event.guild.GuildUnbanEvent
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent
import net.dzikoysk.funnyguilds.event.guild.GuildRenameEvent
import net.dzikoysk.funnyguilds.event.guild.GuildEnlargeEvent
import net.dzikoysk.funnyguilds.event.guild.GuildPreCreateEvent
import net.dzikoysk.funnyguilds.event.guild.GuildPreRenameEvent
import net.dzikoysk.funnyguilds.event.guild.GuildTagChangeEvent
import net.dzikoysk.funnyguilds.event.guild.GuildBaseChangeEvent
import net.dzikoysk.funnyguilds.event.guild.GuildLivesChangeEvent
import net.dzikoysk.funnyguilds.event.guild.GuildRegionEnterEvent
import net.dzikoysk.funnyguilds.event.guild.GuildRegionLeaveEvent
import net.dzikoysk.funnyguilds.event.guild.GuildPreTagChangeEvent
import net.dzikoysk.funnyguilds.event.guild.GuildEntityExplodeEvent
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent
import net.dzikoysk.funnyguilds.system.ban.BanUtils
import net.dzikoysk.funnyguilds.system.war.WarUtils
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.system.war.WarSystem
import net.dzikoysk.funnyguilds.command.user.InfoCommand
import net.dzikoysk.funnyguilds.system.war.WarListener
import net.dzikoysk.funnyguilds.system.security.SecuritySystem
import net.dzikoysk.funnycommands.resources.ValidationException
import net.dzikoysk.funnyguilds.system.security.cheat.SecurityReach
import net.dzikoysk.funnyguilds.system.security.SecurityUtils
import net.dzikoysk.funnyguilds.system.security.SecurityType
import net.dzikoysk.funnyguilds.system.security.cheat.SecurityFreeCam
import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem
import net.dzikoysk.funnyguilds.system.validity.ValidityUtils
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent
import net.dzikoysk.funnyguilds.command.CanManage
import net.dzikoysk.funnyguilds.command.DefaultValidation
import net.dzikoysk.funnyguilds.command.GuildValidation
import net.dzikoysk.funnyguilds.command.IsOwner
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixUpdateGuildRequest
import net.dzikoysk.funnyguilds.command.IsMember
import java.util.concurrent.atomic.AtomicInteger
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest
import net.dzikoysk.funnyguilds.command.UserValidation
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemovePlayerRequest
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer
import net.dzikoysk.funnyguilds.element.gui.GuiWindow
import net.dzikoysk.funnyguilds.element.gui.GuiItem
import net.dzikoysk.funnyguilds.command.user.CreateCommand
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateGuildRequest
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddGuildRequest
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildRequest
import net.dzikoysk.funnyguilds.command.user.DeleteCommand
import net.dzikoysk.funnyguilds.command.user.ConfirmCommand
import net.dzikoysk.funnyguilds.concurrency.requests.ReloadRequest
import net.dzikoysk.funnyguilds.concurrency.requests.FunnybinRequest
import net.dzikoysk.funnyguilds.command.admin.AdminUtils
import net.dzikoysk.funnyguilds.command.admin.ProtectionCommand
import java.lang.IllegalStateException
import net.dzikoysk.funnyguilds.command.GuildBind
import net.dzikoysk.funnyguilds.command.UserBind
import net.dzikoysk.funnyguilds.command.OwnerValidator
import net.dzikoysk.funnyguilds.command.MemberValidator
import net.dzikoysk.funnyguilds.command.ManageValidator
import net.dzikoysk.funnycommands.FunnyCommands
import net.dzikoysk.funnyguilds.command.CommandsConfiguration.CommandComponents
import net.dzikoysk.funnyguilds.command.user.AllyCommand
import net.dzikoysk.funnyguilds.command.user.BaseCommand
import net.dzikoysk.funnyguilds.command.user.BreakCommand
import net.dzikoysk.funnyguilds.command.user.DeputyCommand
import net.dzikoysk.funnyguilds.command.user.EnlargeCommand
import net.dzikoysk.funnyguilds.command.user.EscapeCommand
import net.dzikoysk.funnyguilds.command.user.FunnyGuildsCommand
import net.dzikoysk.funnyguilds.command.user.GuildCommand
import net.dzikoysk.funnyguilds.command.user.InviteCommand
import net.dzikoysk.funnyguilds.command.user.ItemsCommand
import net.dzikoysk.funnyguilds.command.user.JoinCommand
import net.dzikoysk.funnyguilds.command.user.KickCommand
import net.dzikoysk.funnyguilds.command.user.LeaderCommand
import net.dzikoysk.funnyguilds.command.user.LeaveCommand
import net.dzikoysk.funnyguilds.command.user.PlayerInfoCommand
import net.dzikoysk.funnyguilds.command.user.PvPCommand
import net.dzikoysk.funnyguilds.command.user.RankingCommand
import net.dzikoysk.funnyguilds.command.user.RankResetCommand
import net.dzikoysk.funnyguilds.command.user.SetBaseCommand
import net.dzikoysk.funnyguilds.command.user.TopCommand
import net.dzikoysk.funnyguilds.command.user.ValidityCommand
import net.dzikoysk.funnyguilds.command.user.WarCommand
import net.dzikoysk.funnyguilds.command.user.TntCommand
import net.dzikoysk.funnyguilds.command.admin.AddCommand
import net.dzikoysk.funnyguilds.command.admin.BaseAdminCommand
import net.dzikoysk.funnyguilds.command.admin.BanCommand
import net.dzikoysk.funnyguilds.command.admin.DeathsCommand
import net.dzikoysk.funnyguilds.command.admin.DeleteAdminCommand
import net.dzikoysk.funnyguilds.command.admin.DeputyAdminCommand
import net.dzikoysk.funnyguilds.command.admin.GuildsEnabledCommand
import net.dzikoysk.funnyguilds.command.admin.KickAdminCommand
import net.dzikoysk.funnyguilds.command.admin.KillsCommand
import net.dzikoysk.funnyguilds.command.admin.LeaderAdminCommand
import net.dzikoysk.funnyguilds.command.admin.LivesCommand
import net.dzikoysk.funnyguilds.command.admin.MainCommand
import net.dzikoysk.funnyguilds.command.admin.MoveCommand
import net.dzikoysk.funnyguilds.command.admin.NameCommand
import net.dzikoysk.funnyguilds.command.admin.PointsCommand
import net.dzikoysk.funnyguilds.command.admin.SpyCommand
import net.dzikoysk.funnyguilds.command.admin.TagCommand
import net.dzikoysk.funnyguilds.command.admin.TeleportCommand
import net.dzikoysk.funnyguilds.command.admin.UnbanCommand
import net.dzikoysk.funnyguilds.command.admin.ValidityAdminCommand
import net.dzikoysk.funnyguilds.command.SettingsBind
import net.dzikoysk.funnyguilds.command.MessagesBind
import net.dzikoysk.funnycommands.resources.types.PlayerType
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
import net.dzikoysk.funnyguilds.basic.rank.RankRecalculationTask
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
import net.dzikoysk.funnyguilds.listener.region.PlayerRespawn
import java.lang.StackTraceElement

class PlayerDeath : Listener {
    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        val playerVictim = event.entity
        var playerAttacker = event.entity.killer
        val victim: User = User.Companion.get(playerVictim)
        val victimCache = victim.cache
        victim.rank.addDeath()
        if (playerAttacker == null) {
            if (!config.considerLastAttackerAsKiller) {
                victimCache!!.clearDamage()
                return
            }
            val lastAttacker = victimCache.lastAttacker
            if (lastAttacker == null || !lastAttacker.isOnline) {
                victimCache!!.clearDamage()
                return
            }
            val attackTime = victimCache!!.wasVictimOf(lastAttacker)
            if (attackTime == null || attackTime + config.lastAttackerAsKillerConsiderationTimeout_ < System.currentTimeMillis()) {
                victimCache.clearDamage()
                return
            }
            playerAttacker = lastAttacker.player
        }
        val attacker: User = User.Companion.get(playerAttacker)
        val attackerCache = attacker.cache
        if (victim == attacker) {
            victimCache!!.clearDamage()
            return
        }
        if (PluginHook.isPresent(PluginHook.PLUGIN_WORLDGUARD)) {
            if (PluginHook.WORLD_GUARD!!.isInNonPointsRegion(playerVictim.location) || PluginHook.WORLD_GUARD!!.isInNonPointsRegion(
                    playerAttacker!!.location
                )
            ) {
                victimCache!!.clearDamage()
                return
            }
        }
        val messages: MessageConfiguration = FunnyGuilds.Companion.getInstance().getMessageConfiguration()
        if (config.rankFarmingProtect) {
            val attackTimestamp = attackerCache!!.wasAttackerOf(victim)
            val victimTimestamp = attackerCache.wasVictimOf(attacker)
            if (attackTimestamp != null) {
                if (attackTimestamp + config.rankFarmingCooldown * 1000L >= System.currentTimeMillis()) {
                    playerVictim.sendMessage(messages.rankLastVictimV)
                    playerAttacker!!.sendMessage(messages.rankLastVictimA)
                    victimCache!!.clearDamage()
                    event.deathMessage = null
                    return
                }
            } else if (victimTimestamp != null) {
                if (victimTimestamp + config.rankFarmingCooldown * 1000L >= System.currentTimeMillis()) {
                    playerVictim.sendMessage(messages.rankLastAttackerV)
                    playerAttacker!!.sendMessage(messages.rankLastAttackerA)
                    victimCache!!.clearDamage()
                    event.deathMessage = null
                    return
                }
            }
        }
        if (config.rankIPProtect) {
            val attackerIP = playerAttacker!!.address!!.hostString
            if (attackerIP != null && attackerIP.equals(playerVictim.address!!.hostString, ignoreCase = true)) {
                playerVictim.sendMessage(messages.rankIPVictim)
                playerAttacker.sendMessage(messages.rankIPAttacker)
                victimCache!!.clearDamage()
                event.deathMessage = null
                return
            }
        }
        var rankChanges = IntArray(2)
        val victimPoints = victim.rank.points
        val attackerPoints = attacker.rank.points
        when (config.rankSystem) {
            RankSystem.PERCENT -> {
                val value = victim.rank.points * (config.percentRankChange / 100.0)
                rankChanges[0] = value.toInt()
                rankChanges[1] = value.toInt()
            }
            RankSystem.STATIC -> {
                rankChanges[0] = config.staticAttackerChange
                rankChanges[1] = config.staticVictimChange
            }
            RankSystem.ELO -> rankChanges = getEloValues(victimPoints, attackerPoints)
            else -> rankChanges = getEloValues(victimPoints, attackerPoints)
        }
        val attackerEvent: RankChangeEvent = PointsChangeEvent(EventCause.USER, attacker.rank, attacker, rankChanges[0])
        val victimEvent: RankChangeEvent = PointsChangeEvent(EventCause.USER, victim.rank, attacker, rankChanges[1])
        val assistEntries: MutableList<String> = ArrayList()
        val messageReceivers: MutableList<User?> = ArrayList()
        var victimPointsBeforeChange = victim.rank.points
        if (SimpleEventHandler.handle(attackerEvent) && SimpleEventHandler.handle(victimEvent)) {
            val attackerDamage = victimCache!!.killedBy(attacker)
            if (config.assistEnable && victimCache.isAssisted) {
                val toShare = attackerEvent.change * (1 - config.assistKillerShare)
                val totalDamage = victimCache.totalDamage + attackerDamage
                var givenPoints = 0
                val damage = MapUtil.sortByValue(
                    victimCache.damage
                )
                var assists = 0
                for ((key, value) in damage) {
                    val assistFraction = value / totalDamage
                    val addedPoints = Math.round(assistFraction * toShare).toInt()
                    if (addedPoints <= 0) {
                        continue
                    }
                    if (config.assistsLimit > 0) {
                        if (assists >= config.assistsLimit) {
                            continue
                        }
                        assists++
                    }
                    if (!config.broadcastDeathMessage) {
                        messageReceivers.add(key)
                    }
                    givenPoints += addedPoints
                    var assistEntry = StringUtils.replace(messages.rankAssistEntry, "{PLAYER}", key.name)
                    assistEntry = StringUtils.replace(assistEntry, "{+}", Integer.toString(addedPoints))
                    assistEntry = StringUtils.replace(assistEntry, "{SHARE}", ChatUtils.getPercent(assistFraction))
                    assistEntries.add(assistEntry)
                    key.rank.addPoints(addedPoints)
                }
                val updatedAttackerPoints: Double = attackerEvent.change - toShare + if (givenPoints < toShare) toShare - givenPoints else 0
                attackerEvent.change = Math.round(updatedAttackerPoints).toInt()
            }
            attacker.rank.addKill()
            attacker.rank.addPoints(attackerEvent.change)
            attackerCache!!.registerVictim(victim)
            victimPointsBeforeChange = victim.rank.points
            victim.rank.removePoints(victimEvent.change)
            victimCache.registerAttacker(attacker)
            victimCache.clearDamage()
            if (!config.broadcastDeathMessage) {
                messageReceivers.add(attacker)
                messageReceivers.add(victim)
            }
        }
        val concurrencyManager: ConcurrencyManager = FunnyGuilds.Companion.getInstance().getConcurrencyManager()
        val taskBuilder: ConcurrencyTaskBuilder = ConcurrencyTask.Companion.builder()
        if (config.dataModel == PluginConfiguration.DataModel.MYSQL) {
            if (victim.hasGuild()) {
                taskBuilder.delegate(DatabaseUpdateGuildPointsRequest(victim.guild))
            }
            if (attacker.hasGuild()) {
                taskBuilder.delegate(DatabaseUpdateGuildPointsRequest(attacker.guild))
            }
            taskBuilder.delegate(DatabaseUpdateUserPointsRequest(victim))
            taskBuilder.delegate(DatabaseUpdateUserPointsRequest(attacker))
        }
        concurrencyManager.postTask(
            taskBuilder
                .delegate(DummyGlobalUpdateUserRequest(victim))
                .delegate(DummyGlobalUpdateUserRequest(attacker))
                .delegate(RankUpdateUserRequest(victim))
                .delegate(RankUpdateUserRequest(attacker))
                .build()
        )
        val killFormatter = Formatter()
            .register("{ATTACKER}", attacker.name)
            .register("{VICTIM}", victim.name)
            .register("{+}", Integer.toString(attackerEvent.change))
            .register("{-}", Math.min(victimPointsBeforeChange, victimEvent.change))
            .register("{POINTS-FORMAT}", IntegerRange.Companion.inRangeToString<String?>(victimPoints, config.pointsFormat))
            .register("{POINTS}", Integer.toString(victim.rank.points))
            .register("{WEAPON}", MaterialUtils.getMaterialName(playerAttacker!!.itemInHand.type))
            .register("{REMAINING-HEALTH}", String.format(Locale.US, "%.2f", playerAttacker.health))
            .register("{REMAINING-HEARTS}", Integer.toString((playerAttacker.health / 2).toInt()))
            .register("{VTAG}", if (victim.hasGuild()) StringUtils.replace(config.chatGuild, "{TAG}", victim.guild.tag) else "")
            .register("{ATAG}", if (attacker.hasGuild()) StringUtils.replace(config.chatGuild, "{TAG}", attacker.guild.tag) else "")
            .register(
                "{ASSISTS}",
                if (config.assistEnable && !assistEntries.isEmpty()) StringUtils.replace(
                    messages.rankAssistMessage,
                    "{ASSISTS}",
                    java.lang.String.join(messages.rankAssistDelimiter, assistEntries)
                ) else ""
            )
        if (config.displayTitleNotificationForKiller) {
            val titlePackets = NotificationUtil.createTitleNotification(
                killFormatter.format(messages.rankKillTitle),
                killFormatter.format(messages.rankKillSubtitle),
                config.notificationTitleFadeIn,
                config.notificationTitleStay,
                config.notificationTitleFadeOut
            )
            PacketSender.sendPacket(playerAttacker, titlePackets)
        }
        val deathMessage = killFormatter.format(messages.rankDeathMessage)
        if (config.broadcastDeathMessage) {
            if (config.ignoreDisabledDeathMessages) {
                for (player in event.entity.world.players) {
                    event.deathMessage = null
                    player.sendMessage(deathMessage)
                }
            } else {
                event.deathMessage = deathMessage
            }
        } else {
            event.deathMessage = null
            for (fighter in messageReceivers) {
                if (fighter!!.isOnline) {
                    fighter.player.sendMessage(deathMessage)
                }
            }
        }
    }

    private fun getEloValues(victimPoints: Int, attackerPoints: Int): IntArray {
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        val rankChanges = IntArray(2)
        val attackerElo: Int = IntegerRange.Companion.inRange<Int>(attackerPoints, config.eloConstants).orElseGet(0)
        val victimElo: Int = IntegerRange.Companion.inRange<Int>(victimPoints, config.eloConstants).orElseGet(0)
        rankChanges[0] = Math.round(attackerElo * (1 - 1.0 / (1.0 + Math.pow(config.eloExponent, (victimPoints - attackerPoints) / config.eloDivider)))).toInt()
        rankChanges[1] = Math.round(victimElo * (0 - 1.0 / (1.0 + Math.pow(config.eloExponent, (attackerPoints - victimPoints) / config.eloDivider))) * -1).toInt()
        return rankChanges
    }
}