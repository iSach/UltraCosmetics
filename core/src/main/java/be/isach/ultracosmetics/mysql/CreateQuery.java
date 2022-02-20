package be.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

public class CreateQuery extends Query {
    protected StringJoiner values = new StringJoiner(" (", ", ", ")");

    public CreateQuery(Connection connection, String sql) {
        super(connection, sql);
    }

    public CreateQuery create(String value) {
        values.add(value);
        return this;
    }

    public void execute() {
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.execute(sql + values.toString());
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
