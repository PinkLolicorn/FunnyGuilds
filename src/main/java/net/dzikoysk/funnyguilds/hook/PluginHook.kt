package net.dzikoysk.funnyguilds.hook

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.hook.worldedit.WorldEdit6Hook
import net.dzikoysk.funnyguilds.hook.worldedit.WorldEdit7Hook
import net.dzikoysk.funnyguilds.hook.worldedit.WorldEditHook
import net.dzikoysk.funnyguilds.hook.worldguard.WorldGuard6Hook
import net.dzikoysk.funnyguilds.hook.worldguard.WorldGuard7Hook
import net.dzikoysk.funnyguilds.hook.worldguard.WorldGuardHook
import org.bukkit.Bukkit
import java.util.function.BooleanSupplier

object PluginHook {
    const val PLUGIN_FUNNYTAB = "FunnyTab"
    const val PLUGIN_WORLDGUARD = "WorldGuard"
    const val PLUGIN_WORLDEDIT = "WorldEdit"
    const val PLUGIN_VAULT = "Vault"
    const val PLUGIN_PLACEHOLDERAPI = "PlaceholderAPI"
    const val PLUGIN_BUNGEETABLISTPLUS = "BungeeTabListPlus"
    const val PLUGIN_MVDWPLACEHOLDERAPI = "MVdWPlaceholderAPI"
    const val PLUGIN_LEADERHEADS = "LeaderHeads"
    var WORLD_GUARD: WorldGuardHook? = null
    var WORLD_EDIT: WorldEditHook? = null
    private val HOOK_LIST: MutableList<String?> = ArrayList()
    fun earlyInit() {
        tryInit(PLUGIN_WORLDGUARD, {
            try {
                Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry")
                Class.forName("com.sk89q.worldguard.protection.flags.Flag")
                val worldGuardVersion = Bukkit.getPluginManager().getPlugin("WorldGuard")!!.description.version
                WORLD_GUARD = if (worldGuardVersion.startsWith("7")) WorldGuard7Hook() else WorldGuard6Hook()
                WORLD_GUARD!!.init()
                return@tryInit true
            } catch (exception: ClassNotFoundException) {
                FunnyGuilds.Companion.getPluginLogger().warning("FunnyGuilds supports only WorldGuard v6.2 or newer")
                return@tryInit false
            }
        })
    }

    fun init() {
        tryInit(PLUGIN_FUNNYTAB, {
            FunnyTabHook.initFunnyDisabler()
            true
        }, false)
        tryInit(PLUGIN_WORLDEDIT, {
            try {
                Class.forName("com.sk89q.worldedit.Vector")
                WORLD_EDIT = WorldEdit6Hook()
            } catch (ignored: ClassNotFoundException) {
                WORLD_EDIT = WorldEdit7Hook()
            }
            WORLD_EDIT!!.init()
            true
        })
        tryInit(PLUGIN_BUNGEETABLISTPLUS, {
            try {
                Class.forName("codecrafter47.bungeetablistplus.api.bukkit.Variable")
                BungeeTabListPlusHook.initVariableHook()
                return@tryInit true
            } catch (exception: ClassNotFoundException) {
                return@tryInit false
            }
        })
        tryInit(PLUGIN_MVDWPLACEHOLDERAPI, {
            try {
                Class.forName("be.maximvdw.placeholderapi.PlaceholderReplacer")
                MVdWPlaceholderAPIHook.initPlaceholderHook()
                return@tryInit true
            } catch (exception: ClassNotFoundException) {
                return@tryInit false
            }
        })
        tryInit(PLUGIN_VAULT, {
            VaultHook.initHooks()
            true
        })
        tryInit(PLUGIN_PLACEHOLDERAPI, {
            PlaceholderAPIHook.initPlaceholderHook()
            true
        })
        tryInit(PLUGIN_LEADERHEADS, {
            LeaderHeadsHook.initLeaderHeadsHook()
            true
        })
    }

    @JvmOverloads
    fun tryInit(plugin: String, init: BooleanSupplier, notifyIfMissing: Boolean = true) {
        if (Bukkit.getPluginManager().getPlugin(plugin) != null) {
            if (!init.asBoolean) {
                return
            }
            HOOK_LIST.add(plugin)
        } else if (notifyIfMissing) {
            FunnyGuilds.Companion.getPluginLogger().info("$plugin plugin could not be found, some features may not be available")
        }
    }

    fun isPresent(plugin: String?): Boolean {
        return HOOK_LIST.contains(plugin)
    }
}