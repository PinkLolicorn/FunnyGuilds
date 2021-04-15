package net.dzikoysk.funnyguilds.basic.guild

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion
import net.dzikoysk.funnyguilds.data.database.SQLDataModel
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils
import org.bukkit.Location
import org.panda_lang.utilities.commons.function.Option
import java.util.concurrent.ConcurrentHashMap

object RegionUtils {
    val REGION_LIST: MutableSet<Region?> = ConcurrentHashMap.newKeySet()
    val regions: Set<Region?>
        get() = HashSet(REGION_LIST)

    operator fun get(name: String?): Region? {
        if (name == null) {
            return null
        }
        for (region in REGION_LIST) {
            if (name.equals(region!!.name, ignoreCase = true)) {
                return region
            }
        }
        return null
    }

    fun isIn(location: Location?): Boolean {
        for (region in REGION_LIST) {
            if (region!!.isIn(location)) {
                return true
            }
        }
        return false
    }

    fun getAt(location: Location?): Region? {
        for (region in REGION_LIST) {
            if (region!!.isIn(location)) {
                return region
            }
        }
        return null
    }

    fun getAtOpt(location: Location?): Option<Region?> {
        return Option.of(getAt(location))
    }

    fun isNear(center: Location?): Boolean {
        if (center == null) {
            return false
        }
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        var size = config.regionSize
        if (config.enlargeItems != null) {
            size += config.enlargeItems!!.size * config.enlargeSize
        }
        val requiredDistance = 2 * size + config.regionMinDistance
        for (region in REGION_LIST) {
            if (region.getCenter() == null) {
                continue
            }
            if (region.getCenter() == center) {
                continue
            }
            if (center.world != region.getCenter().world) {
                continue
            }
            if (LocationUtils.flatDistance(center, region.getCenter()) < requiredDistance) {
                return true
            }
        }
        return false
    }

    fun delete(region: Region?) {
        if (region == null) {
            return
        }
        if (FunnyGuilds.Companion.getInstance().getDataModel() is FlatDataModel) {
            val dataModel = FunnyGuilds.Companion.getInstance().getDataModel() as FlatDataModel
            dataModel.getRegionFile(region).delete()
        }
        if (FunnyGuilds.Companion.getInstance().getDataModel() is SQLDataModel) {
            DatabaseRegion.delete(region)
        }
        region.delete()
    }

    fun getNames(lsg: Collection<Region?>?): MutableList<String?> {
        val list: MutableList<String?> = ArrayList()
        if (lsg == null) {
            return list
        }
        for (r in lsg) {
            if (r != null && r.name != null) {
                list.add(r.name)
            }
        }
        return list
    }

    fun addRegion(region: Region?) {
        REGION_LIST.add(region)
    }

    fun removeRegion(region: Region?) {
        REGION_LIST.remove(region)
    }

    fun toString(region: Region?): String? {
        return if (region != null) region.name else "null"
    }
}