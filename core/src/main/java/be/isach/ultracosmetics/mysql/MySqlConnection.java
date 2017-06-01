package be.isach.ultracosmetics.mysql;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySqlConnection {
	private final String hostname;
	private final String portNumber;
	private final String database;
	private final String username;
	private final String password;
	
	public MySqlConnection(String hostname, String portNumber, String database, String username, String password) {
		this.hostname = hostname;
		this.portNumber = portNumber;
		this.database = database;
		this.username = username;
		this.password = password;
	}
	
	public Connection getConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://" + this.hostname + ":" + this.portNumber + "/" + this.database;
		return DriverManager.getConnection(url, this.username, this.password);
	}
}
