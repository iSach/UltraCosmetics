package be.isach.ultracosmetics.mysql;

import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Package: be.isach.ultracosmetics.mysql
 * Created by: sacha
 * Date: 15/08/15
 * Project: UltraCosmetics
 */
public class SqlUtils {
    private MySqlConnectionManager connectionManager;

    public SqlUtils(MySqlConnectionManager MySqlConnectionManager) {
        this.connectionManager = MySqlConnectionManager;
    }

    public void initStats(UltraPlayer up) {
        Player p = up.getBukkitPlayer();
        // TODO: username is never retrieved from the database, only set, so remove it.
        try {
            if (!connectionManager.getTable().select().uuid(p.getUniqueId()).execute().get().next()) {
                connectionManager.getTable().insert().insert("uuid").value(p.getUniqueId()).execute();
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getAmmo(UUID uuid, String name) {
        ResultSet res = null;
        try {
            res = connectionManager.getTable().select().uuid(uuid).execute().get();
            res.first();
            return res.getInt(name.replace("_", ""));
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (res != null)
                try {
                    res.close();
                } catch (SQLException ignored) {
                }
        }
    }

    public String getPetName(UUID uuid, String pet) {
        ResultSet res = null;
        try {
            res = connectionManager.getTable().select().uuid(uuid).execute().get();
            res.first();
            return res.getString("name" + pet);
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown";
        } finally {
            if (res != null)
                try {
                    res.close();
                } catch (SQLException ignored) {
                }
        }
    }

    public void setName(UUID uuid, String pet, String name) {
        DatabaseMetaData md;
        ResultSet rs = null;
        try {
            md = connectionManager.co.getMetaData();
            rs = md.getColumns(null, null, "UltraCosmeticsData", "name" + pet);
            if (!rs.next()) {
                PreparedStatement statement = connectionManager.co
                        .prepareStatement("ALTER TABLE UltraCosmeticsData ADD name" + pet + " varchar(255)");
                statement.executeUpdate();
                statement.close();
            }
            connectionManager.getTable().update().set("name" + pet, name).uuid(uuid).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
        }
    }

    public int getKeys(UUID uuid) {
        ResultSet res = null;
        try {
            res = connectionManager.getTable().select().uuid(uuid).execute().get();
            res.first();
            return res.getInt("treasureKeys");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public void removeKey(UUID uuid) {
        connectionManager.getTable().update().set("treasureKeys", getKeys(uuid) - 1).where("uuid", uuid).execute();
    }

    public void addKey(UUID uuid) {
        connectionManager.getTable().update().set("treasureKeys", getKeys(uuid) + 1).where("uuid", uuid).execute();
    }

    public void removeAmmo(UUID uuid, String name) {
        addAmmo(uuid, name, -1);
    }

    public void addAmmo(UUID uuid, String name, int i) {
        connectionManager.getTable().update().set(name.replace("_", ""), getAmmo(uuid, name) + i).where("uuid", uuid).execute();
    }

    public void setGadgetsEnabled(UUID uuid, boolean enabled) {
        DatabaseMetaData md;
        ResultSet rs = null;
        try {
            md = connectionManager.co.getMetaData();
            rs = md.getColumns(null, null, "UltraCosmeticsData", "gadgetsEnabled");
            if (!rs.next()) {
                PreparedStatement statement = connectionManager.co
                        .prepareStatement("ALTER TABLE UltraCosmeticsData ADD gadgetsEnabled INT NOT NULL DEFAULT 1");
                statement.executeUpdate();
                statement.close();
                connectionManager.getTable().update().set("gadgetsEnabled", 1).where("uuid", uuid).execute();
                return;
            }
            connectionManager.getTable().update().set("gadgetsEnabled", enabled ? 1 : 0).where("uuid", uuid).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public boolean hasGadgetsEnabled(UUID uuid) {
        ResultSet rs = null;
        try (Connection co = connectionManager.getDataSource().getConnection()) {
            ResultSet res = connectionManager.getTable().select("gadgetsEnabled").where("uuid", uuid).execute().get();
            return res.getBoolean(1);
        } catch (SQLException e) {
            return false;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public void setSeeSelfMorph(UUID uuid, boolean enabled) {
        connectionManager.getTable().update().set("selfmorphview", enabled ? 1 : 0).where("uuid", uuid).execute();
    }

    public boolean canSeeSelfMorph(UUID uuid) {
        ResultSet rs = null;
        try {
            return connectionManager.getTable().select("selfmorphview").where("uuid", uuid).asBoolean();
        } catch (SQLException e) {
            return false;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private boolean getBoolean(UUID uuid, String key) throws SQLException {
        return getResults(uuid, key).getBoolean(1);
    }

    private int getInt(UUID uuid, String key) throws SQLException {
        return getResults(uuid, key).getInt(1);
    }

    private ResultSet getResults(UUID uuid, String key) {
        try (Connection co = connectionManager.getDataSource().getConnection(); 
                PreparedStatement statement = co.prepareStatement("SELECT " + key + " FROM " + MySqlConnectionManager.TABLE_NAME + " WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
