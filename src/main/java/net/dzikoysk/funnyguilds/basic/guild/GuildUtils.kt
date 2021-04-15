package net.dzikoysk.funnyguilds.basic.guild

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.user.UserUtils
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemoveGuildRequest
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild
import net.dzikoysk.funnyguilds.data.database.SQLDataModel
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel
import net.dzikoysk.funnyguilds.util.nms.BlockDataChanger
import net.dzikoysk.funnyguilds.util.nms.GuildEntityHelper
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate
import java.util.stream.Collectors

object GuildUtils {
    private val GUILDS: MutableSet<Guild?> = ConcurrentHashMap.newKeySet()
    val guilds: MutableSet<Guild?>
        get() = HashSet(GUILDS)

    fun deleteGuild(guild: Guild?) {
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        if (guild == null) {
            return
        }
        if (config.regionsEnabled) {
            val region = guild.region
            if (region != null) {
                if (config.createEntityType != null) {
                    GuildEntityHelper.despawnGuildHeart(guild)
                } else if (config.createMaterial != null && config.createMaterial!!.left != Material.AIR) {
                    val centerLocation = region.center.clone()
                    Bukkit.getScheduler().runTask(FunnyGuilds.Companion.getInstance(), Runnable {
                        val block = centerLocation.block.getRelative(BlockFace.DOWN)
                        if (block.location.blockY > 1) {
                            block.type = Material.AIR
                        }
                    })
                }
            }
            RegionUtils.delete(guild.region)
        }
        val concurrencyManager: ConcurrencyManager = FunnyGuilds.Companion.getInstance().getConcurrencyManager()
        concurrencyManager.postRequests(PrefixGlobalRemoveGuildRequest(guild))
        UserUtils.removeGuild(guild.members)
        for (ally in guild.allies!!) {
            ally!!.removeAlly(guild)
        }
        for (globalGuild in guilds) {
            globalGuild!!.removeEnemy(guild)
        }
        if (FunnyGuilds.Companion.getInstance().getDataModel() is FlatDataModel) {
            val dataModel = FunnyGuilds.Companion.getInstance().getDataModel() as FlatDataModel
            dataModel.getGuildFile(guild).delete()
        } else if (FunnyGuilds.Companion.getInstance().getDataModel() is SQLDataModel) {
            DatabaseGuild.delete(guild)
        }
        guild.delete()
    }

    fun spawnHeart(guild: Guild) {
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        if (config.createMaterial != null && config.createMaterial!!.left != Material.AIR) {
            val heart = guild.region.center.block.getRelative(BlockFace.DOWN)
            heart.type = config.createMaterial!!.left!!
            BlockDataChanger.applyChanges(heart, config.createMaterial!!.right!!)
        } else if (config.createEntityType != null) {
            GuildEntityHelper.spawnGuildHeart(guild)
        }
    }

    fun getByName(name: String?): Guild? {
        for (guild in GUILDS) {
            if (guild!!.name != null && guild.name.equals(name, ignoreCase = true)) {
                return guild
            }
        }
        return null
    }

    fun getByUUID(uuid: UUID?): Guild? {
        for (guild in GUILDS) {
            if (guild.getUUID() == uuid) {
                return guild
            }
        }
        return null
    }

    fun getByTag(tag: String?): Guild? {
        for (guild in GUILDS) {
            if (guild.getTag() != null && guild.getTag().equals(tag!!.toLowerCase(), ignoreCase = true)) {
                return guild
            }
        }
        return null
    }

    fun nameExists(name: String?): Boolean {
        for (guild in GUILDS) {
            if (guild!!.name != null && guild.name.equals(name, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun tagExists(tag: String?): Boolean {
        for (guild in GUILDS) {
            if (guild.getTag() != null && guild.getTag().equals(tag, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun getNames(guilds: Collection<Guild?>?): MutableSet<String?> {
        return guilds!!.stream()
            .filter { obj: Guild? -> Objects.nonNull(obj) }
            .map { obj: Guild? -> obj!!.name }
            .collect(Collectors.toSet())
    }

    fun getTags(guilds: Collection<Guild?>?): List<String?> {
        return guilds!!.stream()
            .filter { obj: Guild? -> Objects.nonNull(obj) }
            .map { obj: Guild? -> obj.getTag() }
            .collect(Collectors.toList())
    }

    fun getGuilds(names: Collection<String?>?): MutableSet<Guild> {
        return names!!.stream()
            .map { obj: String? -> getByName() }
            .filter(Predicate { obj: Guild? -> Objects.nonNull(obj) })
            .collect(Collectors.toSet())
    }

    @Synchronized
    fun addGuild(guild: Guild?) {
        if (guild == null) {
            return
        }
        if (getByName(guild.name) == null) {
            GUILDS.add(guild)
        }
    }

    @Synchronized
    fun removeGuild(guild: Guild?) {
        GUILDS.remove(guild)
    }

    fun isNameValid(guildName: String?): Boolean {
        return FunnyGuilds.Companion.getInstance().getPluginConfiguration().restrictedGuildNames.stream().noneMatch(Predicate { name: String -> name.equals(guildName, ignoreCase = true) })
    }

    fun isTagValid(guildTag: String?): Boolean {
        return FunnyGuilds.Companion.getInstance().getPluginConfiguration().restrictedGuildTags.stream().noneMatch(Predicate { tag: String -> tag.equals(guildTag, ignoreCase = true) })
    }
}