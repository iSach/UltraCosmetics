package be.isach.ultracosmetics.mysql;

public class Column {
    private final String name;
    private final String properties;
    public Column(String name, String properties) {
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " " + properties;
    }
}
