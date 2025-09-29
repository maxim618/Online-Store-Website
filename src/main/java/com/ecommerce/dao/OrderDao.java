package com.ecommerce.dao;

import com.ecommerce.entities.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class OrderDao {

	private final JdbcTemplate jdbcTemplate;

	public OrderDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// Маппер
	private Order mapRowToOrder(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
		Order order = new Order();
		order.setId(rs.getInt("id"));
		order.setOrderId(rs.getString("orderid"));
		order.setStatus(rs.getString("status"));
		order.setDate(rs.getTimestamp("date"));
		order.setPayementType(rs.getString("paymentType"));
		order.setUserId(rs.getInt("userId"));
		return order;
	}

	public int insertOrder(Order order) {
		String sql = "INSERT INTO `order` (orderid, status, paymentType, userId) VALUES (?, ?, ?, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, order.getOrderId());
			ps.setString(2, order.getStatus());
			ps.setString(3, order.getPayementType());
			ps.setInt(4, order.getUserId());
			return ps;
		}, keyHolder);

		return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
	}

	public List<Order> getAllOrderByUserId(int uid) {
		String sql = "SELECT * FROM `order` WHERE userId = ?";
		return jdbcTemplate.query(sql, this::mapRowToOrder, uid);
	}

	public Order getOrderById(int id) {
		String sql = "SELECT * FROM `order` WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, this::mapRowToOrder, id);
	}

	public List<Order> getAllOrder() {
		String sql = "SELECT * FROM `order`";
		return jdbcTemplate.query(sql, this::mapRowToOrder);
	}

	public void updateOrderStatus(int oid, String status) {
		String sql = "UPDATE `order` SET status=? WHERE id=?";
		jdbcTemplate.update(sql, status, oid);
	}
}
