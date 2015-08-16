package me.isach.ultracosmetics;

import org.bukkit.entity.Player;

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

    public void removeAmmo(Player p, String name) {
        core.table.update().set(name, getAmmo(p, name) - 1).where("uuid", p.getUniqueId().toString()).execute();
    }

    public void addAmmo(Player p, String name, int i) {
        core.table.update().set(name, getAmmo(p, name) + i).where("uuid", p.getUniqueId().toString()).execute();
    }

}
