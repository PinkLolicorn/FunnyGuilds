package net.dzikoysk.funnyguilds.data.flat

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.BasicType
import net.dzikoysk.funnyguilds.basic.guild.*
import net.dzikoysk.funnyguilds.basic.user.User
import net.dzikoysk.funnyguilds.basic.user.UserUtils
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseFixAlliesRequest
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest
import net.dzikoysk.funnyguilds.data.DataModel
import net.dzikoysk.funnyguilds.util.commons.*
import org.apache.commons.lang3.StringUtils
import java.io.*

class FlatDataModel(funnyGuilds: FunnyGuilds) : DataModel {
    val guildsFolder: File
    val regionsFolder: File
    val usersFolder: File
    fun loadCustomFile(type: BasicType?, name: String?): File? {
        when (type) {
            BasicType.GUILD -> {
                val file = File(guildsFolder, "$name.yml")
                IOUtils.initialize(file, true)
                return file
            }
            BasicType.REGION -> {
                val file = File(regionsFolder, "$name.yml")
                IOUtils.initialize(file, true)
                return file
            }
            BasicType.USER -> {
                val file = File(usersFolder, "$name.yml")
                IOUtils.initialize(file, true)
                return file
            }
        }
        return null
    }

    fun getUserFile(user: User?): File {
        return File(usersFolder, user!!.name + ".yml")
    }

    fun getRegionFile(region: Region): File {
        return File(regionsFolder, region.name + ".yml")
    }

    fun getGuildFile(guild: Guild): File {
        return File(guildsFolder, guild.name + ".yml")
    }

    override fun load() {
        loadUsers()
        loadRegions()
        loadGuilds()
        validateLoadedData()
    }

    override fun save(ignoreNotChanged: Boolean) {
        saveUsers(ignoreNotChanged)
        saveRegions(ignoreNotChanged)
        saveGuilds(ignoreNotChanged)
    }

    private fun saveUsers(ignoreNotChanged: Boolean) {
        if (UserUtils.getUsers().isEmpty()) {
            return
        }
        var errors = 0
        for (user in UserUtils.getUsers()) {
            if (user.uuid == null || user!!.name == null) {
                errors++
                continue
            }
            if (ignoreNotChanged && !user!!.wasChanged()) {
                continue
            }
            FlatUser(user).serialize(this)
        }
        if (errors > 0) {
            FunnyGuilds.Companion.getPluginLogger().error("Users save errors $errors")
        }
    }

    private fun loadUsers() {
        val path = usersFolder.listFiles()
        var errors = 0
        if (path == null) {
            FunnyGuilds.Companion.getPluginLogger().error("critical error loading the users!")
            return
        }
        for (file in path) {
            if (file.length() == 0L) {
                continue
            }
            if (!UserUtils.validateUsername(StringUtils.removeEnd(file.name, ".yml"))) {
                FunnyGuilds.Companion.getPluginLogger().warning("Skipping loading of user file '" + file.name + "'. Name is invalid.")
                continue
            }
            val user: User = FlatUser.Companion.deserialize(file)
            if (user == null) {
                errors++
                continue
            }
            user.wasChanged()
        }
        if (errors > 0) {
            FunnyGuilds.Companion.getPluginLogger().error("Users load errors $errors")
        }
        FunnyGuilds.Companion.getPluginLogger().info("Loaded users: " + UserUtils.getUsers().size)
    }

    private fun saveRegions(ignoreNotChanged: Boolean) {
        if (!FunnyGuilds.Companion.getInstance().getPluginConfiguration().regionsEnabled) {
            return
        }
        var errors = 0
        for (region in RegionUtils.getRegions()) {
            if (ignoreNotChanged && !region!!.wasChanged()) {
                continue
            }
            if (!FlatRegion(region).serialize(this)) {
                errors++
            }
        }
        if (errors > 0) {
            FunnyGuilds.Companion.getPluginLogger().error("Regions save errors $errors")
        }
    }

    private fun loadRegions() {
        if (!FunnyGuilds.Companion.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.Companion.getPluginLogger().info("Regions are disabled and thus - not loaded")
            return
        }
        val path = regionsFolder.listFiles()
        var errors = 0
        if (path == null) {
            FunnyGuilds.Companion.getPluginLogger().error("critical error loading the regions!")
            return
        }
        for (file in path) {
            val region: Region = FlatRegion.Companion.deserialize(file)
            if (region == null) {
                errors++
                continue
            }
            region.wasChanged()
            RegionUtils.addRegion(region)
        }
        if (errors > 0) {
            FunnyGuilds.Companion.getPluginLogger().error("Guild load errors $errors")
        }
        FunnyGuilds.Companion.getPluginLogger().info("Loaded regions: " + RegionUtils.getRegions().size)
    }

    private fun saveGuilds(ignoreNotChanged: Boolean) {
        var errors = 0
        for (guild in GuildUtils.getGuilds()) {
            if (ignoreNotChanged && !guild!!.wasChanged()) {
                continue
            }
            if (!FlatGuild(guild).serialize(this)) {
                errors++
            }
        }
        if (errors > 0) {
            FunnyGuilds.Companion.getPluginLogger().error("Guilds save errors: $errors")
        }
    }

    private fun loadGuilds() {
        GuildUtils.getGuilds().clear()
        val path = guildsFolder.listFiles()
        var errors = 0
        if (path == null) {
            FunnyGuilds.Companion.getPluginLogger().error("critical error loading the guilds!")
            return
        }
        for (file in path) {
            val guild: Guild = FlatGuild.Companion.deserialize(file)
            if (guild == null) {
                errors++
                continue
            }
            guild.wasChanged()
        }
        for (guild in GuildUtils.getGuilds()) {
            if (guild!!.owner != null) {
                continue
            }
            errors++
            FunnyGuilds.Companion.getPluginLogger().error("In guild " + guild.tag + " owner not exist!")
        }
        if (errors > 0) {
            FunnyGuilds.Companion.getPluginLogger().error("Guild load errors $errors")
        }
        val concurrencyManager: ConcurrencyManager = FunnyGuilds.Companion.getInstance().getConcurrencyManager()
        concurrencyManager.postRequests(DatabaseFixAlliesRequest(), PrefixGlobalUpdateRequest())
        FunnyGuilds.Companion.getPluginLogger().info("Loaded guilds: " + GuildUtils.getGuilds().size)
    }

    init {
        guildsFolder = File(funnyGuilds.pluginDataFolder, "guilds")
        regionsFolder = File(funnyGuilds.pluginDataFolder, "regions")
        usersFolder = File(funnyGuilds.pluginDataFolder, "users")
        val flatPatcher = FlatPatcher()
        flatPatcher.patch(this)
    }
}