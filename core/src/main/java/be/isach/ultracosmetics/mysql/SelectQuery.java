package be.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelectQuery extends Query {
    private boolean and;

    private PreparedStatement prest;

    private final List<Object> values;

    public SelectQuery(Connection connection, String sql) {
        super(connection, sql);

        and = false;

        values = new ArrayList<Object>();
    }

    public SelectQuery where(String key, Object value) {
        if (and) {
            sql += " AND";
        } else {
            sql += " WHERE";
        }

        sql += " " + key + "=";

        values.add(value);

        sql += "?";

        and = true;

        return this;
    }

    public ResultSet execute() {
        try {
            prest = connection.prepareStatement(sql);

            int i = 1;

            for (Object object : values) {
                prest.setObject(i, object);

                i++;
            }

            return prest.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();

            return null;
        }
    }

    public void close() {
        try {
            prest.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
