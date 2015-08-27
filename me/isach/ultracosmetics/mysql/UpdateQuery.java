package me.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateQuery extends Query {
    private boolean comma;

    private boolean and;

    private final List<Object> values;

    public UpdateQuery(Connection connection, String sql) {
        super(connection, sql);

        comma = false;

        and = false;

        values = new ArrayList<Object>();
    }

    public UpdateQuery set(String field, Object value) {
        if (comma) {
            sql += ",";
        }

        values.add(value);

        sql += " " + field + "=?";

        comma = true;

        return this;
    }

    public UpdateQuery where(String key, Object value) {
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

    public void execute() {
        PreparedStatement prest;

        try {
            prest = connection.prepareStatement(sql);

            int i = 1;

            for (Object object : values) {
                prest.setObject(i, object);

                i++;
            }

            prest.executeUpdate();

            prest.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
