package com.ecommerce.dao;

import com.ecommerce.entities.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OrderDao {
	private final JdbcTemplate jdbcTemplate;

	public OrderDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// 🔹 Создание нового заказа
	public boolean placeOrder(Order order) {
		String sql = "INSERT INTO `order` (orderid, status, paymentType, userId) VALUES (?, ?, ?, ?)";
		int rows = jdbcTemplate.update(sql,
				order.getOrderId(),
				order.getStatus(),
				order.getPaymentType(),
				order.getUserId());
		return rows > 0;
	}

	// 🔹 Получить все заказы пользователя
	public List<Order> getOrdersByUserId(int userId) {
		String sql = "SELECT * FROM `order` WHERE userId = ?";
		return jdbcTemplate.query(sql, this::mapRowToOrder, userId);
	}

	// 🔹 Обновить статус заказа (например, для админа)
	public void updateOrderStatus(int id, String status) {
		String sql = "UPDATE `order` SET status = ? WHERE id = ?";
		jdbcTemplate.update(sql, status, id);
	}

	// 🔹 Маппер
	private Order mapRowToOrder(ResultSet rs, int rowNum) throws SQLException {
		Order order = new Order();
		order.setId(rs.getInt("id"));
		order.setOrderId(rs.getString("orderid"));
		order.setStatus(rs.getString("status"));
		order.setPaymentType(rs.getString("paymentType"));
		order.setUserId(rs.getInt("userId"));
		order.setDate(rs.getTimestamp("date").toLocalDateTime());
		return order;
	}
}
