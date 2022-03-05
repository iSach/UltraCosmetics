package be.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class Table {
    private final DataSource dataSource;
    private final String table;

    public Table(DataSource dataSource, String table) {
        this.dataSource = dataSource;
        this.table = table;
    }

    public String getTableName() {
        return table;
    }

    public StandardQuery select(String columns) {
        return new StandardQuery(getConnection(), "SELECT " + columns + " FROM " + table);
    }

    public StandardQuery update() {
        return new StandardQuery(getConnection(), "UPDATE " + table);
    }

    public StandardQuery delete() {
        return new StandardQuery(getConnection(), "DELETE FROM " + table);
    }

    public InsertQuery insert() {
        return new InsertQuery(getConnection(), table);
    }

    public InsertQuery insertIgnore() {
        return new InsertQuery(getConnection(), table, true);
    }

    private Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
