package net.dzikoysk.funnyguilds.util

import org.apache.commons.lang3.Validate
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.util.NumberConversions
import org.bukkit.util.Vector
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class FunnyBox {
    companion object {
        fun of(block: Block): FunnyBox {
            Validate.notNull(block, "block cannot be null")
            return if (getBoundingBox == null) {
                ofBlock(block)
            } else try {
                val boundingBox = getBoundingBox!!.invoke(block)
                val minX = bbGetMinX!!.invoke(boundingBox) as Double
                val minY = bbGetMinY!!.invoke(boundingBox) as Double
                val minZ = bbGetMinZ!!.invoke(boundingBox) as Double
                val maxX = bbGetMaxX!!.invoke(boundingBox) as Double
                val maxY = bbGetMaxY!!.invoke(boundingBox) as Double
                val maxZ = bbGetMaxZ!!.invoke(boundingBox) as Double
                FunnyBox(minX, minY, minZ, maxX, maxY, maxZ)
            } catch (throwable: Throwable) {
                ofBlock(block)
            }
        }

        private var getBoundingBox: MethodHandle? = null
        private var bbGetMinX: MethodHandle? = null
        private var bbGetMinY: MethodHandle? = null
        private var bbGetMinZ: MethodHandle? = null
        private var bbGetMaxX: MethodHandle? = null
        private var bbGetMaxY: MethodHandle? = null
        private var bbGetMaxZ: MethodHandle? = null
        fun of(corner1: Vector, corner2: Vector): FunnyBox {
            Validate.notNull(corner1, "corner1 cannot be null")
            Validate.notNull(corner2, "corner2 cannot be null")
            return FunnyBox(corner1.x, corner1.y, corner1.z, corner2.x, corner2.y, corner2.z)
        }

        fun of(corner1: Location?, corner2: Location?): FunnyBox {
            Validate.notNull(corner1, "corner1 cannot be null")
            Validate.notNull(corner2, "corner2 cannot be null")
            Validate.isTrue(corner1!!.world == corner2!!.world, "Locations from different worlds!")
            return FunnyBox(corner1.x, corner1.y, corner1.z, corner2.x, corner2.y, corner2.z)
        }

        fun of(center: Vector, x: Double, y: Double, z: Double): FunnyBox {
            Validate.notNull(center, "center cannot be null")
            return FunnyBox(center.x - x, center.y - y, center.z - z, center.x + x, center.y + y, center.z + z)
        }

        fun of(center: Location, x: Double, y: Double, z: Double): FunnyBox {
            Validate.notNull(center, "center cannot be null")
            return FunnyBox(center.x - x, center.y - y, center.z - z, center.x + x, center.y + y, center.z + z)
        }

        private fun ofBlock(block: Block): FunnyBox {
            return FunnyBox(block.x.toDouble(), block.y.toDouble(), block.z.toDouble(), (block.x + 1).toDouble(), (block.y + 1).toDouble(), (block.z + 1).toDouble())
        }

        private fun normalizeZeros(vector: Vector): Vector {
            var x = vector.x
            var y = vector.y
            var z = vector.z
            if (x == -0.0) x = 0.0
            if (y == -0.0) y = 0.0
            if (z == -0.0) z = 0.0
            vector.x = x
            vector.y = y
            vector.z = z
            return vector
        }

        init {
            try {
                val lookup = MethodHandles.publicLookup()
                val boundingBoxClazz = Class.forName("org.bukkit.util.BoundingBox")
                getBoundingBox = net.dzikoysk.funnyguilds.util.lookup.findVirtual(Block::class.java, "getBoundingBox", MethodType.methodType(net.dzikoysk.funnyguilds.util.boundingBoxClazz))
                bbGetMinX = net.dzikoysk.funnyguilds.util.lookup.findVirtual(net.dzikoysk.funnyguilds.util.boundingBoxClazz, "getMinX", MethodType.methodType(Double::class.javaPrimitiveType))
                bbGetMinY = net.dzikoysk.funnyguilds.util.lookup.findVirtual(net.dzikoysk.funnyguilds.util.boundingBoxClazz, "getMinY", MethodType.methodType(Double::class.javaPrimitiveType))
                bbGetMinZ = net.dzikoysk.funnyguilds.util.lookup.findVirtual(net.dzikoysk.funnyguilds.util.boundingBoxClazz, "getMinZ", MethodType.methodType(Double::class.javaPrimitiveType))
                bbGetMaxX = net.dzikoysk.funnyguilds.util.lookup.findVirtual(net.dzikoysk.funnyguilds.util.boundingBoxClazz, "getMaxX", MethodType.methodType(Double::class.javaPrimitiveType))
                bbGetMaxY = net.dzikoysk.funnyguilds.util.lookup.findVirtual(net.dzikoysk.funnyguilds.util.boundingBoxClazz, "getMaxY", MethodType.methodType(Double::class.javaPrimitiveType))
                bbGetMaxZ = net.dzikoysk.funnyguilds.util.lookup.findVirtual(net.dzikoysk.funnyguilds.util.boundingBoxClazz, "getMaxZ", MethodType.methodType(Double::class.javaPrimitiveType))
            } catch (ignored: Exception) {
                getBoundingBox = null
            }
        }
    }

    var minX = 0.0
        private set
    var minY = 0.0
        private set
    var minZ = 0.0
        private set
    var maxX = 0.0
        private set
    var maxY = 0.0
        private set
    var maxZ = 0.0
        private set

    constructor() {}
    constructor(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double) {
        resize(x1, y1, z1, x2, y2, z2)
    }

    fun resize(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): FunnyBox {
        NumberConversions.checkFinite(x1, "x1 not finite")
        NumberConversions.checkFinite(y1, "y1 not finite")
        NumberConversions.checkFinite(z1, "z1 not finite")
        NumberConversions.checkFinite(x2, "x2 not finite")
        NumberConversions.checkFinite(y2, "y2 not finite")
        NumberConversions.checkFinite(z2, "z2 not finite")
        minX = Math.min(x1, x2)
        minY = Math.min(y1, y2)
        minZ = Math.min(z1, z2)
        maxX = Math.max(x1, x2)
        maxY = Math.max(y1, y2)
        maxZ = Math.max(z1, z2)
        return this
    }

    val min: Vector
        get() = Vector(minX, minY, minZ)
    val max: Vector
        get() = Vector(maxX, maxY, maxZ)
    val widthX: Double
        get() = maxX - minX
    val widthZ: Double
        get() = maxZ - minZ
    val height: Double
        get() = maxY - minY
    val volume: Double
        get() = height * widthX * widthZ
    val centerX: Double
        get() = minX + widthX * 0.5
    val centerY: Double
        get() = minY + height * 0.5
    val centerZ: Double
        get() = minZ + widthZ * 0.5
    val center: Vector
        get() = Vector(centerX, centerY, centerZ)

    fun copy(other: FunnyBox): FunnyBox {
        Validate.notNull(other, "other cannot be null")
        return resize(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ)
    }

    fun expand(negativeX: Double, negativeY: Double, negativeZ: Double, positiveX: Double, positiveY: Double, positiveZ: Double): FunnyBox {
        if (negativeX == 0.0 && negativeY == 0.0 && negativeZ == 0.0 && positiveX == 0.0 && positiveY == 0.0 && positiveZ == 0.0) {
            return this
        }
        var newMinX = minX - negativeX
        var newMinY = minY - negativeY
        var newMinZ = minZ - negativeZ
        var newMaxX = maxX + positiveX
        var newMaxY = maxY + positiveY
        var newMaxZ = maxZ + positiveZ
        if (newMinX > newMaxX) {
            val centerX = centerX
            if (newMaxX >= centerX) {
                newMinX = newMaxX
            } else if (newMinX <= centerX) {
                newMaxX = newMinX
            } else {
                newMinX = centerX
                newMaxX = centerX
            }
        }
        if (newMinY > newMaxY) {
            val centerY = centerY
            if (newMaxY >= centerY) {
                newMinY = newMaxY
            } else if (newMinY <= centerY) {
                newMaxY = newMinY
            } else {
                newMinY = centerY
                newMaxY = centerY
            }
        }
        if (newMinZ > newMaxZ) {
            val centerZ = centerZ
            if (newMaxZ >= centerZ) {
                newMinZ = newMaxZ
            } else if (newMinZ <= centerZ) {
                newMaxZ = newMinZ
            } else {
                newMinZ = centerZ
                newMaxZ = centerZ
            }
        }
        return resize(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ)
    }

    fun expand(x: Double, y: Double, z: Double): FunnyBox {
        return this.expand(x, y, z, x, y, z)
    }

    fun expand(expansion: Vector): FunnyBox {
        Validate.notNull(expansion, "expansion cannot be null")
        val x = expansion.x
        val y = expansion.y
        val z = expansion.z
        return this.expand(x, y, z, x, y, z)
    }

    fun expand(expansion: Double): FunnyBox {
        return this.expand(expansion, expansion, expansion, expansion, expansion, expansion)
    }

    fun expand(dirX: Double, dirY: Double, dirZ: Double, expansion: Double): FunnyBox {
        if (expansion == 0.0) {
            return this
        }
        if (dirX == 0.0 && dirY == 0.0 && dirZ == 0.0) {
            return this
        }
        val negativeX = if (dirX < 0.0) -dirX * expansion else 0.0
        val negativeY = if (dirY < 0.0) -dirY * expansion else 0.0
        val negativeZ = if (dirZ < 0.0) -dirZ * expansion else 0.0
        val positiveX = if (dirX > 0.0) dirX * expansion else 0.0
        val positiveY = if (dirY > 0.0) dirY * expansion else 0.0
        val positiveZ = if (dirZ > 0.0) dirZ * expansion else 0.0
        return this.expand(negativeX, negativeY, negativeZ, positiveX, positiveY, positiveZ)
    }

    fun expand(direction: Vector, expansion: Double): FunnyBox {
        Validate.notNull(direction, "expansion cannot be null")
        return this.expand(direction.x, direction.y, direction.z, expansion)
    }

    fun expand(blockFace: BlockFace, expansion: Double): FunnyBox {
        Validate.notNull(blockFace, "blockFace cannot be null")
        return if (blockFace == BlockFace.SELF) {
            this
        } else this.expand(blockFace.direction, expansion)
    }

    fun expandDirectional(dirX: Double, dirY: Double, dirZ: Double): FunnyBox {
        return this.expand(dirX, dirY, dirZ, 1.0)
    }

    fun expandDirectional(direction: Vector): FunnyBox {
        Validate.notNull(direction, "direction cannot be null")
        return this.expand(direction.x, direction.y, direction.z, 1.0)
    }

    fun union(posX: Double, posY: Double, posZ: Double): FunnyBox {
        val newMinX = Math.min(minX, posX)
        val newMinY = Math.min(minY, posY)
        val newMinZ = Math.min(minZ, posZ)
        val newMaxX = Math.max(maxX, posX)
        val newMaxY = Math.max(maxY, posY)
        val newMaxZ = Math.max(maxZ, posZ)
        return if (newMinX == minX && newMinY == minY && newMinZ == minZ && newMaxX == maxX && newMaxY == maxY && newMaxZ == maxZ) {
            this
        } else resize(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ)
    }

    fun union(position: Vector): FunnyBox {
        Validate.notNull(position, "position cannot be null")
        return this.union(position.x, position.y, position.z)
    }

    fun union(position: Location): FunnyBox {
        Validate.notNull(position, "position cannot be null")
        return this.union(position.x, position.y, position.z)
    }

    fun union(other: FunnyBox): FunnyBox {
        Validate.notNull(other, "other cannot be null")
        if (this.contains(other)) return this
        val newMinX = Math.min(minX, other.minX)
        val newMinY = Math.min(minY, other.minY)
        val newMinZ = Math.min(minZ, other.minZ)
        val newMaxX = Math.max(maxX, other.maxX)
        val newMaxY = Math.max(maxY, other.maxY)
        val newMaxZ = Math.max(maxZ, other.maxZ)
        return resize(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ)
    }

    fun intersection(other: FunnyBox): FunnyBox {
        Validate.notNull(other, "other cannot be null")
        Validate.isTrue(this.overlaps(other), "boxes do not overlap")
        val newMinX = Math.max(minX, other.minX)
        val newMinY = Math.max(minY, other.minY)
        val newMinZ = Math.max(minZ, other.minZ)
        val newMaxX = Math.min(maxX, other.maxX)
        val newMaxY = Math.min(maxY, other.maxY)
        val newMaxZ = Math.min(maxZ, other.maxZ)
        return resize(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ)
    }

    fun shift(shiftX: Double, shiftY: Double, shiftZ: Double): FunnyBox {
        return if (shiftX == 0.0 && shiftY == 0.0 && shiftZ == 0.0) {
            this
        } else resize(
            minX + shiftX, minY + shiftY, minZ + shiftZ,
            maxX + shiftX, maxY + shiftY, maxZ + shiftZ
        )
    }

    fun shift(shift: Vector): FunnyBox {
        Validate.notNull(shift, "shift cannot be null")
        return this.shift(shift.x, shift.y, shift.z)
    }

    fun shift(shift: Location): FunnyBox {
        Validate.notNull(shift, "shift cannot be null")
        return this.shift(shift.x, shift.y, shift.z)
    }

    private fun overlaps(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double): Boolean {
        return (this.minX < maxX && this.maxX > minX
                && this.minY < maxY && this.maxY > minY
                && this.minZ < maxZ && this.maxZ > minZ)
    }

    fun overlaps(other: FunnyBox): Boolean {
        Validate.notNull(other, "other cannot be null")
        return this.overlaps(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ)
    }

    fun overlaps(min: Vector, max: Vector): Boolean {
        Validate.notNull(min, "min cannot be null")
        Validate.notNull(max, "max cannot be null")
        val x1 = min.x
        val y1 = min.y
        val z1 = min.z
        val x2 = max.x
        val y2 = max.y
        val z2 = max.z
        return this.overlaps(
            Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
            Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2)
        )
    }

    fun contains(x: Double, y: Double, z: Double): Boolean {
        return (x >= minX && x < maxX
                && y >= minY && y < maxY
                && z >= minZ && z < maxZ)
    }

    operator fun contains(position: Vector): Boolean {
        Validate.notNull(position, "position cannot be null")
        return this.contains(position.x, position.y, position.z)
    }

    private fun contains(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double): Boolean {
        return (this.minX <= minX && this.maxX >= maxX
                && this.minY <= minY && this.maxY >= maxY
                && this.minZ <= minZ && this.maxZ >= maxZ)
    }

    operator fun contains(other: FunnyBox): Boolean {
        Validate.notNull(other, "Other bounding box cannot be null")
        return this.contains(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ)
    }

    fun contains(min: Vector, max: Vector): Boolean {
        Validate.notNull(min, "min cannot be null")
        Validate.notNull(max, "max cannot be null")
        val x1 = min.x
        val y1 = min.y
        val z1 = min.z
        val x2 = max.x
        val y2 = max.y
        val z2 = max.z
        return this.contains(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2), Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))
    }

    fun rayTrace(start: Vector, direction: Vector, maxDistance: Double): RayTraceResult? {
        Validate.notNull(start, "start cannot be null")
        start.checkFinite()
        Validate.notNull(direction, "direction cannot be null")
        direction.checkFinite()
        Validate.isTrue(direction.lengthSquared() > 0, "directions's magnitude is 0")
        if (maxDistance < 0.0) return null
        val startX = start.x
        val startY = start.y
        val startZ = start.z
        val dir = normalizeZeros(direction.clone()).normalize()
        val dirX = dir.x
        val dirY = dir.y
        val dirZ = dir.z
        val divX = 1.0 / dirX
        val divY = 1.0 / dirY
        val divZ = 1.0 / dirZ
        var tMin: Double
        var tMax: Double
        var hitBlockFaceMin: BlockFace?
        var hitBlockFaceMax: BlockFace?
        if (dirX >= 0.0) {
            tMin = (minX - startX) * divX
            tMax = (maxX - startX) * divX
            hitBlockFaceMin = BlockFace.WEST
            hitBlockFaceMax = BlockFace.EAST
        } else {
            tMin = (maxX - startX) * divX
            tMax = (minX - startX) * divX
            hitBlockFaceMin = BlockFace.EAST
            hitBlockFaceMax = BlockFace.WEST
        }
        val tyMin: Double
        val tyMax: Double
        val hitBlockFaceYMin: BlockFace
        val hitBlockFaceYMax: BlockFace
        if (dirY >= 0.0) {
            tyMin = (minY - startY) * divY
            tyMax = (maxY - startY) * divY
            hitBlockFaceYMin = BlockFace.DOWN
            hitBlockFaceYMax = BlockFace.UP
        } else {
            tyMin = (maxY - startY) * divY
            tyMax = (minY - startY) * divY
            hitBlockFaceYMin = BlockFace.UP
            hitBlockFaceYMax = BlockFace.DOWN
        }
        if (tMin > tyMax || tMax < tyMin) {
            return null
        }
        if (tyMin > tMin) {
            tMin = tyMin
            hitBlockFaceMin = hitBlockFaceYMin
        }
        if (tyMax < tMax) {
            tMax = tyMax
            hitBlockFaceMax = hitBlockFaceYMax
        }
        val tzMin: Double
        val tzMax: Double
        val hitBlockFaceZMin: BlockFace
        val hitBlockFaceZMax: BlockFace
        if (dirZ >= 0.0) {
            tzMin = (minZ - startZ) * divZ
            tzMax = (maxZ - startZ) * divZ
            hitBlockFaceZMin = BlockFace.NORTH
            hitBlockFaceZMax = BlockFace.SOUTH
        } else {
            tzMin = (maxZ - startZ) * divZ
            tzMax = (minZ - startZ) * divZ
            hitBlockFaceZMin = BlockFace.SOUTH
            hitBlockFaceZMax = BlockFace.NORTH
        }
        if (tMin > tzMax || tMax < tzMin) {
            return null
        }
        if (tzMin > tMin) {
            tMin = tzMin
            hitBlockFaceMin = hitBlockFaceZMin
        }
        if (tzMax < tMax) {
            tMax = tzMax
            hitBlockFaceMax = hitBlockFaceZMax
        }
        if (tMax < 0.0) {
            return null
        }
        if (tMin > maxDistance) {
            return null
        }
        val t: Double
        val hitBlockFace: BlockFace
        if (tMin < 0.0) {
            t = tMax
            hitBlockFace = hitBlockFaceMax
        } else {
            t = tMin
            hitBlockFace = hitBlockFaceMin
        }
        val hitPosition = dir.multiply(t).add(start)
        return RayTraceResult(hitPosition, hitBlockFace)
    }

    class RayTraceResult private constructor(hitPosition: Vector, hitBlock: Block?, hitBlockFace: BlockFace?, hitEntity: Entity?) {
        private val hitPosition: Vector
        val hitBlock: Block?
        val hitBlockFace: BlockFace?
        val hitEntity: Entity?

        constructor(hitPosition: Vector) : this(hitPosition, null, null, null) {}
        constructor(hitPosition: Vector, hitBlockFace: BlockFace?) : this(hitPosition, null, hitBlockFace, null) {}
        constructor(hitPosition: Vector, hitBlock: Block?, hitBlockFace: BlockFace?) : this(hitPosition, hitBlock, hitBlockFace, null) {}
        constructor(hitPosition: Vector, hitEntity: Entity?) : this(hitPosition, null, null, hitEntity) {}
        constructor(hitPosition: Vector, hitEntity: Entity?, hitBlockFace: BlockFace?) : this(hitPosition, null, hitBlockFace, hitEntity) {}

        fun getHitPosition(): Vector {
            return hitPosition.clone()
        }

        init {
            Validate.notNull(hitPosition, "hitPosition cannot be null")
            this.hitPosition = hitPosition.clone()
            this.hitBlock = hitBlock
            this.hitBlockFace = hitBlockFace
            this.hitEntity = hitEntity
        }
    }
}