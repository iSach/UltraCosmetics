package be.isach.ultracosmetics.mysql;

public class StringColumn extends Column<String> {
    private final int size;
    public StringColumn(String name, int size) {
        super(name, "VARCHAR(" + size + ")", String.class);
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
