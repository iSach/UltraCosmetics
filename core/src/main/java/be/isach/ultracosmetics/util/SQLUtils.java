package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraPlayer;
import org.bukkit.entity.Player;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by sacha on 15/08/15.
 */
public class SQLUtils {

    private UltraCosmetics core;

    public SQLUtils(UltraCosmetics core) {
        this.core = core;
    }

    public void initStats(UltraPlayer up) {

        Player p = up.getPlayer();

        try {
            if (!core.table.select().where("uuid", p.getUniqueId().toString()).execute().next()) {
                core.table.insert().insert("uuid").value(p.getUniqueId().toString()).execute();
                core.table.update().set("username", p.getName()).where("uuid", p.getUniqueId().toString()).execute();
                return;
            } else {

                ResultSet res = core.table.select().where("uuid", p.getUniqueId().toString()).execute();
                res.first();
                String s = res.getString("username");
                if (s == null) {
                    core.table.update().set("username", p.getName()).where("uuid", p.getUniqueId().toString()).execute();
                    return;
                }
                if (!s.equals(p.getName())) {
                    core.table.update().set("username", p.getName()).where("uuid", p.getUniqueId().toString()).execute();
                }
            }

            ResultSet res = core.table.select().where("uuid", p.getUniqueId().toString()).execute();
            res.first();
            UltraPlayer.INDEXS.put(p.getUniqueId(), res.getInt("id"));

        } catch (Exception e) {
            //e.printStackTrace();
        	//exception catch when player triggered join event but offline when trigger this.
        }
    }

    public int getAmmo(int index, String name) {
        try {
            ResultSet res = core.table.select().where("id", index).execute();
            res.first();
            return res.getInt(name.replace("_", ""));
        } catch (SQLException e) {
            return 0;
        }
    }

    public String getPetName(int index, String pet) {

        try {
            ResultSet res = core.table.select().where("id", index).execute();
            res.first();
            return res.getString("name" + pet);

        } catch (SQLException e) {
            return "Unknown";
        }
    }

    public boolean exists(int index) {
        try {
            ResultSet resultSet = core.table.select().where("id", index).execute();
            resultSet.first();
            return resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public void setName(int index, String pet, String name) {
        DatabaseMetaData md = null;
        try {
            md = core.co.getMetaData();
            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "name" + pet);
            if (!rs.next()) {
                PreparedStatement statement = core.co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD name" + pet + " varchar(255)");
                statement.executeUpdate();
            }
            core.table.update().set("name" + pet, name).where("id", index).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getKeys(int index) {
        try {
            ResultSet res = core.table.select().where("id", index).execute();
            res.first();
            return res.getInt("treasureKeys");
        } catch (SQLException e) {
            return 0;
        }
    }

    public void removeKey(int index) {
        core.table.update().set("treasureKeys", getKeys(index) - 1).where("id", index).execute();
    }

    public void addKey(int index) {
        core.table.update().set("treasureKeys", getKeys(index) + 1).where("id", index).execute();
    }

    public void removeAmmo(int index, String name) {
        core.table.update().set(name.replace("_", ""), getAmmo(index, name) - 1).where("id", index).execute();
    }

    public void addAmmo(int index, String name, int i) {
        core.table.update().set(name.replace("_", ""), getAmmo(index, name) + i).where("id", index).execute();
    }

    public void setGadgetsEnabled(int index, boolean enabled) {
        DatabaseMetaData md = null;
        try {
            md = core.co.getMetaData();
            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "gadgetsEnabled");
            if (!rs.next()) {
                PreparedStatement statement = core.co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD gadgetsEnabled INT NOT NULL DEFAULT 1");
                statement.executeUpdate();
                core.table.update().set("gadgetsEnabled", 1).where("id", index).execute();
                return;
            }
            core.table.update().set("gadgetsEnabled", enabled ? 1 : 0).where("id", index).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasGadgetsEnabled(int index) {
        try {
            DatabaseMetaData md;
            md = core.co.getMetaData();
            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "gadgetsEnabled");
            if (!rs.next()) {
                PreparedStatement statement = core.co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD gadgetsEnabled INT NOT NULL DEFAULT 1");
                statement.executeUpdate();
                setGadgetsEnabled(index, true);
                return true;
            }
            ResultSet res = core.table.select().where("id", index).execute();
            res.first();
            return res.getBoolean("gadgetsEnabled");
        } catch (SQLException e) {
            return false;
        }
    }

    public void setSeeSelfMorph(int index, boolean enabled) {
        DatabaseMetaData md = null;
        try {
            md = core.co.getMetaData();
            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "selfmorphview");
            if (!rs.next()) {
                PreparedStatement statement = core.co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD selfmorphview INT NOT NULL DEFAULT 1");
                statement.executeUpdate();
                core.table.update().set("selfmorphview", 1).where("id", index).execute();
                return;
            }
            core.table.update().set("selfmorphview", enabled ? 1 : 0).where("id", index).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean canSeeSelfMorph(int index) {
        try {
            DatabaseMetaData md;
            md = core.co.getMetaData();
            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "selfmorphview");
            if (!rs.next()) {
                PreparedStatement statement = core.co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD selfmorphview INT NOT NULL DEFAULT 1");
                statement.executeUpdate();
                setGadgetsEnabled(index, true);
                return true;
            }
            ResultSet res = core.table.select().where("id", index).execute();
            res.first();
            return res.getBoolean("selfmorphview");
        } catch (SQLException e) {
            return false;
        }
    }

    public Map<UUID, Integer> getIds() {
        Map<UUID, Integer> map = new HashMap<>();
        ResultSet rs = core.table.select("*").execute();
        try {
            while (rs.next()) {
                int id = rs.getInt("id");
                String uuid = rs.getString("uuid");
                map.put(UUID.fromString(uuid), id);
            }
        } catch (SQLException ignored) {

        }
        return map;
    }
}
