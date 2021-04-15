package net.dzikoysk.funnyguilds.util.nms

import net.dzikoysk.funnyguilds.FunnyGuilds
import org.apache.commons.lang3.Validate
import org.bukkit.entity.Player
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException

class PacketCreator private constructor(private val packetClass: Class<*>?) {
    private val packetConstructor: Constructor<*>?
    private val packetFields: MutableMap<String, Field>
    var packet: Any? = null
        private set

    fun create(): PacketCreator {
        try {
            packet = packetConstructor!!.newInstance()
        } catch (exception: InstantiationException) {
            FunnyGuilds.Companion.getPluginLogger().error("PacketCreator error", exception)
        } catch (exception: IllegalAccessException) {
            FunnyGuilds.Companion.getPluginLogger().error("PacketCreator error", exception)
        } catch (exception: IllegalArgumentException) {
            FunnyGuilds.Companion.getPluginLogger().error("PacketCreator error", exception)
        } catch (exception: InvocationTargetException) {
            FunnyGuilds.Companion.getPluginLogger().error("PacketCreator error", exception)
        }
        return this
    }

    fun withField(fieldName: String, value: Any): PacketCreator {
        Validate.notNull(value, "Value cannot be NULL!")
        if (packet == null) {
            throw RuntimeException("Tried to set field on non-existing packet instance!")
        }
        try {
            val field = packetFields[fieldName]
            field!![packet] = value
        } catch (ex: IllegalAccessException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not retrieve field from given packet class", ex)
        }
        return this
    }

    fun withField(fieldName: String, value: Any, fieldType: Class<*>): PacketCreator {
        Validate.notNull(value, "Value cannot be NULL!")
        if (packet == null) {
            throw RuntimeException("Tried to set field on non-existing packet instance!")
        }
        try {
            val field = packetFields[fieldName]
            if (!fieldType.isAssignableFrom(field!!.type)) {
                FunnyGuilds.Companion.getPluginLogger().error("Given fieldType is not assignable from found field's type")
            }
            field[packet] = value
        } catch (ex: IllegalAccessException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not retrieve field from given packet class", ex)
        }
        return this
    }

    fun send(players: Collection<Player?>?) {
        PacketSender.sendPacket(players, packet)
    }

    companion object {
        private val PACKET_CREATOR_CACHE: MutableMap<String, ThreadLocal<PacketCreator>?> = HashMap()
        fun of(packetClassName: String): PacketCreator {
            var creator = PACKET_CREATOR_CACHE[packetClassName]
            if (creator == null) {
                val packetClass = Reflections.getNMSClass(packetClassName)
                creator = ThreadLocal.withInitial { PacketCreator(packetClass) }
                PACKET_CREATOR_CACHE[packetClassName] = creator
            }
            return creator!!.get()
        }

        fun of(packetClass: Class<*>): PacketCreator {
            var creator = PACKET_CREATOR_CACHE[packetClass.name]
            if (creator == null) {
                creator = ThreadLocal.withInitial { PacketCreator(packetClass) }
                PACKET_CREATOR_CACHE[packetClass.name] = creator
            }
            return creator!!.get()
        }
    }

    init {
        packetConstructor = Reflections.getConstructor(packetClass)
        packetFields = HashMap(packetClass!!.declaredFields.size)
        for (field in packetClass.declaredFields) {
            field.isAccessible = true
            packetFields[field.name] = field
        }
    }
}