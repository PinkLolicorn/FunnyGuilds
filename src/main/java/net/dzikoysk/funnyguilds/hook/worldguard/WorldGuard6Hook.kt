package net.dzikoysk.funnyguilds.hook.worldguard

import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.ApplicableRegionSet
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry
import com.sk89q.worldguard.protection.managers.RegionManager
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.util.nms.Reflections
import org.bukkit.Location
import org.bukkit.World
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.stream.Collectors

class WorldGuard6Hook : WorldGuardHook {
    companion object {
        private val GET_REGION_MANAGER: MethodHandle? = null
        private val GET_APPLICABLE_REGIONS: MethodHandle? = null

        init {
            try {
                val lookup = MethodHandles.lookup()
                GET_REGION_MANAGER = net.dzikoysk.funnyguilds.hook.worldguard.lookup.findVirtual(
                    WorldGuardPlugin::class.java, "getRegionManager",
                    MethodType.methodType(RegionManager::class.java, World::class.java)
                )
                GET_APPLICABLE_REGIONS = net.dzikoysk.funnyguilds.hook.worldguard.lookup.findVirtual(
                    RegionManager::class.java, "getApplicableRegions",
                    MethodType.methodType(ApplicableRegionSet::class.java, Location::class.java)
                )
            } catch (ex: NoSuchMethodException) {
                throw RuntimeException("Could not properly initialize WorldGuard 6.0+ hook!", ex)
            } catch (ex: IllegalAccessException) {
                throw RuntimeException("Could not properly initialize WorldGuard 6.0+ hook!", ex)
            }
        }
    }

    private var getInstance: Method? = null
    private var getFlagRegistry: Method? = null
    private var worldGuard: WorldGuardPlugin? = null
    private var noPointsFlag: StateFlag? = null
    override fun init() {
        worldGuard = WorldGuardPlugin.inst()
        noPointsFlag = StateFlag("fg-no-points", false)
        val worldGuardPluginClass = Reflections.getClass("com.sk89q.worldguard.bukkit.WorldGuardPlugin")
        getInstance = Reflections.getMethod(worldGuardPluginClass, "inst")
        getFlagRegistry = Reflections.getMethod(worldGuardPluginClass, "getFlagRegistry")
        try {
            (getFlagRegistry!!.invoke(getInstance!!.invoke(null)) as FlagRegistry).register(noPointsFlag)
        } catch (ex: FlagConflictException) {
            FunnyGuilds.Companion.getPluginLogger().error("An error occurred while registering an \"fg-no-points\" worldguard flag", ex)
        } catch (ex: IllegalAccessException) {
            FunnyGuilds.Companion.getPluginLogger().error("An error occurred while registering an \"fg-no-points\" worldguard flag", ex)
        } catch (ex: IllegalArgumentException) {
            FunnyGuilds.Companion.getPluginLogger().error("An error occurred while registering an \"fg-no-points\" worldguard flag", ex)
        } catch (ex: InvocationTargetException) {
            FunnyGuilds.Companion.getPluginLogger().error("An error occurred while registering an \"fg-no-points\" worldguard flag", ex)
        }
    }

    override fun isInNonPointsRegion(location: Location?): Boolean {
        val regionSet = getRegionSet(location) ?: return false
        for (region in regionSet) {
            if (region.getFlag(noPointsFlag) == StateFlag.State.ALLOW) {
                return true
            }
        }
        return false
    }

    override fun isInIgnoredRegion(location: Location?): Boolean {
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        val regionSet = getRegionSet(location) ?: return false
        return regionSet.regions
            .stream()
            .anyMatch { region: ProtectedRegion -> config.assistsRegionsIgnored.contains(region.id) }
    }

    override fun isInRegion(location: Location?): Boolean {
        val regionSet = getRegionSet(location) ?: return false
        return regionSet.size() != 0
    }

    override fun getRegionSet(location: Location?): ApplicableRegionSet? {
        return if (location == null || worldGuard == null) {
            null
        } else try {
            val regionManager = GET_REGION_MANAGER!!.invokeExact(worldGuard, location.world) as RegionManager
            GET_APPLICABLE_REGIONS!!.invokeExact(regionManager, location) as ApplicableRegionSet
        } catch (ex: Throwable) {
            null
        }
    }

    override fun getRegionNames(location: Location?): List<String?>? {
        val regionSet = getRegionSet(location)
        return regionSet?.regions?.stream()?.map { obj: ProtectedRegion -> obj.id }?.collect(Collectors.toList())
    }
}