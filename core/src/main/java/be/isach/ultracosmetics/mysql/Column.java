package be.isach.ultracosmetics.mysql;

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

    public Class<?> getTypeClass() {
        return type;
    }
}
