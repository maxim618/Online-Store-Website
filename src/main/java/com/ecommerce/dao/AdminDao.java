package com.ecommerce.dao;


import com.ecommerce.entities.Admin;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdminDao {

	private final JdbcTemplate jdbcTemplate;

	public AdminDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// Маппер для Admin
	private Admin mapRowToAdmin(ResultSet rs, int rowNum) throws SQLException {
		Admin admin = new Admin();
		admin.setId(rs.getInt("id"));
		admin.setName(rs.getString("name"));
		admin.setEmail(rs.getString("email"));
		admin.setPassword(rs.getString("password"));
		admin.setPhone(rs.getString("phone"));
		return admin;
	}

	public boolean saveAdmin(Admin admin) {
		String sql = "INSERT INTO admin(name, email, password, phone) VALUES (?, ?, ?, ?)";
		return jdbcTemplate.update(sql,
				admin.getName(),
				admin.getEmail(),
				admin.getPassword(),
				admin.getPhone()) > 0;
	}

	public Admin getAdminByEmailPassword(String email, String password) {
		String sql = "SELECT * FROM admin WHERE email=? AND password=?";
		List<Admin> admins = jdbcTemplate.query(sql, this::mapRowToAdmin, email, password);
		return admins.isEmpty() ? null : admins.get(0);
	}

	public List<Admin> getAllAdmin() {
		String sql = "SELECT * FROM admin";
		return jdbcTemplate.query(sql, this::mapRowToAdmin);
	}

	public boolean deleteAdmin(int id) {
		String sql = "DELETE FROM admin WHERE id=?";
		return jdbcTemplate.update(sql, id) > 0;
	}
}
