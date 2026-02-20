package com.store.app.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection {
	private static Connection con = null;

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		if (con == null) {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(
  "jdbc:mysql://mysql:3306/estore?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
  "estore_user",
  "estore_pass"
);
			//System.out.println("Connected");
		}
		return con;
	}
}
