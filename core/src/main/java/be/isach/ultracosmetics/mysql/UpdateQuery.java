package be.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class UpdateQuery extends WhereQuery {
    protected StringJoiner newValuesJoiner = new StringJoiner(", ");
    protected List<Object> newValues = new ArrayList<>();

    public UpdateQuery(Connection connection, String sql) {
        super(connection, sql);
    }

    public UpdateQuery set(String field, Object value) {
        newValuesJoiner.add(field + " = ?");
        newValues.add(value);
        return this;
    }

    @Override
    protected void finalizeSQL() {
        super.finalizeSQL();
        if (newValuesJoiner.length() > 0) {
            sql += " " + newValuesJoiner.toString();
        }
    }

    @Override
    protected int setObjects(int nextIndex) {
        int i = nextIndex;
        try {
            for (; i < nextIndex + newValues.size(); i++) {
                statement.setObject(i, newValues.get(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    protected Optional<ResultSet> executeStatement() throws SQLException {
        statement.executeUpdate();
        return Optional.empty();
    }
}
