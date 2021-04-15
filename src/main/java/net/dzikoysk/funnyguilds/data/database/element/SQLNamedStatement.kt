package net.dzikoysk.funnyguilds.data.database.element

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.data.database.Database
import org.diorite.utils.collections.maps.CaseInsensitiveMap
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class SQLNamedStatement(private val sql: String, keyMap: Map<String?, Int?>?) {
    private val placeholders: MutableMap<String, Any?> = HashMap()
    private val keyMapIndex: CaseInsensitiveMap<Int?>
    operator fun set(key: String, value: Any?) {
        if (!keyMapIndex.containsKey(key)) {
            return
        }
        placeholders[key] = value
    }

    fun executeUpdate() {
        try {
            Database.Companion.getConnection().use { con ->
                val statement = setPlaceholders(con.prepareStatement(sql))
                statement.executeUpdate()
            }
        } catch (sqlException: SQLException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not execute update", sqlException)
        }
    }

    fun executeUpdate(ignoreFails: Boolean) {
        try {
            Database.Companion.getConnection().use { con ->
                val statement = setPlaceholders(con.prepareStatement(sql))
                statement.executeUpdate()
            }
        } catch (sqlException: SQLException) {
            if (ignoreFails) {
                FunnyGuilds.Companion.getPluginLogger().debug("Could not execute update (ignoreFails)")
                return
            }
            FunnyGuilds.Companion.getPluginLogger().error("Could not execute update", sqlException)
        }
    }

    fun executeQuery(): ResultSet? {
        try {
            Database.Companion.getConnection().use { con ->
                val statement = setPlaceholders(con.prepareStatement(sql))
                return statement.executeQuery()
            }
        } catch (sqlException: SQLException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not execute query", sqlException)
        }
        return null
    }

    @Throws(SQLException::class)
    private fun setPlaceholders(preparedStatement: PreparedStatement): PreparedStatement {
        for ((key, value) in placeholders) {
            preparedStatement.setObject(keyMapIndex[key]!!, value)
        }
        return preparedStatement
    }

    init {
        keyMapIndex = CaseInsensitiveMap(keyMap)
    }
}