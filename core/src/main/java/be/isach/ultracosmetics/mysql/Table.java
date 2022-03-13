package be.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;

public class Table {
    private final DataSource dataSource;
    private final String table;

    public Table(DataSource dataSource, String table) {
        this.dataSource = dataSource;
        this.table = table;
    }

    public String getName() {
        return table;
    }

    public StandardQuery select(String columns) {
        return new StandardQuery(this, "SELECT " + columns + " FROM");
    }

    public StandardQuery update() {
        return new StandardQuery(this, "UPDATE");
    }

    public StandardQuery delete() {
        return new StandardQuery(this, "DELETE FROM");
    }

    public InsertQuery insert() {
        return new InsertQuery(this);
    }

    public InsertQuery insertIgnore() {
        return new InsertQuery(this, true);
    }

    protected Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String cleanCosmeticName(CosmeticType<?> cosmetic) {
        return cosmetic == null ? null : cosmetic.getConfigName().toLowerCase().replace("_", "");
    }

    public static String cleanCategoryName(Category cat) {
        return cat.toString().toLowerCase();
    }
}
