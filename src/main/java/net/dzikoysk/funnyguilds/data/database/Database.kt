package net.dzikoysk.funnyguilds.data.database

import com.zaxxer.hikari.HikariDataSource
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration.MySQL
import java.sql.SQLException

java.sql.Connection
class Database {
    val dataSource: HikariDataSource
    fun shutdown() {
        dataSource.close()
    }

    companion object {
        private var instance: Database?
        fun getInstance(): Database? {
            return if (instance == null) {
                Database()
            } else instance
        }

        @get:Throws(SQLException::class)
        val connection: Connection
            get() = getInstance()!!.dataSource.connection
    }

    init {
        instance = this
        dataSource = HikariDataSource()
        val c: MySQL = FunnyGuilds.Companion.getInstance().getPluginConfiguration().mysql
        var poolSize = c.poolSize
        if (poolSize <= 0) {
            poolSize = Runtime.getRuntime().availableProcessors() * 2 + 1 // (core_count * 2) + spindle [pattern from PostgreSQL wiki]
        }
        dataSource.maximumPoolSize = poolSize
        dataSource.connectionTimeout = c.connectionTimeout.toLong()
        dataSource.jdbcUrl = "jdbc:mysql://" + c.hostname + ":" + c.port + "/" + c.database + "?useSSL=" + c.useSSL
        dataSource.username = c.user
        if (c.password != null && !c.password!!.isEmpty()) {
            dataSource.password = c.password
        }
        dataSource.addDataSourceProperty("cachePrepStmts", true)
        dataSource.addDataSourceProperty("prepStmtCacheSize", 250)
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048)
        dataSource.addDataSourceProperty("useServerPrepStmts", true)
    }
}