package net.dzikoysk.funnyguilds.util.commons.bukkit

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import net.dzikoysk.funnyguilds.util.commons.spigot.ItemComponentUtils
import net.dzikoysk.funnyguilds.util.nms.EggTypeChanger
import net.dzikoysk.funnyguilds.util.nms.Reflections
import org.apache.commons.lang3.StringUtils
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

object ItemUtils {
    private var BY_IN_GAME_NAME_ENCHANT: Method? = null
    private var CREATE_NAMESPACED_KEY: Method? = null
    fun playerHasEnoughItems(player: Player, requiredItems: List<ItemStack?>?): Boolean {
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        val messages: MessageConfiguration = FunnyGuilds.Companion.getInstance().getMessageConfiguration()
        for (requiredItem in requiredItems!!) {
            if (player.inventory.containsAtLeast(requiredItem, requiredItem!!.amount)) {
                continue
            }
            if (config.enableItemComponent) {
                player.spigot().sendMessage(ItemComponentUtils.translateComponentPlaceholder(messages.createItems, requiredItems, requiredItem))
            } else {
                player.sendMessage(translateTextPlaceholder(messages.createItems, requiredItems, requiredItem)!!)
            }
            return false
        }
        return true
    }

    fun translateTextPlaceholder(message: String?, items: Collection<ItemStack?>?, item: ItemStack?): String? {
        var message = message
        val contentBuilder = StringBuilder()
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        if (message!!.contains("{ITEM}")) {
            contentBuilder.append(item!!.amount)
            contentBuilder.append(config.itemAmountSuffix + " ")
            contentBuilder.append(MaterialUtils.getMaterialName(item.type))
            message = StringUtils.replace(message, "{ITEM}", contentBuilder.toString())
        }
        if (message!!.contains("{ITEMS}")) {
            val translatedItems: MutableCollection<String?> = ArrayList()
            for (itemStack in items!!) {
                contentBuilder.setLength(0)
                contentBuilder.append(itemStack!!.amount)
                contentBuilder.append(config.itemAmountSuffix + " ")
                contentBuilder.append(MaterialUtils.getMaterialName(itemStack.type))
                translatedItems.add(contentBuilder.toString())
            }
            message = StringUtils.replace(message, "{ITEMS}", ChatUtils.toString(translatedItems, true))
        }
        if (message!!.contains("{ITEM-NO-AMOUNT}")) {
            contentBuilder.setLength(0)
            contentBuilder.append(MaterialUtils.getMaterialName(item!!.type))
            message = StringUtils.replace(message, "{ITEM-NO-AMOUNT}", contentBuilder.toString())
        }
        return message
    }

    fun parseItem(string: String): ItemStack {
        val split = string.split(" ".toRegex()).toTypedArray()
        val typeSplit = split[1].split(":".toRegex()).toTypedArray()
        val subtype = if (typeSplit.size > 1) typeSplit[1] else "0"
        val material = MaterialUtils.parseMaterial(typeSplit[0], false)
        var stack: Int
        var data: Int
        try {
            stack = split[0].toInt()
            data = subtype.toInt()
        } catch (e: NumberFormatException) {
            FunnyGuilds.Companion.getPluginLogger().parser("Unknown size: " + split[0])
            stack = 1
            data = 0
        }
        val item = ItemBuilder(material, stack, data)
        for (index in 2 until split.size) {
            val itemAttribute = split[index].split(":".toRegex()).toTypedArray()
            val attributeName = itemAttribute[0]
            val attributeValue = Arrays.copyOfRange(itemAttribute, 1, itemAttribute.size)
            if (attributeName.equals("name", ignoreCase = true)) {
                item.setName(StringUtils.replace(ChatUtils.colored(java.lang.String.join(":", *attributeValue)), "_", " "), true)
            } else if (attributeName.equals("lore", ignoreCase = true)) {
                val lores = java.lang.String.join(":", *attributeValue).split("#".toRegex()).toTypedArray()
                val lore: MutableList<String> = ArrayList()
                for (loreLine in lores) {
                    lore.add(StringUtils.replace(StringUtils.replace(ChatUtils.colored(loreLine), "_", " "), "{HASH}", "#"))
                }
                item.setLore(lore)
            } else if (attributeName.equals("enchant", ignoreCase = true)) {
                var level: Int
                level = try {
                    attributeValue[1].toInt()
                } catch (numberFormatException: NumberFormatException) {
                    FunnyGuilds.Companion.getPluginLogger().parser("Unknown enchant level: " + attributeValue[1])
                    1
                }
                val enchant = matchEnchant(attributeValue[0])
                if (enchant == null) {
                    FunnyGuilds.Companion.getPluginLogger().parser("Unknown enchant: " + attributeValue[0])
                    continue
                }
                item.addEnchant(enchant, level)
            } else if (attributeName.equals("skullowner", ignoreCase = true)) {
                if (item.meta is SkullMeta) {
                    (item.meta as SkullMeta).owner = attributeValue[0]
                    item.refreshMeta()
                }
            } else if (attributeName.equals("flags", ignoreCase = true)) {
                val flags = attributeValue[0].split(",".toRegex()).toTypedArray()
                for (flag in flags) {
                    flag = flag.trim { it <= ' ' }
                    val matchedFlag = matchItemFlag(flag)
                    if (matchedFlag == null) {
                        FunnyGuilds.Companion.getPluginLogger().parser("Unknown item flag: $flag")
                        continue
                    }
                    item.setFlag(matchedFlag)
                }
            } else if (attributeName.equals("armorcolor", ignoreCase = true)) {
                if (item.meta !is LeatherArmorMeta) {
                    FunnyGuilds.Companion.getPluginLogger().parser("Invalid item armor color attribute (given item is not a leather armor!): " + split[index])
                    continue
                }
                val color = attributeValue[0].split("_".toRegex()).toTypedArray()
                try {
                    (item.meta as LeatherArmorMeta).setColor(Color.fromRGB(color[0].toInt(), color[1].toInt(), color[2].toInt()))
                    item.refreshMeta()
                } catch (numberFormatException: NumberFormatException) {
                    FunnyGuilds.Companion.getPluginLogger().parser("Invalid armor color: " + Arrays.toString(attributeValue))
                }
            } else if (attributeName.equals("eggtype", ignoreCase = true)) {
                if (EggTypeChanger.needsSpawnEggMeta()) {
                    var type: EntityType? = null
                    val entityTypeName = attributeValue[0].toUpperCase()
                    try {
                        type = EntityType.valueOf(entityTypeName)
                    } catch (exception: Exception) {
                        FunnyGuilds.Companion.getPluginLogger().parser("Unknown entity type: $entityTypeName")
                    }
                    if (type != null) {
                        EggTypeChanger.applyChanges(item.meta, type)
                        item.refreshMeta()
                    }
                } else {
                    FunnyGuilds.Companion.getPluginLogger().info("This MC version supports metadata for spawnGuildHeart egg type, no need to use eggtype in item creation!")
                }
            }
        }
        return item.item
    }

    private fun matchEnchant(enchantName: String): Enchantment? {
        if (BY_IN_GAME_NAME_ENCHANT != null && CREATE_NAMESPACED_KEY != null) {
            try {
                val namespacedKey = CREATE_NAMESPACED_KEY!!.invoke(null, enchantName.toLowerCase())
                val enchantment = BY_IN_GAME_NAME_ENCHANT!!.invoke(null, namespacedKey)
                if (enchantment != null) {
                    return enchantment as Enchantment
                }
            } catch (ignored: IllegalAccessException) {
            } catch (ignored: InvocationTargetException) {
            }
        }
        return Enchantment.getByName(enchantName.toUpperCase())
    }

    private fun matchItemFlag(flagName: String): ItemFlag? {
        for (flag in ItemFlag.values()) {
            if (flag.name.equals(flagName, ignoreCase = true)) {
                return flag
            }
        }
        return null
    }

    fun getItemAmount(item: ItemStack, inv: Inventory): Int {
        var amount = 0
        for (`is` in inv.contents) {
            if (item.isSimilar(`is`)) {
                amount += `is`.amount
            }
        }
        return amount
    }

    fun toArray(collection: Collection<ItemStack?>?): Array<ItemStack> {
        return collection!!.toTypedArray()!!
    }

    init {
        if (!Reflections.USE_PRE_12_METHODS) {
            val namespacedKeyClass = Reflections.getBukkitClass("NamespacedKey")
            BY_IN_GAME_NAME_ENCHANT = Reflections.getMethod(Enchantment::class.java, "getByKey")
            CREATE_NAMESPACED_KEY = Reflections.getMethod(net.dzikoysk.funnyguilds.util.commons.bukkit.namespacedKeyClass, "minecraft", String::class.java)
        }
    }
}