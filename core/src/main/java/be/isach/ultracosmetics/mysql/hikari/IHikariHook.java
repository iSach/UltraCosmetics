package be.isach.ultracosmetics.mysql.hikari;

import javax.sql.DataSource;

public interface IHikariHook {
    public DataSource getDataSource();
    public void close();
}
