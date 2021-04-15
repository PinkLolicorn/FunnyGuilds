package net.dzikoysk.funnyguilds.util.nms

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.util.commons.SafeUtils
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Entity
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

object Reflections {
    val SERVER_VERSION = Bukkit.getServer().javaClass.getPackage().name.split("\\.".toRegex()).toTypedArray()[3]
    val USE_PRE_13_METHODS = SERVER_VERSION.split("_".toRegex()).toTypedArray()[1].toInt() < 13
    val USE_PRE_12_METHODS = SERVER_VERSION.split("_".toRegex()).toTypedArray()[1].toInt() < 12
    val USE_PRE_9_METHODS = SERVER_VERSION.split("_".toRegex()).toTypedArray()[1].toInt() < 9
    private val CLASS_CACHE: MutableMap<String, Class<*>?> = HashMap()
    private val FIELD_CACHE: MutableMap<String, Field?> = HashMap()
    private val FIELD_ACCESSOR_CACHE: MutableMap<String, FieldAccessor<*>> = HashMap()
    private val METHOD_CACHE: MutableMap<String, Method> = HashMap()
    private val INVALID_CLASS: Class<*> = InvalidMarker::class.java
    private val INVALID_METHOD = SafeUtils.safeInit { InvalidMarker::class.java.getDeclaredMethod("invalidMethodMaker") }
    private val INVALID_FIELD = SafeUtils.safeInit { InvalidMarker::class.java.getDeclaredField("invalidFieldMarker") }
    private val INVALID_FIELD_ACCESSOR: FieldAccessor<*> = getField(INVALID_CLASS, Void::class.java, 0)
    fun getClassOmitCache(className: String): Class<*>? {
        CLASS_CACHE.remove(className)
        return getClass(className)
    }

    fun getClass(className: String): Class<*>? {
        var c = CLASS_CACHE[className]
        if (c != null) {
            return if (c != INVALID_CLASS) c else null
        }
        try {
            c = Class.forName(className)
            CLASS_CACHE[className] = c
        } catch (ex: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not retrieve class", ex)
            CLASS_CACHE[className] = INVALID_CLASS
        }
        return c
    }

    fun getNMSClass(name: String): Class<*>? {
        return getClass("net.minecraft.server." + SERVER_VERSION + "." + name)
    }

    fun getCraftBukkitClass(name: String): Class<*>? {
        return getClass("org.bukkit.craftbukkit." + SERVER_VERSION + "." + name)
    }

    fun getBukkitClass(name: String): Class<*>? {
        return getClass("org.bukkit.$name")
    }

    fun getHandle(entity: Entity): Any? {
        return try {
            getMethod(entity.javaClass, "getHandle")!!.invoke(entity)
        } catch (ex: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not get entity handle", ex)
            null
        }
    }

    fun getHandle(world: World?): Any? {
        return try {
            getMethod(world!!.javaClass, "getHandle")!!.invoke(world)
        } catch (ex: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not get world handle", ex)
            null
        }
    }

    private fun constructFieldCacheKey(cl: Class<*>?, fieldName: String): String {
        return cl!!.name + "." + fieldName
    }

    fun getField(cl: Class<*>?, fieldName: String): Field? {
        val cacheKey = constructFieldCacheKey(cl, fieldName)
        var field = FIELD_CACHE[cacheKey]
        if (field != null) {
            return if (field !== INVALID_FIELD) field else null
        }
        try {
            field = cl!!.getDeclaredField(fieldName)
            FIELD_CACHE[cacheKey] = field
        } catch (ex: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not retrieve field", ex)
            FIELD_CACHE[cacheKey] = INVALID_FIELD
        }
        return field
    }

    fun <T> getField(target: Class<*>?, fieldType: Class<T>, index: Int): FieldAccessor<T> {
        return getField(target, null, fieldType, index)
    }

    private fun <T> getField(target: Class<*>?, name: String?, fieldType: Class<T>, index: Int): FieldAccessor<T> {
        var index = index
        val cacheKey = target!!.name + "." + (name ?: "NONE") + "." + fieldType.name + "." + index
        var output = FIELD_ACCESSOR_CACHE[cacheKey] as FieldAccessor<T>?
        if (output != null) {
            require(!(output === INVALID_FIELD_ACCESSOR)) { "Cannot find field with type $fieldType" }
            return output
        }
        for (field in target.declaredFields) {
            if ((name == null || field.name == name) && fieldType.isAssignableFrom(field.type) && index-- <= 0) {
                field.isAccessible = true
                output = object : FieldAccessor<T> {
                    override operator fun get(target: Any?): T {
                        return try {
                            field[target] as T
                        } catch (e: IllegalAccessException) {
                            throw RuntimeException("Cannot access reflection.", e)
                        }
                    }

                    override operator fun set(target: Any?, value: Any?) {
                        try {
                            field[target] = value
                        } catch (e: IllegalAccessException) {
                            throw RuntimeException("Cannot access reflection.", e)
                        }
                    }

                    override fun hasField(target: Any): Boolean {
                        return field.declaringClass.isAssignableFrom(target.javaClass)
                    }
                }
                break
            }
        }
        if (output == null && target.superclass != null) {
            output = getField(target.superclass, name, fieldType, index)
        }
        FIELD_ACCESSOR_CACHE[cacheKey] = output ?: INVALID_FIELD_ACCESSOR
        requireNotNull(output) { "Cannot find field with type $fieldType" }
        return output
    }

    fun getPrivateField(cl: Class<*>?, fieldName: String): Field? {
        val cacheKey = constructFieldCacheKey(cl, fieldName)
        var c = FIELD_CACHE[cacheKey]
        if (c != null) {
            return if (c !== INVALID_FIELD) c else null
        }
        try {
            c = cl!!.getDeclaredField(fieldName)
            c.isAccessible = true
            FIELD_CACHE[cacheKey] = c
        } catch (ex: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not retrieve field", ex)
            FIELD_CACHE[cacheKey] = INVALID_FIELD
        }
        return c
    }

    fun getMethod(cl: Class<*>?, method: String, vararg args: Class<*>): Method? {
        val cacheKey = cl!!.name + "." + method + "." + if (args == null) "NONE" else Arrays.toString(args)
        var output = METHOD_CACHE[cacheKey]
        if (output != null) {
            return if (output !== INVALID_METHOD) output else null
        }
        for (m in cl.methods) {
            if (m.name == method && (args == null || classListEqual(args, m.parameterTypes))) {
                output = m
                break
            }
        }
        METHOD_CACHE[cacheKey] = output ?: INVALID_METHOD
        return output
    }

    fun getMethod(cl: Class<*>?, method: String): Method? {
        return getMethod(cl, method, null)
    }

    fun getConstructor(clazz: Class<*>?, vararg arguments: Class<*>?): Constructor<*>? {
        for (constructor in clazz!!.declaredConstructors) {
            if (Arrays.equals(constructor.parameterTypes, arguments)) {
                return constructor
            }
        }
        return null
    }

    fun classListEqual(l1: Array<Class<*>>, l2: Array<Class<*>>): Boolean {
        if (l1.size != l2.size) {
            return false
        }
        for (i in l1.indices) {
            if (l1[i] != l2[i]) {
                return false
            }
        }
        return true
    }

    interface ConstructorInvoker {
        operator fun invoke(vararg arguments: Any?): Any?
    }

    interface MethodInvoker {
        operator fun invoke(target: Any?, vararg arguments: Any?): Any?
    }

    interface FieldAccessor<T> {
        operator fun get(target: Any?): T
        operator fun set(target: Any?, value: Any?)
        fun hasField(target: Any): Boolean
    }

    private class InvalidMarker {
        var invalidFieldMarker: Void? = null
        fun invalidMethodMaker() {}
    }
}