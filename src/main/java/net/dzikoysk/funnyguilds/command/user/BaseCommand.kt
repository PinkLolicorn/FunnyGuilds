package net.dzikoysk.funnyguilds.command.user

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.command.DefaultValidation
import net.dzikoysk.funnyguilds.command.IsMember
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.atomic.AtomicInteger

@FunnyComponent
class BaseCommand {
    @FunnyCommand(
        name = "\${user.base.name}",
        aliases = ["\${user.base.aliases}"],
        description = "\${user.base.description}",
        permission = "funnyguilds.base",
        acceptsExceeded = true,
        playerOnly = true
    )
    fun execute(config: PluginConfiguration, messages: MessageConfiguration, player: Player, @IsMember user: User, guild: Guild) {
        DefaultValidation.`when`(!config.regionsEnabled, messages.regionsDisabled)
        DefaultValidation.`when`(!config.baseEnable, messages.baseTeleportationDisabled)
        DefaultValidation.`when`(user.cache.teleportation != null, messages.baseIsTeleportation)
        val requiredItems = if (player.hasPermission("funnyguilds.vip.base")) emptyList() else config.baseItems!!
        if (!ItemUtils.playerHasEnoughItems(player, requiredItems)) {
            return
        }
        val items = ItemUtils.toArray(requiredItems)
        player.inventory.removeItem(*items!!)
        if (config.baseDelay < 1) {
            player.teleport(guild.home!!)
            player.sendMessage(messages.baseTeleport)
            return
        }
        val time = if (player.hasPermission("funnyguilds.vip.baseTeleportTime")) config.baseDelayVip else config.baseDelay
        val before = player.location
        val timeCounter = AtomicInteger(1)
        val cache = user.cache
        cache.teleportation = Bukkit.getScheduler().runTaskTimer(FunnyGuilds.Companion.getInstance(), Runnable {
            if (!player.isOnline) {
                cache.teleportation.cancel()
                cache.teleportation = null
                return@runTaskTimer
            }
            if (!LocationUtils.equals(player.location, before)) {
                cache.teleportation.cancel()
                player.sendMessage(messages.baseMove)
                cache.teleportation = null
                player.inventory.addItem(*items)
                return@runTaskTimer
            }
            if (timeCounter.getAndIncrement() > time) {
                cache.teleportation.cancel()
                player.sendMessage(messages.baseTeleport)
                player.teleport(guild.home!!)
                cache.teleportation = null
            }
        }, 0L, 20L)
        player.sendMessage(messages.baseDontMove.replace("{TIME}", Integer.toString(time)))
    }
}