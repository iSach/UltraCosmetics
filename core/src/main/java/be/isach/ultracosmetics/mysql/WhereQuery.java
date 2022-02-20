package be.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.log.SmartLogger.LogLevel;

public abstract class WhereQuery extends Query {
    protected boolean firstValue = true;
    protected List<String> values = new ArrayList<>();
    protected StringJoiner keys = new StringJoiner(" AND ");
    public WhereQuery(Connection connection, String sql) {
        super(connection, sql);
    }

    public WhereQuery where(String key, Object value) {
        keys.add(key + " = ?");
        values.add(value.toString());
        return this;
    }

    public WhereQuery uuid(UUID uuid) {
        return where("uuid", uuid);
    }

    protected void finalizeSQL() {
        if (keys.length() > 0) {
            sql += " WHERE " + keys.toString();
        }
    }

    // returns new next index
    protected int setObjects(int nextIndex) {
        return nextIndex;
    }

    public Optional<ResultSet> execute() {
        finalizeSQL();
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            if (connection == null) {
                UltraCosmeticsData.get().getPlugin().getSmartLogger().write(LogLevel.WARNING, "No Connection!");
                return Optional.empty();
            }
            int i = 1;
            for (String string : values) {
                statement.setString(i++, string);
            }
            setObjects(i);
            Optional<ResultSet> result = executeStatement(statement);
            if (!result.isPresent()) statement.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // for SelectQuery
    public boolean asBoolean() throws SQLException {
        return true;
    }

    public int asInt() throws SQLException {
        return 0;
    }

    protected abstract Optional<ResultSet> executeStatement(PreparedStatement statement) throws SQLException;
}
