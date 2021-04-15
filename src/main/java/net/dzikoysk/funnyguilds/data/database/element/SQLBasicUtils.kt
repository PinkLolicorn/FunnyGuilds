package net.dzikoysk.funnyguilds.data.database.element

import org.panda_lang.utilities.commons.text.Joiner

object SQLBasicUtils {
    fun getInsert(table: SQLTable?): SQLNamedStatement {
        val sb = StringBuilder()
        sb.append("INSERT INTO ")
        sb.append(table.getNameGraveAccent())
        sb.append(" (")
        sb.append(Joiner.on(", ").join(table.getSqlElements()) { obj: SQLElement? -> obj!!.keyGraveAccent })
        sb.append(") VALUES (")
        sb.append(Joiner.on(", ").join(table.getSqlElements()) { sqlElement: SQLElement? -> "?" })
        sb.append(") ON DUPLICATE KEY UPDATE ")
        sb.append(Joiner.on(", ").join(table.getSqlElements()) { obj: SQLElement? -> obj!!.keyValuesAssignment })
        return SQLNamedStatement(sb.toString(), table.getMapElementsKey())
    }

    fun getSelect(table: SQLTable?, vararg sqlElements: String): SQLNamedStatement {
        val sb = StringBuilder()
        sb.append("SELECT ")
        sb.append(Joiner.on(", ").join(sqlElements) { sqlElement: String? ->
            table!!.getSQLElement(sqlElement)!!
                .keyGraveAccent
        })
        sb.append(" FROM ")
        sb.append(table.getNameGraveAccent())
        return SQLNamedStatement(sb.toString(), HashMap())
    }

    fun getSelectAll(table: SQLTable?): SQLNamedStatement {
        val sb = StringBuilder()
        sb.append("SELECT * FROM ")
        sb.append(table.getNameGraveAccent())
        return SQLNamedStatement(sb.toString(), HashMap())
    }

    fun getUpdate(table: SQLTable?, element: SQLElement?): SQLNamedStatement {
        val keyMap = HashMap<String?, Int?>()
        val sb = StringBuilder()
        sb.append("UPDATE ")
        sb.append(table.getNameGraveAccent())
        sb.append(" SET ")
        sb.append(element!!.keyGraveAccent)
        sb.append(" = ?")
        sb.append(" WHERE ")
        sb.append(table.getPrimaryKey().keyGraveAccent)
        sb.append(" = ?")
        keyMap[element.key] = 1
        keyMap[table.getPrimaryKey().key] = 2
        return SQLNamedStatement(sb.toString(), keyMap)
    }

    fun getCreate(table: SQLTable?): SQLNamedStatement {
        val sb = StringBuilder()
        sb.append("CREATE TABLE IF NOT EXISTS ")
        sb.append(table.getNameGraveAccent())
        sb.append(" (")
        sb.append(Joiner.on(", ").join(table.getSqlElements()) { sqlElement: SQLElement? ->
            val element = StringBuilder()
            element.append(sqlElement!!.keyGraveAccent)
            element.append(" ")
            element.append(sqlElement.type)
            if (sqlElement.isNotNull) {
                element.append(" NOT NULL")
            }
            element.toString()
        })
        sb.append(", PRIMARY KEY (")
        sb.append(table.getPrimaryKey().key)
        sb.append("));")
        return SQLNamedStatement(sb.toString(), HashMap())
    }

    fun getDelete(table: SQLTable?): SQLNamedStatement {
        val keyMap = HashMap<String?, Int?>()
        val sb = StringBuilder()
        sb.append("DELETE FROM ")
        sb.append(table.getNameGraveAccent())
        sb.append(" WHERE ")
        sb.append(table.getPrimaryKey().keyGraveAccent)
        sb.append(" = ?")
        keyMap[table.getPrimaryKey().key] = 1
        return SQLNamedStatement(sb.toString(), keyMap)
    }

    fun getAlter(table: SQLTable?, column: SQLElement?): SQLNamedStatement {
        val sb = StringBuilder()
        val index = table!!.getIndexElement(column!!.key)
        sb.append("ALTER TABLE ")
        sb.append(table.nameGraveAccent)
        sb.append(" ADD COLUMN ")
        sb.append(column.keyGraveAccent)
        sb.append(" ")
        sb.append(column.type)
        sb.append(if (index == 0) " FIRST" else " AFTER " + table.sqlElements[index - 1].keyGraveAccent)
        sb.append(";")
        return SQLNamedStatement(sb.toString(), HashMap())
    }
}