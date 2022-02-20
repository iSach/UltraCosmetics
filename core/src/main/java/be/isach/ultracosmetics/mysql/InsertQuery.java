package be.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;

public class InsertQuery extends Query {
    private boolean firstValue;
    private final List<String> values;

    public InsertQuery(Connection connection, String sql) {
        super(connection, sql);
        firstValue = true;
        values = new ArrayList<>();
    }

    public InsertQuery insert(String insert) {
        sql += insert + ", ";
        return this;
    }

    public InsertQuery value(Object value) {
        values.add(value.toString());
        sql = sql.substring(0, sql.length() - 1);

        if (firstValue) {
            sql = sql.substring(0, sql.length() - 1);
            sql += ") VALUES (?)";
            firstValue = false;
        } else {
            sql += ", ?)";
        }
        return this;
    }

    public void execute() {
        PreparedStatement prest;
        try {
            if (connection == null) {
                UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.WARNING, "No Connection!");
                return;
            }
            if (sql == null) {
                UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.WARNING, "Request is null!");
                return;
            }
            prest = connection.prepareStatement(sql);
            int i = 1;
            for (String string : values) {
                prest.setString(i, string);
                i++;
            }
            prest.executeUpdate();
            prest.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
