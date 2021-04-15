package net.dzikoysk.funnyguilds.util.commons.bukkit

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.panda_lang.utilities.commons.function.QuadFunction
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Supplier

object SpaceUtils {
    fun chance(chance: Double): Boolean {
        return chance >= 100 || chance > ThreadLocalRandom.current().nextDouble(0.0, 100.0)
    }

    fun sphere(sphereCenter: Location, radius: Int, height: Int, hollow: Boolean, sphere: Boolean, plusY: Int): List<Location> {
        return mapSphereCoordinates(
            sphereCenter,
            radius,
            height,
            plusY,
            hollow,
            sphere,
            Supplier { ArrayList() },
            QuadFunction<World?, Int, Int, Int, Location> { world: World?, x: Int, y: Int, z: Int -> Location(world, x.toDouble(), y.toDouble(), z.toDouble()) })
    }

    fun sphereBlocks(sphereLocation: Location, radius: Int, height: Int, plusY: Int, hollow: Boolean, sphere: Boolean): MutableList<Block> {
        return mapSphereCoordinates(sphereLocation, radius, height, plusY, hollow, sphere, { ArrayList() }) { obj: World?, i: Int?, i1: Int?, i2: Int? ->
            obj!!.getBlockAt(
                i!!, i1!!, i2!!
            )
        }
    }

    private fun <T, C : Collection<T>?> mapSphereCoordinates(
        sphereCenter: Location,
        radius: Int, height: Int, plusY: Int,
        hollow: Boolean, sphere: Boolean,
        collectionSupplier: Supplier<C>,
        coordinateMapper: QuadFunction<World?, Int, Int, Int, T>
    ): C {
        val result = collectionSupplier.get()
        val world = sphereCenter.world
        val centerX = sphereCenter.blockX
        val centerY = sphereCenter.blockY
        val centerZ = sphereCenter.blockZ
        for (x in centerX - radius..centerX + radius) {
            for (z in centerZ - radius..centerZ + radius) {
                for (y in (if (sphere) centerY - radius else centerY) until if (sphere) centerY + radius else centerY + height) {
                    val dist = ((centerX - x) * (centerX - x) + (centerZ - z) * (centerZ - z) + if (sphere) (centerY - y) * (centerY - y) else 0).toDouble()
                    if (dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))) {
                        result.add(coordinateMapper.apply(world, x, y + plusY, z))
                    }
                }
            }
        }
        return result
    }
}