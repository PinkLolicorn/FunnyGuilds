package net.dzikoysk.funnyguilds.util.nms

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.Guild
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

object GuildEntityHelper {
    private val SPAWN_ENTITY_CONSTRUCTOR: Constructor<*>? = null
    private val SPAWN_ENTITY_LIVING_CONSTRUCTOR: Constructor<*>? = null
    private val DESPAWN_ENTITY_CONSTRUCTOR: Constructor<*>? = null
    private val ENTITY_CONSTRUCTOR: Constructor<*>? = null
    private val SET_LOCATION: Method? = null
    private val GET_ID: Method? = null
    private val ENTITY_MAP: MutableMap<Guild?, Int> = ConcurrentHashMap()
    private val ID_MAP: MutableMap<Int?, Any> = HashMap()
    private val OBJECT_TYPE: ObjectType? = null
    val guildEntities: Map<Guild?, Int>
        get() = ENTITY_MAP

    @Throws(Exception::class)
    private fun createSpawnGuildHeartPacket(loc: Location): Int {
        val world = Reflections.getHandle(loc.world)
        val entity: Any
        when (Reflections.SERVER_VERSION) {
            "v1_14_R1", "v1_15_R1", "v1_16_R1", "v1_16_R2", "v1_16_R3" -> entity = ENTITY_CONSTRUCTOR!!.newInstance(world, loc.x, loc.y, loc.z)
            else -> {
                entity = ENTITY_CONSTRUCTOR!!.newInstance(world)
                SET_LOCATION!!.invoke(entity, loc.x, loc.y, loc.z, 0, 0)
            }
        }
        val packet: Any
        packet = if (OBJECT_TYPE == null) {
            SPAWN_ENTITY_LIVING_CONSTRUCTOR!!.newInstance(entity)
        } else {
            SPAWN_ENTITY_CONSTRUCTOR!!.newInstance(entity, OBJECT_TYPE.objectID)
        }
        val id = GET_ID!!.invoke(entity) as Int
        ID_MAP[id] = packet
        return id
    }

    @Throws(Exception::class)
    private fun createDespawnGuildHeartPacket(id: Int): Any {
        return DESPAWN_ENTITY_CONSTRUCTOR!!.newInstance(*intArrayOf(id))
    }

    fun spawnGuildHeart(guild: Guild?) {
        spawnGuildHeart(guild, *Bukkit.getOnlinePlayers().toTypedArray())
    }

    fun spawnGuildHeart(guild: Guild?, vararg players: Player?) {
        try {
            val value: Any?
            if (!ENTITY_MAP.containsKey(guild)) {
                val region = guild.getRegion() ?: return
                val center = region.center ?: return
                val id = createSpawnGuildHeartPacket(center.clone().add(0.5, -1.0, 0.5))
                value = ID_MAP[id]
                ENTITY_MAP[guild] = id
            } else {
                value = ID_MAP[ENTITY_MAP[guild]]
            }
            PacketSender.sendPacket(players, value)
        } catch (exception: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not spawn guild heart", exception)
        }
    }

    fun despawnGuildHeart(guild: Guild?) {
        try {
            if (!ENTITY_MAP.containsKey(guild)) {
                return
            }
            val id = ENTITY_MAP[guild]!!
            ID_MAP.remove(id)
            ENTITY_MAP.remove(guild)
            val o = createDespawnGuildHeartPacket(id)
            PacketSender.sendPacket(Bukkit.getOnlinePlayers(), o)
        } catch (exception: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not despawn guild heart", exception)
        }
    }

    fun despawnGuildHeart(guild: Guild?, vararg players: Player?) {
        try {
            if (!ENTITY_MAP.containsKey(guild)) {
                return
            }
            val id = ENTITY_MAP[guild]!!
            val o = createDespawnGuildHeartPacket(id)
            PacketSender.sendPacket(players, o)
        } catch (exception: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not despawn guild heart", exception)
        }
    }

    fun despawnGuildHearts() {
        for (guild in GuildUtils.getGuilds()) {
            despawnGuildHeart(guild)
        }
    }

    init {
        val entityType: EntityType = FunnyGuilds.Companion.getInstance().getPluginConfiguration().createEntityType
        val entityTypeName = if (net.dzikoysk.funnyguilds.util.nms.entityType == null) "EnderCrystal" else net.dzikoysk.funnyguilds.util.nms.entityType.getEntityClass().getSimpleName()
        val generalEntityClass = Reflections.getNMSClass("Entity")
        val entityLivingClass = Reflections.getNMSClass("EntityLiving")
        val entityClass = Reflections.getNMSClass("Entity" + net.dzikoysk.funnyguilds.util.nms.entityTypeName)
        val spawnEntityClass = Reflections.getNMSClass("PacketPlayOutSpawnEntity")
        val spawnEntityLivingClass = Reflections.getNMSClass("PacketPlayOutSpawnEntityLiving")
        val despawnEntityClass = Reflections.getNMSClass("PacketPlayOutEntityDestroy")
        val craftWorldClass = Reflections.getNMSClass("World")
        SPAWN_ENTITY_CONSTRUCTOR = Reflections.getConstructor(net.dzikoysk.funnyguilds.util.nms.spawnEntityClass, net.dzikoysk.funnyguilds.util.nms.generalEntityClass, Int::class.javaPrimitiveType)
        SPAWN_ENTITY_LIVING_CONSTRUCTOR = Reflections.getConstructor(net.dzikoysk.funnyguilds.util.nms.spawnEntityLivingClass, net.dzikoysk.funnyguilds.util.nms.entityLivingClass)
        DESPAWN_ENTITY_CONSTRUCTOR = Reflections.getConstructor(net.dzikoysk.funnyguilds.util.nms.despawnEntityClass, IntArray::class.java)
        when (Reflections.SERVER_VERSION) {
            "v1_14_R1", "v1_15_R1", "v1_16_R1", "v1_16_R2", "v1_16_R3" -> ENTITY_CONSTRUCTOR = Reflections.getConstructor(
                net.dzikoysk.funnyguilds.util.nms.entityClass,
                net.dzikoysk.funnyguilds.util.nms.craftWorldClass,
                Double::class.javaPrimitiveType,
                Double::class.javaPrimitiveType,
                Double::class.javaPrimitiveType
            )
            else -> ENTITY_CONSTRUCTOR = Reflections.getConstructor(net.dzikoysk.funnyguilds.util.nms.entityClass, net.dzikoysk.funnyguilds.util.nms.craftWorldClass)
        }
        SET_LOCATION = Reflections.getMethod(
            net.dzikoysk.funnyguilds.util.nms.entityClass,
            "setLocation",
            Double::class.javaPrimitiveType!!,
            Double::class.javaPrimitiveType!!,
            Double::class.javaPrimitiveType!!,
            Float::class.javaPrimitiveType!!,
            Float::class.javaPrimitiveType!!
        )
        GET_ID = Reflections.getMethod(net.dzikoysk.funnyguilds.util.nms.entityClass, "getId")
        OBJECT_TYPE =
            if (net.dzikoysk.funnyguilds.util.nms.entityLivingClass.isAssignableFrom(net.dzikoysk.funnyguilds.util.nms.entityClass)) null else ObjectType.Companion.get(net.dzikoysk.funnyguilds.util.nms.entityType)
    }
}