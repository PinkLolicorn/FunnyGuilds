package net.dzikoysk.funnyguilds.basic.guild

import net.dzikoysk.funnyguilds.basic.AbstractBasic
import net.dzikoysk.funnyguilds.basic.BasicType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector

class Region : AbstractBasic {
    private override var name: String
    private var guild: Guild? = null
    var center: Location? = null
        private set
    var world: World? = null
        private set
    private var size = 0
    var enlarge = 0
        set(i) {
            field = i
            super.markChanged()
        }
    var firstCorner: Location? = null
        private set
    var secondCorner: Location? = null
        private set

    private constructor(name: String) {
        this.name = name
    }

    constructor(guild: Guild, loc: Location, size: Int) {
        this.guild = guild
        name = guild.name!!
        world = loc.world
        center = loc
        this.size = size
        update()
    }

    @Synchronized
    fun update() {
        super.markChanged()
        if (center == null) {
            return
        }
        if (size < 1) {
            return
        }
        if (world == null) {
            world = Bukkit.getWorlds()[0]
        }
        if (world != null) {
            val lx = center!!.blockX + size
            val lz = center!!.blockZ + size
            val px = center!!.blockX - size
            val pz = center!!.blockZ - size
            val l = Vector(lx, 0, lz)
            val p = Vector(px, world!!.maxHeight, pz)
            firstCorner = l.toLocation(world!!)
            secondCorner = p.toLocation(world!!)
        }
    }

    fun delete() {
        RegionUtils.removeRegion(this)
        guild = null
        world = null
        center = null
        firstCorner = null
        secondCorner = null
    }

    fun isIn(loc: Location?): Boolean {
        if (loc == null || firstCorner == null || secondCorner == null) {
            return false
        }
        if (center!!.world != loc.world) {
            return false
        }
        if (loc.blockX > lowerX && loc.blockX < upperX) {
            if (loc.blockY > lowerY && loc.blockY < upperY) {
                return loc.blockZ > lowerZ && loc.blockZ < upperZ
            }
        }
        return false
    }

    private fun compareCoordinates(upper: Boolean, a: Int, b: Int): Int {
        return if (upper) {
            Math.max(b, a)
        } else {
            Math.min(a, b)
        }
    }

    fun setName(s: String) {
        name = s
        super.markChanged()
    }

    fun setGuild(guild: Guild?) {
        this.guild = guild
        super.markChanged()
    }

    fun setCenter(loc: Location) {
        center = loc
        world = loc.world
        update()
    }

    fun setSize(i: Int) {
        size = i
        update()
    }

    fun getGuild(): Guild? {
        return guild
    }

    val heart: Location
        get() = center!!.block.getRelative(BlockFace.DOWN).location

    fun getSize(): Int {
        return size
    }

    val upperX: Int
        get() = compareCoordinates(true, firstCorner!!.blockX, secondCorner!!.blockX)
    val upperY: Int
        get() = compareCoordinates(true, firstCorner!!.blockY, secondCorner!!.blockY)
    val upperZ: Int
        get() = compareCoordinates(true, firstCorner!!.blockZ, secondCorner!!.blockZ)
    val lowerX: Int
        get() = compareCoordinates(false, firstCorner!!.blockX, secondCorner!!.blockX)
    val lowerY: Int
        get() = compareCoordinates(false, firstCorner!!.blockY, secondCorner!!.blockY)
    val lowerZ: Int
        get() = compareCoordinates(false, firstCorner!!.blockZ, secondCorner!!.blockZ)
    override val type: BasicType?
        get() = BasicType.REGION

    override fun getName(): String? {
        return name
    }

    override fun toString(): String {
        return name
    }

    companion object {
        fun getOrCreate(name: String): Region? {
            for (region in RegionUtils.getRegions()) {
                if (region!!.name != null && region!!.name.equals(name, ignoreCase = true)) {
                    return region
                }
            }
            return Region(name)
        }
    }
}