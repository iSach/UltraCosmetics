package be.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DeleteQuery extends WhereQuery {
    public DeleteQuery(Connection connection, String sql) {
        super(connection, sql);
    }

    @Override
    protected Optional<ResultSet> executeStatement(PreparedStatement statement) throws SQLException {
        statement.executeUpdate();
        return Optional.empty();
    }
}
