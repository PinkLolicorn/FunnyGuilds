package net.dzikoysk.funnyguilds.data.flat

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.util.commons.*
import java.io.*

class FlatPatcher {
    fun patch(flatDataModel: FlatDataModel) {
        val guilds: File = File(FunnyGuilds.Companion.getInstance().getDataFolder().toString() + File.separator + "guilds")
        val regions: File = File(FunnyGuilds.Companion.getInstance().getDataFolder().toString() + File.separator + "regions")
        val guildsExists = guilds.exists()
        val regionsExists = regions.exists()
        if (guildsExists || regionsExists) {
            FunnyGuilds.Companion.getPluginLogger().update("Updating flat files ...")
            FunnyGuilds.Companion.getPluginLogger().update("Scanning files ...")
            var filesFound = 0
            var guildsList = guilds.listFiles()
            var regionsList = regions.listFiles()
            filesFound += guildsList?.size ?: 0
            filesFound += regionsList?.size ?: 0
            FunnyGuilds.Companion.getPluginLogger().update("$filesFound files found ...")
            FunnyGuilds.Companion.getPluginLogger().update("Updating files ...")
            if (guildsExists) {
                guilds.renameTo(flatDataModel.guildsFolder)
            }
            if (regionsExists) {
                regions.renameTo(flatDataModel.regionsFolder)
            }
            guildsList = guilds.listFiles()
            regionsList = regions.listFiles()
            if (guildsList == null || guildsList.size == 0) {
                IOUtils.delete(guilds)
            }
            if (regionsList == null || regionsList.size == 0) {
                IOUtils.delete(regions)
            }
            FunnyGuilds.Companion.getPluginLogger().update("Done!")
            FunnyGuilds.Companion.getPluginLogger().update("Updated files: $filesFound")
        }
    }
}