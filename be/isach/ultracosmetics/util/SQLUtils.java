package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.Core;
import org.bukkit.entity.Player;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sacha on 15/08/15.
 */
public class SQLUtils {

    private Core core;

    public SQLUtils(Core core) {
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
            e.printStackTrace();
        }
    }

    public int getAmmo(Player p, String name) {

        try {
            ResultSet res = core.table.select().where("uuid", p.getUniqueId().toString()).execute();
            res.first();
            return res.getInt(name);

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

    public int getKeys(Player p) {
        try {
            ResultSet res = core.table.select().where("uuid", p.getUniqueId().toString()).execute();
            res.first();
            return res.getInt("treasureKeys");
        } catch (SQLException e) {
            return 0;
        }
    }

    public void removeKey(Player p) {
        core.table.update().set("treasureKeys", getKeys(p) - 1).where("uuid", p.getUniqueId().toString()).execute();
    }

    public void addKey(Player p) {
        core.table.update().set("treasureKeys", getKeys(p) + 1).where("uuid", p.getUniqueId().toString()).execute();
    }

    public void removeAmmo(Player p, String name) {
        core.table.update().set(name, getAmmo(p, name) - 1).where("uuid", p.getUniqueId().toString()).execute();
    }

    public void addAmmo(Player p, String name, int i) {
        core.table.update().set(name, getAmmo(p, name) + i).where("uuid", p.getUniqueId().toString()).execute();
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
