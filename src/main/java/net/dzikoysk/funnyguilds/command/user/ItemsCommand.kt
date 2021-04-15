package net.dzikoysk.funnyguilds.command.user

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.element.gui.GuiItem
import net.dzikoysk.funnyguilds.element.gui.GuiWindow
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils
import org.apache.commons.lang3.StringUtils
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent

@FunnyComponent
class ItemsCommand {
    @FunnyCommand(
        name = "\${user.items.name}",
        description = "\${user.items.description}",
        aliases = ["\${user.items.aliases}"],
        permission = "funnyguilds.items",
        acceptsExceeded = true,
        playerOnly = true
    )
    fun execute(config: PluginConfiguration, player: Player) {
        var guiItems = config.guiItems
        var title = config.guiItemsTitle
        if (!config.useCommonGUI && player.hasPermission("funnyguilds.vip.items")) {
            guiItems = config.guiItemsVip
            title = config.guiItemsVipTitle
        }
        val gui = GuiWindow(title, guiItems!!.size / 9 + if (guiItems.size % 9 != 0) 1 else 0)
        gui.setCloseEvent { close: InventoryCloseEvent? -> gui.unregister() }
        for (item in guiItems) {
            item = item!!.clone()
            if (config.addLoreLines && (config.createItems!!.contains(item) || config.createItemsVip!!.contains(item))) {
                val meta = item.itemMeta
                if (meta == null) {
                    FunnyGuilds.Companion.getPluginLogger().warning("Item meta is not defined ($item)")
                    continue
                }
                val requiredAmount = item.amount
                val inventoryAmount = ItemUtils.getItemAmount(item, player.inventory)
                val enderChestAmount = ItemUtils.getItemAmount(item, player.enderChest)
                var lore = meta.lore
                if (lore == null) {
                    lore = ArrayList(config.guiItemsLore!!.size)
                }
                for (line in config.guiItemsLore!!) {
                    line = StringUtils.replace(line, "{REQ-AMOUNT}", Integer.toString(requiredAmount))
                    line = StringUtils.replace(line, "{PINV-AMOUNT}", Integer.toString(inventoryAmount))
                    line = StringUtils.replace(line, "{PINV-PERCENT}", ChatUtils.getPercent(inventoryAmount.toDouble(), requiredAmount.toDouble()))
                    line = StringUtils.replace(line, "{EC-AMOUNT}", Integer.toString(enderChestAmount))
                    line = StringUtils.replace(line, "{EC-PERCENT}", ChatUtils.getPercent(enderChestAmount.toDouble(), requiredAmount.toDouble()))
                    line = StringUtils.replace(line, "{ALL-AMOUNT}", Integer.toString(inventoryAmount + enderChestAmount))
                    line = StringUtils.replace(line, "{ALL-PERCENT}", ChatUtils.getPercent((inventoryAmount + enderChestAmount).toDouble(), requiredAmount.toDouble()))
                    lore.add(line)
                }
                if (config.guiItemsName != "") {
                    meta.setDisplayName(ItemUtils.translateTextPlaceholder(config.guiItemsName, null, item))
                }
                meta.lore = lore
                item.itemMeta = meta
            }
            gui.setToNextFree(GuiItem(item))
        }
        gui.open(player)
    }
}