package net.dzikoysk.funnyguilds.util.commons.spigot

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.util.commons.bukkit.MaterialUtils
import net.dzikoysk.funnyguilds.util.nms.Reflections
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object ItemComponentUtils {
    private val NBT_TAG_COMPOUND_CONSTRUCTOR: Constructor<*>? = null
    private val AS_NMS_COPY: Method? = null
    private val SAVE: Method? = null
    fun translateComponentPlaceholder(message: String?, items: List<ItemStack?>?, item: ItemStack?): TextComponent {
        val translatedMessage = TextComponent()
        var messagePart = StringBuilder()
        var messageColor = ""
        val messageChars = message!!.toCharArray()
        var index = 0
        while (index < messageChars.size) {
            val symbol = messageChars[index]
            if (symbol != '{') {
                messagePart.append(symbol)
                if (symbol == ChatColor.COLOR_CHAR) {
                    messageColor += symbol
                    if (index + 1 >= messageChars.size) {
                        FunnyGuilds.Companion.getPluginLogger().warning("Invalid placeholder: $message (exceeds array limit at + $index)")
                        index++
                        continue
                    }
                    messageColor += messageChars[index + 1]
                }
                index++
                continue
            }
            val subItem = message.substring(index, Math.min(message.length, index + 6))
            if (subItem == "{ITEM}") {
                for (extra in TextComponent.fromLegacyText(messagePart.toString())) {
                    translatedMessage.addExtra(extra)
                }
                messagePart = StringBuilder()
                translatedMessage.addExtra(getItemComponent(item, messageColor))
                index += 5
                index++
                continue
            }
            val subItems = message.substring(index, Math.min(message.length, index + 7))
            if (subItems == "{ITEMS}") {
                for (extra in TextComponent.fromLegacyText(messagePart.toString())) {
                    translatedMessage.addExtra(extra)
                }
                messagePart = StringBuilder()
                for (itemNum in items!!.indices) {
                    translatedMessage.addExtra(getItemComponent(items[itemNum], messageColor))
                    if (itemNum != items.size - 1) {
                        for (extra in TextComponent.fromLegacyText("$messageColor, ")) {
                            translatedMessage.addExtra(extra)
                        }
                    }
                }
                index += 6
                index++
                continue
            }
            messagePart.append(symbol)
            index++
        }
        for (extra in TextComponent.fromLegacyText(messagePart.toString())) {
            translatedMessage.addExtra(extra)
        }
        return translatedMessage
    }

    fun getItemComponent(item: ItemStack?, messageColor: String): TextComponent {
        val itemComponent = TextComponent()
        val config: PluginConfiguration = FunnyGuilds.Companion.getInstance().getPluginConfiguration()
        for (extra in TextComponent.fromLegacyText(messageColor + item!!.amount + config.itemAmountSuffix + " " + MaterialUtils.getMaterialName(item.type))) {
            itemComponent.addExtra(extra)
        }
        try {
            val jsonItem = SAVE!!.invoke(AS_NMS_COPY!!.invoke(null, item), NBT_TAG_COMPOUND_CONSTRUCTOR!!.newInstance()).toString()
            itemComponent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_ITEM, arrayOf<BaseComponent>(TextComponent(jsonItem)))
        } catch (exception: IllegalAccessException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not get item component", exception)
        } catch (exception: IllegalArgumentException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not get item component", exception)
        } catch (exception: InvocationTargetException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not get item component", exception)
        } catch (exception: InstantiationException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not get item component", exception)
        }
        return itemComponent
    }

    init {
        val craftItemStack = Reflections.getCraftBukkitClass("inventory.CraftItemStack")
        AS_NMS_COPY = Reflections.getMethod(net.dzikoysk.funnyguilds.util.commons.spigot.craftItemStack, "asNMSCopy", ItemStack::class.java)
        val nmsItemStack = Reflections.getNMSClass("ItemStack")
        val nbtTagCompound = Reflections.getNMSClass("NBTTagCompound")
        NBT_TAG_COMPOUND_CONSTRUCTOR = Reflections.getConstructor(net.dzikoysk.funnyguilds.util.commons.spigot.nbtTagCompound)
        SAVE = Reflections.getMethod(net.dzikoysk.funnyguilds.util.commons.spigot.nmsItemStack, "save", net.dzikoysk.funnyguilds.util.commons.spigot.nbtTagCompound)
    }
}