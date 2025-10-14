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

	// маппер
	private Admin mapRowToAdmin(ResultSet rs, int rowNum) throws SQLException {
		Admin admin = new Admin();
		admin.setId(rs.getInt("id"));
		admin.setName(rs.getString("name"));
		admin.setEmail(rs.getString("email"));
		admin.setPassword(rs.getString("password"));
		return admin;
	}

	// получить всех админов
	public List<Admin> getAllAdmins() {
		String sql = "SELECT * FROM admin";
		return jdbcTemplate.query(sql, this::mapRowToAdmin);
	}

	// проверить уникальность email
	public boolean existsByEmail(String email) {
		String sql = "SELECT COUNT(*) FROM admin WHERE email = ?";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
		return count != null && count > 0;
	}

	// добавить админа
	public boolean addAdmin(Admin admin) {
		if (existsByEmail(admin.getEmail())) {
			return false; // уже существует
		}
		String sql = "INSERT INTO admin (name, email, password) VALUES (?, ?, ?)";
		return jdbcTemplate.update(sql,
				admin.getName(),
				admin.getEmail(),
				admin.getPassword()) > 0;
	}

	// удалить админа
	public void deleteAdmin(int id) {
		String sql = "DELETE FROM admin WHERE id = ?";
		jdbcTemplate.update(sql, id);
	}
	public Admin getAdminByEmail(String email) {
		String sql = "SELECT * FROM admin WHERE email = ?";
		List<Admin> admins = jdbcTemplate.query(sql, this::mapRowToAdmin, email);
		return admins.isEmpty() ? null : admins.get(0);
	}


}
