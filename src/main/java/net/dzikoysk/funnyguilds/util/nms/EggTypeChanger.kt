package net.dzikoysk.funnyguilds.util.nms

import net.dzikoysk.funnyguilds.FunnyGuilds
import org.bukkit.entity.EntityType
import org.bukkit.inventory.meta.ItemMeta
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object EggTypeChanger {
    private var spawnEggMetaClass: Class<*>? = null
    private var setSpawnedTypeMethod: Method? = null
    fun needsSpawnEggMeta(): Boolean {
        return spawnEggMetaClass != null
    }

    fun applyChanges(meta: ItemMeta, type: EntityType?) {
        val implementations = meta.javaClass.interfaces
        if (implementations.size == 0 || implementations[0] != spawnEggMetaClass) {
            return
        }
        try {
            setSpawnedTypeMethod!!.invoke(meta, type)
        } catch (ex: IllegalAccessException) {
            FunnyGuilds.Companion.getPluginLogger().error("Failed to set entity type for SpawnEggMeta object", ex)
        } catch (ex: IllegalArgumentException) {
            FunnyGuilds.Companion.getPluginLogger().error("Failed to set entity type for SpawnEggMeta object", ex)
        } catch (ex: InvocationTargetException) {
            FunnyGuilds.Companion.getPluginLogger().error("Failed to set entity type for SpawnEggMeta object", ex)
        }
    }

    init {
        try {
            spawnEggMetaClass = Class.forName("org.bukkit.inventory.meta.SpawnEggMeta")
        } catch (ignored: ClassNotFoundException) {
            spawnEggMetaClass = null
        }
        if (spawnEggMetaClass != null) {
            try {
                setSpawnedTypeMethod = spawnEggMetaClass!!.getMethod("setSpawnedType", EntityType::class.java)
            } catch (ignored: NoSuchMethodException) {
            } catch (ignored: SecurityException) {
            }
        }
    }
}