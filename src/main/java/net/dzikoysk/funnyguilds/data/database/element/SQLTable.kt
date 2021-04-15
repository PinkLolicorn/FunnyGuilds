package net.dzikoysk.funnyguilds.data.database.element;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLTable {

    private final ArrayList<SQLElement> sqlElements = new ArrayList<>();
    private final String name;
    private int idPrimaryKey = 0;

    public SQLTable(String name) {
        this.name = name;
    }

    public void add(String key, SQLType type) {
        sqlElements.add(new SQLElement(key, type, -1, false));
    }

    public void add(String key, SQLType type, int size) {
        sqlElements.add(new SQLElement(key, type, size, false));
    }

    public void add(String key, SQLType type, boolean notNull) {
        sqlElements.add(new SQLElement(key, type, -1, notNull));
    }

    public void add(String key, SQLType type, int size, boolean notNull) {
        sqlElements.add(new SQLElement(key, type, size, notNull));
    }

    public void setPrimaryKey(String key) {
        for (int i = 0; i < sqlElements.size(); i++) {
            if (sqlElements.get(i).getKey().equalsIgnoreCase(key)) {
                this.setPrimaryKey(i);
                return;
            }
        }

        this.setPrimaryKey(0);
    }

    public void setPrimaryKey(int idPrimaryKey) {
        this.idPrimaryKey = idPrimaryKey;
    }

    public SQLElement getPrimaryKey() {
        return sqlElements.get(idPrimaryKey);
    }

    public String getName() {
        return name;
    }

    public String getNameGraveAccent() {
        return "`" + name + "`";
    }

    public ArrayList<SQLElement> getSqlElements() {
        return sqlElements;
    }

    public int getIndexElement(String key) {
        for (int index = 0; index < sqlElements.size(); index++) {
            if (!sqlElements.get(index).getKey().equalsIgnoreCase(key)) {
                continue;
            }

            return index;
        }

        return -1;
    }

    public SQLElement getSQLElement(String key) {
        for (SQLElement element : sqlElements) {
            if (element.getKey().equalsIgnoreCase(key)) {
                return element;
            }
        }

        return null;
    }

    public HashMap<String, Integer> getMapElementsKey() {
        HashMap<String, Integer> elementsMap = new HashMap<>();

        for (int i = 1; i < sqlElements.size() + 1; i++) {
            elementsMap.put(sqlElements.get(i - 1).getKey(), i);
        }

        return elementsMap;
    }
}
