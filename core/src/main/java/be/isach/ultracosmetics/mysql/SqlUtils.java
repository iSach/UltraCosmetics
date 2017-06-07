package be.isach.ultracosmetics.mysql;

import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.Player;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Package: be.isach.ultracosmetics.mysql
 * Created by: sacha
 * Date: 15/08/15
 * Project: UltraCosmetics
 */
public class SqlUtils {
	private MySqlConnectionManager MySqlConnectionManager;
	
	SqlUtils(MySqlConnectionManager MySqlConnectionManager) {
		this.MySqlConnectionManager = MySqlConnectionManager;
	}
	
	public void initStats(UltraPlayer up) {
		Player p = up.getBukkitPlayer();
		
		try {
			if (!MySqlConnectionManager.getTable().select().where("uuid", p.getUniqueId().toString()).execute()
			                           .next()) {
				MySqlConnectionManager.getTable().insert().insert("uuid").value(p.getUniqueId().toString()).execute();
				MySqlConnectionManager.getTable().update().set("username", p.getName())
				                      .where("uuid", p.getUniqueId().toString()).execute();
				return;
			} else {
				ResultSet res = MySqlConnectionManager.getTable().select().where("uuid", p.getUniqueId().toString())
				                                      .execute();
				res.first();
				String s = res.getString("username");
				if (s == null) {
					MySqlConnectionManager.getTable().update().set("username", p.getName())
					                      .where("uuid", p.getUniqueId().toString()).execute();
					return;
				}
				if (!s.equals(p.getName())) {
					MySqlConnectionManager.getTable().update().set("username", p.getName())
					                      .where("uuid", p.getUniqueId().toString()).execute();
				}
				res.close();
			}
			
			ResultSet res = MySqlConnectionManager.getTable().select().where("uuid", p.getUniqueId().toString())
			                                      .execute();
			res.first();
			be.isach.ultracosmetics.mysql.MySqlConnectionManager.INDEXS.put(p.getUniqueId(), res.getInt("id"));
			res.close();
		} catch (Exception e) {
			// Triggered when user is already offline when this method is invoked.
		}
	}
	
	public int getAmmo(int index, String name) {
		ResultSet res = null;
		try {
			res = MySqlConnectionManager.getTable().select().where("id", index).execute();
			res.first();
			return res.getInt(name.replace("_", ""));
		} catch (SQLException e) {
			return 0;
		} finally {
			if (res != null)
				try {
					res.close();
				} catch (SQLException ignored) {
				}
		}
	}
	
	public String getPetName(int index, String pet) {
		ResultSet res = null;
		try {
			res = MySqlConnectionManager.getTable().select().where("id", index).execute();
			res.first();
			return res.getString("name" + pet);
		} catch (SQLException e) {
			return "Unknown";
		} finally {
			if (res != null)
				try {
					res.close();
				} catch (SQLException ignored) {
				}
		}
	}
	
	public boolean exists(int index) {
		ResultSet resultSet = null;
		try {
			resultSet = MySqlConnectionManager.getTable().select().where("id", index).execute();
			resultSet.first();
			return resultSet.next();
		} catch (SQLException e) {
			return false;
		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException ignored) {
				}
		}
	}
	
	public void setName(int index, String pet, String name) {
		DatabaseMetaData md;
		ResultSet rs = null;
		try {
			md = MySqlConnectionManager.co.getMetaData();
			rs = md.getColumns(null, null, "UltraCosmeticsData", "name" + pet);
			if (!rs.next()) {
				PreparedStatement statement = MySqlConnectionManager.co
						.prepareStatement("ALTER TABLE UltraCosmeticsData ADD name" + pet + " varchar(255)");
				statement.executeUpdate();
				statement.close();
			}
			MySqlConnectionManager.getTable().update().set("name" + pet, name).where("id", index).execute();
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
	
	public int getKeys(int index) {
		ResultSet res = null;
		try {
			res = MySqlConnectionManager.getTable().select().where("id", index).execute();
			res.first();
			return res.getInt("treasureKeys");
		} catch (SQLException e) {
			return 0;
		} finally {
			if (res != null)
				try {
					res.close();
				} catch (SQLException ignored) {
				}
		}
	}
	
	public void removeKey(int index) {
		MySqlConnectionManager.getTable().update().set("treasureKeys", getKeys(index) - 1).where("id", index).execute();
	}
	
	public void addKey(int index) {
		MySqlConnectionManager.getTable().update().set("treasureKeys", getKeys(index) + 1).where("id", index).execute();
	}
	
	public void removeAmmo(int index, String name) {
		MySqlConnectionManager.getTable().update().set(name.replace("_", ""), getAmmo(index, name) - 1)
		                      .where("id", index).execute();
	}
	
	public void addAmmo(int index, String name, int i) {
		MySqlConnectionManager.getTable().update().set(name.replace("_", ""), getAmmo(index, name) + i)
		                      .where("id", index).execute();
	}
	
	public void setGadgetsEnabled(int index, boolean enabled) {
		DatabaseMetaData md;
		ResultSet rs = null;
		try {
			md = MySqlConnectionManager.co.getMetaData();
			rs = md.getColumns(null, null, "UltraCosmeticsData", "gadgetsEnabled");
			if (!rs.next()) {
				PreparedStatement statement = MySqlConnectionManager.co
						.prepareStatement("ALTER TABLE UltraCosmeticsData ADD gadgetsEnabled INT NOT NULL DEFAULT 1");
				statement.executeUpdate();
				statement.close();
				MySqlConnectionManager.getTable().update().set("gadgetsEnabled", 1).where("id", index).execute();
				return;
			}
			MySqlConnectionManager.getTable().update().set("gadgetsEnabled", enabled ? 1 : 0).where("id", index)
			                      .execute();
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
	
	public boolean hasGadgetsEnabled(int index) {
		DatabaseMetaData md;
		ResultSet rs = null;
		try {
			md = MySqlConnectionManager.co.getMetaData();
			rs = md.getColumns(null, null, "UltraCosmeticsData", "gadgetsEnabled");
			if (!rs.next()) {
				PreparedStatement statement = MySqlConnectionManager.co
						.prepareStatement("ALTER TABLE UltraCosmeticsData ADD gadgetsEnabled INT NOT NULL DEFAULT 1");
				statement.executeUpdate();
				statement.close();
				setGadgetsEnabled(index, true);
				return true;
			}
			ResultSet res = MySqlConnectionManager.getTable().select().where("id", index).execute();
			res.first();
			return res.getBoolean("gadgetsEnabled");
		} catch (SQLException e) {
			return false;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException ignored) {
				}
		}
	}
	
	public void setSeeSelfMorph(int index, boolean enabled) {
		DatabaseMetaData md;
		ResultSet rs = null;
		try {
			md = MySqlConnectionManager.co.getMetaData();
			rs = md.getColumns(null, null, "UltraCosmeticsData", "selfmorphview");
			if (!rs.next()) {
				PreparedStatement statement = MySqlConnectionManager.co
						.prepareStatement("ALTER TABLE UltraCosmeticsData ADD selfmorphview INT NOT NULL DEFAULT 1");
				statement.executeUpdate();
				statement.close();
				MySqlConnectionManager.getTable().update().set("selfmorphview", 1).where("id", index).execute();
				return;
			}
			MySqlConnectionManager.getTable().update().set("selfmorphview", enabled ? 1 : 0).where("id", index)
			                      .execute();
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
	
	public boolean canSeeSelfMorph(int index) {
		DatabaseMetaData md;
		ResultSet rs = null;
		try {
			md = MySqlConnectionManager.co.getMetaData();
			rs = md.getColumns(null, null, "UltraCosmeticsData", "selfmorphview");
			if (!rs.next()) {
				PreparedStatement statement = MySqlConnectionManager.co
						.prepareStatement("ALTER TABLE UltraCosmeticsData ADD selfmorphview INT NOT NULL DEFAULT 1");
				statement.executeUpdate();
				statement.close();
				setGadgetsEnabled(index, true);
				return true;
			}
			ResultSet res = MySqlConnectionManager.getTable().select().where("id", index).execute();
			res.first();
			return res.getBoolean("selfmorphview");
		} catch (SQLException e) {
			return false;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException ignored) {
				}
		}
	}
	
	public Map<UUID, Integer> getIds() {
		Map<UUID, Integer> map = new HashMap<>();
		ResultSet rs = MySqlConnectionManager.getTable().select("*").execute();
		try {
			while (rs.next()) {
				int id = rs.getInt("id");
				String uuid = rs.getString("uuid");
				map.put(UUID.fromString(uuid), id);
			}
		} catch (SQLException ignored) {
		} finally {
			try {
				rs.close();
			} catch (SQLException ignored) {
			}
		}
		return map;
	}
}
