package be.isach.ultracosmetics.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Column<T> {
    private final String name;
    private final String properties;
    private final Class<T> type;
    public Column(String name, String properties, Class<T> type) {
        this.name = name;
        this.properties = properties;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " " + properties;
    }

    public Class<T> getTypeClass() {
        return type;
    }

    public Object getValue(ResultSet result) throws SQLException {
        if (type == Integer.class) {
            return result.getInt(name);
        } else if (type == Boolean.class) {
            return result.getBoolean(name);
        } else if (type == String.class) {
            return result.getString(name);
        } else {
            throw new RuntimeException("No getter for class " + type.getName());
        }
    }
}
