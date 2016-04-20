package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmetics;
import org.bukkit.entity.Player;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by sacha on 15/08/15.
 */
public class SQLUtils {

    private UltraCosmetics core;

    public SQLUtils(UltraCosmetics core) {
        this.core = core;
    }

    public void initStats(Player p) {

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

        } catch (Exception e) {
            //e.printStackTrace();
        	//exception catch when player triggered join event but offline when trigger this.
        }
    }

    public int getAmmo(UUID uuid, String name) {
        try {
            ResultSet res = core.table.select().where("uuid", uuid.toString()).execute();
            res.first();
            return res.getInt(name.replace("_", ""));
        } catch (SQLException e) {
            return 0;
        }
    }

    public String getPetName(Player p, String pet) {

        try {
            ResultSet res = core.table.select().where("uuid", p.getUniqueId().toString()).execute();
            res.first();
            return res.getString("name" + pet);

        } catch (SQLException e) {
            return "Unknown";
        }
    }

    public boolean exists(UUID uuid) {
        try {
            ResultSet resultSet = core.table.select().where("uuid", uuid.toString()).execute();
            resultSet.first();
            return resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public void setName(Player p, String pet, String name) {
        DatabaseMetaData md = null;
        try {
            md = core.co.getMetaData();
            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "name" + pet);
            if (!rs.next()) {
                PreparedStatement statement = core.co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD name" + pet + " varchar(255)");
                statement.executeUpdate();
            }
            core.table.update().set("name" + pet, name).where("uuid", p.getUniqueId().toString()).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getKeys(UUID uuid) {
        try {
            ResultSet res = core.table.select().where("uuid", uuid.toString()).execute();
            res.first();
            return res.getInt("treasureKeys");
        } catch (SQLException e) {
            return 0;
        }
    }

    public void removeKey(UUID uuid) {
        core.table.update().set("treasureKeys", getKeys(uuid) - 1).where("uuid", uuid.toString()).execute();
    }

    public void addKey(UUID uuid) {
        core.table.update().set("treasureKeys", getKeys(uuid) + 1).where("uuid", uuid.toString()).execute();
    }

    public void removeAmmo(UUID uuid, String name) {
        core.table.update().set(name.replace("_", ""), getAmmo(uuid, name) - 1).where("uuid", uuid.toString()).execute();
    }

    public void addAmmo(UUID uuid, String name, int i) {
        core.table.update().set(name.replace("_", ""), getAmmo(uuid, name) + i).where("uuid", uuid.toString()).execute();
    }

    public void setGadgetsEnabled(Player p, boolean enabled) {
        DatabaseMetaData md = null;
        try {
            md = core.co.getMetaData();
            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "gadgetsEnabled");
            if (!rs.next()) {
                PreparedStatement statement = core.co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD gadgetsEnabled INT NOT NULL DEFAULT 1");
                statement.executeUpdate();
                core.table.update().set("gadgetsEnabled", 1).where("uuid", p.getUniqueId().toString()).execute();
                return;
            }
            core.table.update().set("gadgetsEnabled", enabled ? 1 : 0).where("uuid", p.getUniqueId().toString()).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasGadgetsEnabled(Player p) {
        try {
            DatabaseMetaData md;
            md = core.co.getMetaData();
            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "gadgetsEnabled");
            if (!rs.next()) {
                PreparedStatement statement = core.co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD gadgetsEnabled INT NOT NULL DEFAULT 1");
                statement.executeUpdate();
                setGadgetsEnabled(p, true);
                return true;
            }
            ResultSet res = core.table.select().where("uuid", p.getUniqueId().toString()).execute();
            res.first();
            return res.getBoolean("gadgetsEnabled");
        } catch (SQLException e) {
            return false;
        }
    }

    public void setSeeSelfMorph(Player p, boolean enabled) {
        DatabaseMetaData md = null;
        try {
            md = core.co.getMetaData();
            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "selfmorphview");
            if (!rs.next()) {
                PreparedStatement statement = core.co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD selfmorphview INT NOT NULL DEFAULT 1");
                statement.executeUpdate();
                core.table.update().set("selfmorphview", 1).where("uuid", p.getUniqueId().toString()).execute();
                return;
            }
            core.table.update().set("selfmorphview", enabled ? 1 : 0).where("uuid", p.getUniqueId().toString()).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean canSeeSelfMorph(Player p) {
        try {
            DatabaseMetaData md;
            md = core.co.getMetaData();
            ResultSet rs = md.getColumns(null, null, "UltraCosmeticsData", "selfmorphview");
            if (!rs.next()) {
                PreparedStatement statement = core.co.prepareStatement("ALTER TABLE UltraCosmeticsData ADD selfmorphview INT NOT NULL DEFAULT 1");
                statement.executeUpdate();
                setGadgetsEnabled(p, true);
                return true;
            }
            ResultSet res = core.table.select().where("uuid", p.getUniqueId().toString()).execute();
            res.first();
            return res.getBoolean("selfmorphview");
        } catch (SQLException e) {
            return false;
        }
    }
}
