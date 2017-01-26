package be.isach.ultracosmetics.mysql;

import java.sql.Connection;

public class Table {
    private final Connection connection;

    private final String table;

    public Table(Connection connection, String table) {
        this.connection = connection;

        this.table = table;
    }

    public String getTableName() {
        return table;
    }

    public SelectQuery select() {
        return select("*");
    }

    public SelectQuery select(String selection) {
        return new SelectQuery(connection, "SELECT " + selection + " FROM " + table);
    }

    public CreateQuery create() {
        return new CreateQuery(connection, "CREATE TABLE IF NOT EXISTS " + table + " (");
    }

    public UpdateQuery update() {
        return new UpdateQuery(connection, "UPDATE " + table + " SET");
    }

    public InsertQuery insert() {
        return new InsertQuery(connection, "INSERT INTO " + table + " (");
    }

    public DeleteQuery delete() {
        return new DeleteQuery(connection, "DELETE FROM " + table);
    }
}
