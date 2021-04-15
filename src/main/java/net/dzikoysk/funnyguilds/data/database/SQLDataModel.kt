package net.dzikoysk.funnyguilds.data.database

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils
import net.dzikoysk.funnyguilds.basic.user.UserUtils
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdateRequest
import net.dzikoysk.funnyguilds.data.DataModel
import net.dzikoysk.funnyguilds.data.database.element.*
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import java.sql.SQLException

class SQLDataModel : DataModel {
    @Throws(SQLException::class)
    override fun load() {
        createTableIfNotExists(tabUsers)
        createTableIfNotExists(tabRegions)
        createTableIfNotExists(tabGuilds)
        loadUsers()
        loadRegions()
        loadGuilds()
        val concurrencyManager: ConcurrencyManager = FunnyGuilds.Companion.getInstance().getConcurrencyManager()
        concurrencyManager.postRequests(PrefixGlobalUpdateRequest())
    }

    @Throws(SQLException::class)
    fun loadUsers() {
        val result = SQLBasicUtils.getSelectAll(tabUsers).executeQuery()
        while (result!!.next()) {
            val userName = result.getString("name")
            if (!UserUtils.validateUsername(userName)) {
                FunnyGuilds.Companion.getPluginLogger().warning("Skipping loading of user '$userName'. Name is invalid.")
                continue
            }
            val user = DatabaseUser.deserialize(result)
            user?.wasChanged()
        }
        FunnyGuilds.Companion.getPluginLogger().info("Loaded users: " + UserUtils.getUsers().size)
    }

    @Throws(SQLException::class)
    fun loadRegions() {
        if (!FunnyGuilds.Companion.getInstance().getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.Companion.getPluginLogger().info("Regions are disabled and thus - not loaded")
            return
        }
        val result = SQLBasicUtils.getSelectAll(tabRegions).executeQuery()
        while (result!!.next()) {
            val region = DatabaseRegion.deserialize(result)
            if (region != null) {
                region.wasChanged()
                RegionUtils.addRegion(region)
            }
        }
        FunnyGuilds.Companion.getPluginLogger().info("Loaded regions: " + RegionUtils.getRegions().size)
    }

    @Throws(SQLException::class)
    fun loadGuilds() {
        val resultAll = SQLBasicUtils.getSelectAll(tabGuilds).executeQuery()
        while (resultAll!!.next()) {
            val guild = DatabaseGuild.deserialize(resultAll)
            guild?.wasChanged()
        }
        val result = SQLBasicUtils.getSelect(tabGuilds, "tag", "allies", "enemies").executeQuery()
        while (result!!.next()) {
            val guild = GuildUtils.getByTag(result.getString("tag")) ?: continue
            val alliesList = result.getString("allies")
            val enemiesList = result.getString("enemies")
            if (alliesList != null && alliesList != "") {
                guild.allies = GuildUtils.getGuilds(ChatUtils.fromString(alliesList))
            }
            if (enemiesList != null && enemiesList != "") {
                guild.enemies = GuildUtils.getGuilds(ChatUtils.fromString(enemiesList))
            }
        }
        for (guild in GuildUtils.getGuilds()) {
            if (guild!!.owner != null) {
                continue
            }
            GuildUtils.deleteGuild(guild)
        }
        FunnyGuilds.Companion.getPluginLogger().info("Loaded guilds: " + GuildUtils.getGuilds().size)
    }

    override fun save(ignoreNotChanged: Boolean) {
        for (user in UserUtils.getUsers()) {
            if (ignoreNotChanged && !user!!.wasChanged()) {
                continue
            }
            DatabaseUser.save(user)
        }
        for (guild in GuildUtils.getGuilds()) {
            if (ignoreNotChanged && !guild!!.wasChanged()) {
                continue
            }
            DatabaseGuild.save(guild)
        }
        if (!FunnyGuilds.Companion.getInstance().getPluginConfiguration().regionsEnabled) {
            return
        }
        for (region in RegionUtils.getRegions()) {
            if (ignoreNotChanged && !region!!.wasChanged()) {
                continue
            }
            DatabaseRegion.save(region)
        }
    }

    fun createTableIfNotExists(table: SQLTable?) {
        SQLBasicUtils.getCreate(table).executeUpdate()
        for (sqlElement in table.getSqlElements()) {
            SQLBasicUtils.getAlter(table, sqlElement).executeUpdate(true)
        }
    }

    companion object {
        private var instance: SQLDataModel?
        var tabUsers: SQLTable? = null
        var tabRegions: SQLTable? = null
        var tabGuilds: SQLTable? = null
        fun getInstance(): SQLDataModel? {
            return if (instance != null) {
                instance
            } else SQLDataModel()
        }

        fun loadModels() {
            tabUsers = SQLTable(FunnyGuilds.Companion.getInstance().getPluginConfiguration().mysql.usersTableName)
            tabRegions = SQLTable(FunnyGuilds.Companion.getInstance().getPluginConfiguration().mysql.regionsTableName)
            tabGuilds = SQLTable(FunnyGuilds.Companion.getInstance().getPluginConfiguration().mysql.guildsTableName)
            tabUsers!!.add("uuid", SQLType.VARCHAR, 36, true)
            tabUsers!!.add("name", SQLType.TEXT, true)
            tabUsers!!.add("points", SQLType.INT, true)
            tabUsers!!.add("kills", SQLType.INT, true)
            tabUsers!!.add("deaths", SQLType.INT, true)
            tabUsers!!.add("ban", SQLType.BIGINT)
            tabUsers!!.add("reason", SQLType.TEXT)
            tabUsers!!.setPrimaryKey("uuid")
            tabRegions!!.add("name", SQLType.VARCHAR, 100, true)
            tabRegions!!.add("center", SQLType.TEXT, true)
            tabRegions!!.add("size", SQLType.INT, true)
            tabRegions!!.add("enlarge", SQLType.INT, true)
            tabRegions!!.setPrimaryKey("name")
            tabGuilds!!.add("uuid", SQLType.VARCHAR, 100, true)
            tabGuilds!!.add("name", SQLType.TEXT, true)
            tabGuilds!!.add("tag", SQLType.TEXT, true)
            tabGuilds!!.add("owner", SQLType.TEXT, true)
            tabGuilds!!.add("home", SQLType.TEXT, true)
            tabGuilds!!.add("region", SQLType.TEXT, true)
            tabGuilds!!.add("regions", SQLType.TEXT, true)
            tabGuilds!!.add("members", SQLType.TEXT, true)
            tabGuilds!!.add("points", SQLType.INT, true)
            tabGuilds!!.add("lives", SQLType.INT, true)
            tabGuilds!!.add("ban", SQLType.BIGINT, true)
            tabGuilds!!.add("born", SQLType.BIGINT, true)
            tabGuilds!!.add("validity", SQLType.BIGINT, true)
            tabGuilds!!.add("pvp", SQLType.BOOLEAN, true)
            tabGuilds!!.add("attacked", SQLType.BIGINT)
            tabGuilds!!.add("allies", SQLType.TEXT)
            tabGuilds!!.add("enemies", SQLType.TEXT)
            tabGuilds!!.add("info", SQLType.TEXT)
            tabGuilds!!.add("deputy", SQLType.TEXT)
            tabGuilds!!.setPrimaryKey("uuid")
        }
    }

    init {
        instance = this
        loadModels()
    }
}