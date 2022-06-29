package be.isach.ultracosmetics.mysql;

public class StringColumn extends Column<String> {
    private final int size;

    public StringColumn(String name, int size, boolean ascii) {
        super(name, "VARCHAR(" + size + ") CHARACTER SET " + (ascii ? "latin1" : "utf8mb4"), String.class);
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
