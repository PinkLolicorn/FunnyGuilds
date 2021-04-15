package net.dzikoysk.funnyguilds.util.nms

import net.dzikoysk.funnyguilds.FunnyGuilds
import org.bukkit.plugin.PluginDescriptionFile

class DescriptionChanger(private val descriptionFile: PluginDescriptionFile) {
    fun rename(pluginName: String?) {
        if (pluginName == null || pluginName.isEmpty()) {
            return
        }
        try {
            val field = Reflections.getPrivateField(descriptionFile.javaClass, "name") ?: return
            field[descriptionFile] = pluginName
        } catch (ex: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not change description file", ex)
        }
    }
}