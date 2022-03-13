package be.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Function;

import be.isach.ultracosmetics.UltraCosmeticsData;

public class StandardQuery {
    protected final Table table;
    protected final String command;
    protected final Map<String,Object> whereItems = new HashMap<>();
    protected final Map<String,Object> setItems = new HashMap<>();

    protected StandardQuery(Table table, String command) {
        this.table = table;
        this.command = command + " " + table.getName();
    }

    public StandardQuery where(String key, Object value) {
        whereItems.put(key, value);
        return this;
    }

    // convenience since we use it so often
    public StandardQuery uuid(UUID uuid) {
        return where("uuid", uuid.toString());
    }

    public StandardQuery set(String key, Object value) {
        setItems.put(key, value);
        return this;
    }

    /**
     * Weird and janky because when a PreparedStatement is closed,
     * its ResultSet is also closed. To work around this, you must
     * pass a Function as a parameter that returns whatever the
     * getResults() method as a whole should return (type parameters ensure same type)
     * 
     * @param <T> Return type, determined by function passed in.
     * @param processResult Function to process ResultSet.
     * @return Whatever processResult() returns.
     */
    protected <T> T getResults(Function<ResultSet,T> processResult) {
        if (whereItems.size() == 0 && !command.startsWith("INSERT")) {
            throw new IllegalStateException("Should not execute non-INSERT query without WHERE clause");
        }
        StringBuilder sql = new StringBuilder(command);
        List<Object> objects = new ArrayList<>();
        addClause(sql, "SET", ", ", setItems, objects);
        addClause(sql, "WHERE", " AND ", whereItems, objects);
        if (UltraCosmeticsData.get().getPlugin().getMySqlConnectionManager().isDebug()) {
            String plaintext = sql.toString();
            for (Object obj : objects) {
                plaintext = plaintext.replaceFirst("\\?", obj == null ? "NULL" : obj.toString());
            }
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write("Executing SQL: " + plaintext);
        }
        try (Connection connection = table.getConnection(); PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < objects.size(); i++) {
                statement.setObject(i + 1, objects.get(i));
            }
            if (processResult == null) {
                statement.executeUpdate();
                return null;
            }
            ResultSet result = statement.executeQuery();
            // yes, this is required
            result.next();
            return processResult.apply(result);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void execute() {
        getResults(null);
    }

    public boolean exists() {
        return getResults(r -> {
            try {
                r.last();
                return r.getRow() > 0;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        });
    }

    public int asInt() {
        return getResults(r -> {
            try {
                return r.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    public boolean asBool() {
        return getResults(r -> {
            try {
                return r.getBoolean(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return true;
            }
        });
    }

    public String asString() {
        return getResults(r -> {
            try {
                return r.getString(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public Map<String,Object> getAll() {
        return getResults(r -> {
            try {
                Map<String,Object> columns = new HashMap<>();
                ResultSetMetaData meta = r.getMetaData();
                int columnCount = meta.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    columns.put(meta.getColumnName(i), r.getObject(i));
                }
                return columns;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    private void addClause(StringBuilder sb, String clause, String joiner, Map<String,Object> items, List<Object> objects) {
        if (items.size() == 0) return;
        sb.append(" " + clause + " ");
        StringJoiner sj = new StringJoiner(joiner);
        for (Entry<String,Object> entry : items.entrySet()) {
            sj.add(entry.getKey() + " = ?");
            objects.add(entry.getValue());
        }
        sb.append(sj.toString());
    }
}
