package com.dictionary.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private static final String URL = "jdbc:mysql://localhost:3306/urdu_dictionary";
	private static final String USER = "root";
	private static final String PASSWORD = "123";

	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("MySQL JDBC Driver not found in classpath.", e);
		}
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}