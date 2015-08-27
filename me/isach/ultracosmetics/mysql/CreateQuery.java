package me.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateQuery extends Query {
    private boolean firstValue;

    public CreateQuery(Connection connection, String sql) {
        super(connection, sql);

        firstValue = true;
    }

    public CreateQuery create(String value) {
        if (!firstValue) {
            sql = sql.substring(0, sql.length() - 1);

            sql += ", ";
        } else {
            firstValue = false;
        }

        sql += value + ")";

        return this;
    }

    public void execute() {
        Statement statement;

        try {
            statement = connection.createStatement();

            statement.execute(sql);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
