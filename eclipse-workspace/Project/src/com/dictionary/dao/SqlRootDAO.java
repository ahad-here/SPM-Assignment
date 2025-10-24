package com.dictionary.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.dictionary.dto.RootDTO;

public class SqlRootDAO implements IRootDAO {
	private String TABLE_NAME = "root";
	private DBConnection db;

	
	@Override
	public RootDTO createRoot(RootDTO root) {
		String query = "INSERT INTO " + TABLE_NAME + " (root_letters) VALUES (?)";
		try (Connection connection = DBConnection.getConnection();){
			PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			System.out.println("hi");
			stmt.setString(1, root.getRootLetters());
			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Creating root failed, no rows affected.");
			}
			return root;
		} catch (SQLException e) {
			throw new RuntimeException("Database error creating root.", e);
		}
	}

	@Override
	public RootDTO getRootById(int id) {
		String sql = "SELECT ID, root_letters FROM " + TABLE_NAME + " WHERE ID = ?";
		try (Connection connection = DBConnection.getConnection();) {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return new RootDTO(rs.getInt("ID"), rs.getString("root_letters"));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Database error reading root by ID.", e);
		}
		return null;
	}

	@Override
	public RootDTO getRootByLetters(String rootLetters) {
		String sql = "SELECT ID, root_letters FROM " + TABLE_NAME + " WHERE root_letters = ?";
		try (Connection connection = DBConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setString(1, rootLetters);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return new RootDTO(rs.getInt("ID"), rs.getString("root_letters"));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Database error reading root by letters: " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<RootDTO> getAllRoots() {
		List<RootDTO> roots = new ArrayList<>();
		String sql = "SELECT ID, root_letters FROM " + TABLE_NAME + " ORDER BY root_letters ASC";
		try (Connection connection = DBConnection.getConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				roots.add(new RootDTO(rs.getInt("ID"), rs.getString("root_letters")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Database error reading all roots: " + e.getMessage(), e);
		}
		return roots;
	}

	@Override
	public void updateRoot(RootDTO root) {
		String sql = "UPDATE " + TABLE_NAME + " SET root_letters = ? WHERE ID = ?";
		try (Connection connection = DBConnection.getConnection();) {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1, root.getRootLetters());
			stmt.setLong(2, root.getId());

			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Updating root failed, ID " + root.getId() + " not found.");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Database error updating root.", e);
		}
	}

	@Override
	public void deleteRoot(int id) {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE ID = ?";
		try (Connection connection = DBConnection.getConnection();) {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);
			if (stmt.executeUpdate() == 0) {
				System.out.println("Warning: Root ID " + id + " not found for deletion.");
			}

		} catch (SQLException e) {
			throw new RuntimeException("Database error deleting root.", e);
		}
	}
}