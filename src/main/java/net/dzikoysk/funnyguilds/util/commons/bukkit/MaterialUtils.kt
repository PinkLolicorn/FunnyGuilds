package net.dzikoysk.funnyguilds.util.commons.bukkit

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import net.dzikoysk.funnyguilds.util.nms.Reflections
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.tuple.Pair
import org.bukkit.Material
import java.lang.reflect.InvocationTargetException

object MaterialUtils {
    private val MATCH_MATERIAL_METHOD = Reflections.getMethod(Material::class.java, "matchMaterial", String::class.java, Boolean::class.javaPrimitiveType!!)
    fun parseMaterial(string: String?, allowNullReturn: Boolean): Material? {
        if (string == null) {
            FunnyGuilds.Companion.getPluginLogger().parser("Unknown material: null")
            return if (allowNullReturn) null else Material.AIR
        }
        val materialName = string.toUpperCase().replace(" ".toRegex(), "_")
        val material = matchMaterial(materialName)
        if (material == null) {
            FunnyGuilds.Companion.getPluginLogger().parser("Unknown material: $string")
            return if (allowNullReturn) null else Material.AIR
        }
        return material
    }

    fun parseMaterialData(string: String?, allowNullReturn: Boolean): Pair<Material, Byte>? {
        if (string == null) {
            FunnyGuilds.Companion.getPluginLogger().parser("Unknown material data: null")
            return if (allowNullReturn) null else Pair.of(Material.AIR, 0.toByte())
        }
        val data = string.split(":".toRegex()).toTypedArray()
        val material = parseMaterial(data[0], allowNullReturn)
        if (material == null) {
            FunnyGuilds.Companion.getPluginLogger().parser("Unknown material in material data: $string")
            return if (allowNullReturn) null else Pair.of(Material.AIR, 0.toByte())
        }
        return Pair.of(material, if (data.size == 2) data[1].toByte() else 0.toByte())
    }

    fun hasGravity(material: Material?): Boolean {
        return when (material.toString()) {
            "DRAGON_EGG", "SAND", "GRAVEL", "ANVIL", "CONCRETE_POWDER" -> true
            else -> false
        }
    }

    fun getMaterialName(material: Material): String {
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        if (!config.translatedMaterialsEnable) {
            return material.toString()
        }
        return if (config.translatedMaterials!!.containsKey(material)) {
            ChatUtils.colored(FunnyGuilds.Companion.getInstance().getPluginConfiguration().translatedMaterials.get(material))
        } else StringUtils.replaceChars(material.toString().toLowerCase(), '_', ' ')
    }

    fun matchMaterial(materialName: String?): Material? {
        return try {
            if (Reflections.USE_PRE_13_METHODS) {
                return Material.matchMaterial(materialName!!)
            }
            if (MATCH_MATERIAL_METHOD == null) {
                return null
            }
            var material = MATCH_MATERIAL_METHOD.invoke(null, materialName, false) as Material
            if (material == null) {
                material = MATCH_MATERIAL_METHOD.invoke(null, materialName, true) as Material
            }
            material
        } catch (ex: IllegalAccessException) {
            null
        } catch (ex: InvocationTargetException) {
            null
        }
    }
}