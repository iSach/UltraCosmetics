package be.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SelectQuery extends WhereQuery {
    public SelectQuery(Connection connection, String sql) {
        super(connection, sql);
    }

    @Override
    protected Optional<ResultSet> executeStatement(PreparedStatement statement) throws SQLException {
        return Optional.of(statement.executeQuery());
    }

    @Override
    public boolean asBoolean() throws SQLException {
        return execute().get().getBoolean(1);
    }

    @Override
    public int asInt() throws SQLException {
        return execute().get().getInt(1);
    }
}
