package net.dzikoysk.funnyguilds.data.database.element

class SQLTable(val name: String) {
    val sqlElements = ArrayList<SQLElement?>()
    private var idPrimaryKey = 0
    fun add(key: String?, type: SQLType?) {
        sqlElements.add(SQLElement(key, type, -1, false))
    }

    fun add(key: String?, type: SQLType?, size: Int) {
        sqlElements.add(SQLElement(key, type, size, false))
    }

    fun add(key: String?, type: SQLType?, notNull: Boolean) {
        sqlElements.add(SQLElement(key, type, -1, notNull))
    }

    fun add(key: String?, type: SQLType?, size: Int, notNull: Boolean) {
        sqlElements.add(SQLElement(key, type, size, notNull))
    }

    fun setPrimaryKey(key: String?) {
        for (i in sqlElements.indices) {
            if (sqlElements[i]!!.key.equals(key, ignoreCase = true)) {
                this.setPrimaryKey(i)
                return
            }
        }
        this.setPrimaryKey(0)
    }

    fun setPrimaryKey(idPrimaryKey: Int) {
        this.idPrimaryKey = idPrimaryKey
    }

    val primaryKey: SQLElement?
        get() = sqlElements[idPrimaryKey]
    val nameGraveAccent: String
        get() = "`$name`"

    fun getIndexElement(key: String?): Int {
        for (index in sqlElements.indices) {
            if (!sqlElements[index]!!.key.equals(key, ignoreCase = true)) {
                continue
            }
            return index
        }
        return -1
    }

    fun getSQLElement(key: String?): SQLElement? {
        for (element in sqlElements) {
            if (element!!.key.equals(key, ignoreCase = true)) {
                return element
            }
        }
        return null
    }

    val mapElementsKey: HashMap<String, Int>
        get() {
            val elementsMap = HashMap<String, Int>()
            for (i in 1 until sqlElements.size + 1) {
                elementsMap[sqlElements[i - 1]!!.key] = i
            }
            return elementsMap
        }
}