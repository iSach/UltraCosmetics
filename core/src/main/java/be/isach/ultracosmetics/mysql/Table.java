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

    public SelectQuery selectAll() {
        return select("*");
    }

    public SelectQuery select(String selection) {
        return new SelectQuery(getConnection(), "SELECT " + selection + " FROM " + table);
    }

    public CreateQuery create() {
        return new CreateQuery(getConnection(), "CREATE TABLE IF NOT EXISTS " + table);
    }

    public UpdateQuery update() {
        return new UpdateQuery(getConnection(), "UPDATE " + table + " SET");
    }

    public InsertQuery insert() {
        return new InsertQuery(getConnection(), "INSERT INTO " + table + " (");
    }

    public DeleteQuery delete() {
        return new DeleteQuery(getConnection(), "DELETE FROM " + table);
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
