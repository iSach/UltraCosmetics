package be.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

public class InsertQuery {
    private final Map<String,String> inserts = new HashMap<>();
    private final Table table;
    // when true, adds IGNORE keyword which ignores the insert if a row with a matching primary key already exists.
    // used to ensure a player is present in database when joining.
    private final boolean ignore;
    public InsertQuery(Table table, boolean ignore) {
        this.table = table;
        this.ignore = ignore;
    }

    public InsertQuery(Table table) {
        this(table, false);
    }

    public InsertQuery insert(String key, String value) {
        inserts.put(key, value);
        return this;
    }

    public void execute() {
        String sql = "INSERT " + (ignore ? "IGNORE " : "") + "INTO " + table.getName() + " ";
        StringJoiner columns = new StringJoiner(", ", "(", ")");
        StringJoiner values = new StringJoiner(", ", "(", ")");
        List<String> objects = new ArrayList<>();
        for (Entry<String,String> entry : inserts.entrySet()) {
            columns.add(entry.getKey());
            values.add("?");
            objects.add(entry.getValue());
        }
        sql += columns.toString() + " VALUES " + values.toString();
        try (Connection connection = table.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < objects.size(); i++) {
                statement.setString(i + 1, objects.get(i));
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
