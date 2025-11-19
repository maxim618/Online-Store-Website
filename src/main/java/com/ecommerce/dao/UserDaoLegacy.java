package com.ecommerce.dao;

import com.ecommerce.entities.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDaoLegacy {

	private final JdbcTemplate jdbcTemplate;

	public UserDaoLegacy(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// Маппер для User
	private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();
		user.setUserId(rs.getInt("userid"));
		user.setUserName(rs.getString("name"));
		user.setUserEmail(rs.getString("email"));
		user.setUserPassword(rs.getString("password"));
		user.setUserPhone(rs.getString("phone"));
		user.setUserGender(rs.getString("gender"));
		user.setDateTime(rs.getTimestamp("registerdate"));
		user.setUserAddress(rs.getString("address"));
		user.setUserCity(rs.getString("city"));
		user.setUserPincode(rs.getString("pincode"));
		user.setUserState(rs.getString("state"));
		return user;
	}

	public boolean saveUser(User user) {
		String sql = "INSERT INTO user(name, email, password, phone, gender, address, city, pincode, state) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		return jdbcTemplate.update(sql,
				user.getUserName(),
				user.getUserEmail(),
				user.getUserPassword(),
				user.getUserPhone(),
				user.getUserGender(),
				user.getUserAddress(),
				user.getUserCity(),
				user.getUserPincode(),
				user.getUserState()) > 0;
	}

	public User getUserByEmailPassword(String userEmail, String userPassword) {
		String sql = "SELECT * FROM user WHERE email=? AND password=?";
		List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, userEmail, userPassword);
		return users.isEmpty() ? null : users.get(0);
	}

	public List<User> getAllUser() {
		String sql = "SELECT * FROM user";
		return jdbcTemplate.query(sql, this::mapRowToUser);
	}

	public void updateUserAddresss(User user) {
		String sql = "UPDATE user SET address=?, city=?, pincode=?, state=? WHERE userid=?";
		jdbcTemplate.update(sql,
				user.getUserAddress(),
				user.getUserCity(),
				user.getUserPincode(),
				user.getUserState(),
				user.getUserId());
	}

	public void updateUserPasswordByEmail(String password, String mail) {
		String sql = "UPDATE user SET password=? WHERE email=?";
		jdbcTemplate.update(sql, password, mail);
	}

	public void updateUser(User user) {
		String sql = "UPDATE user SET name=?, email=?, phone=?, gender=?, address=?, city=?, pincode=?, state=? WHERE userid=?";
		jdbcTemplate.update(sql,
				user.getUserName(),
				user.getUserEmail(),
				user.getUserPhone(),
				user.getUserGender(),
				user.getUserAddress(),
				user.getUserCity(),
				user.getUserPincode(),
				user.getUserState(),
				user.getUserId());
	}

	public int userCount() {
		String sql = "SELECT COUNT(*) FROM user";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public String getUserAddress(int uid) {
		String sql = "SELECT CONCAT(address, ', ', city, '-', pincode, ', ', state) FROM user WHERE userid=?";
		return jdbcTemplate.queryForObject(sql, String.class, uid);
	}

	public String getUserName(int uid) {
		String sql = "SELECT name FROM user WHERE userid=?";
		return jdbcTemplate.queryForObject(sql, String.class, uid);
	}

	public String getUserEmail(int uid) {
		String sql = "SELECT email FROM user WHERE userid=?";
		return jdbcTemplate.queryForObject(sql, String.class, uid);
	}

	public String getUserPhone(int uid) {
		String sql = "SELECT phone FROM user WHERE userid=?";
		return jdbcTemplate.queryForObject(sql, String.class, uid);
	}

	public void deleteUser(int uid) {
		String sql = "DELETE FROM user WHERE userid=?";
		jdbcTemplate.update(sql, uid);
	}

	public List<String> getAllEmail() {
		String sql = "SELECT email FROM user";
		return jdbcTemplate.queryForList(sql, String.class);
	}
	public User getUserById(int uid) {
		String sql = "SELECT * FROM user WHERE userid = ?";
		List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, uid);
		return users.isEmpty() ? null : users.get(0);
	}
	public boolean existsByEmail(String email) {
		String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
		return count != null && count > 0;
	}
	public User findByEmail(String email) {
		String sql = "SELECT * FROM user WHERE email = ?";
		try {
			return jdbcTemplate.queryForObject(sql, new Object[]{email}, new BeanPropertyRowMapper<>(User.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
