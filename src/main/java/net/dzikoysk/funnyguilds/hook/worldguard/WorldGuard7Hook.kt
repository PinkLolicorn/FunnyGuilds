package net.dzikoysk.funnyguilds.hook.worldguard

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.ApplicableRegionSet
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import net.dzikoysk.funnyguilds.FunnyGuilds
import java.util.stream.Collectors

net.dzikoysk.funnyguilds.data .configs.PluginConfiguration
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

org.bukkit.Location
import java.lang.StackTraceElement

class WorldGuard7Hook : WorldGuardHook {
    private var worldGuard: WorldGuard? = null
    private var noPointsFlag: StateFlag? = null
    override fun init() {
        worldGuard = WorldGuard.getInstance()
        noPointsFlag = StateFlag("fg-no-points", false)
        worldGuard.getFlagRegistry().register(noPointsFlag)
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
        if (location == null || worldGuard == null) {
            return null
        }
        val regionManager = worldGuard!!.platform.regionContainer[BukkitAdapter.adapt(location.world)] ?: return null
        return regionManager.getApplicableRegions(BlockVector3.at(location.x, location.y, location.z))
    }

    override fun getRegionNames(location: Location?): List<String?>? {
        val regionSet = getRegionSet(location)
        return regionSet?.regions?.stream()?.map { obj: ProtectedRegion -> obj.id }?.collect(Collectors.toList())
    }
}