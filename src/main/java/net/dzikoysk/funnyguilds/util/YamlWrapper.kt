package net.dzikoysk.funnyguilds.util

import net.dzikoysk.funnyguilds.FunnyGuilds
import org.bukkit.configuration.file.YamlConfiguration
import java.io.*

class YamlWrapper(file: File?) : YamlConfiguration() {
    private val file: File?
    override fun save(file: File) {
        try {
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
            super.save(file)
        } catch (ioException: IOException) {
            FunnyGuilds.Companion.getPluginLogger().error("Failed to save the file!", ioException)
        }
    }

    fun save() {
        try {
            if (!file!!.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
            super.save(file)
        } catch (ioException: IOException) {
            FunnyGuilds.Companion.getPluginLogger().error("Failed to save the file!", ioException)
        }
    }

    init {
        try {
            if (!file!!.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
            super.load(file)
        } catch (exception: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Failed to load the file!", exception)
        }
        this.file = file
    }
}