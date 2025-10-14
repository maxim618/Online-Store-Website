package com.ecommerce.dao;

import com.ecommerce.entities.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class OrderDao {
	private final JdbcTemplate jdbcTemplate;

	public OrderDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	/** ✅ Получить заказ по числовому первичному ключу id */
	public Order getOrderById(int id) {
		String sql = "SELECT * FROM `order` WHERE id = ?";
		List<Order> list = jdbcTemplate.query(sql, this::mapRowToOrder, id);
		return list.isEmpty() ? null : list.get(0);
	}

	// Создание нового заказа
	public boolean placeOrder(Order order) {
		String sql = "INSERT INTO `order` (orderid, status, paymentType, userId, date) VALUES (?, ?, ?, ?, ?)";
		int rows = jdbcTemplate.update(sql,
				order.getOrderId(),
				order.getStatus(),
				order.getPaymentType(),
				order.getUserId(),
				Timestamp.valueOf(order.getDate()) // 👈 конвертация LocalDateTime -> Timestamp
		);
		return rows > 0;
	}

	// Получить все заказы пользователя
	public List<Order> getOrdersByUserId(int userId) {
		String sql = "SELECT * FROM `order` WHERE userId = ?";
		return jdbcTemplate.query(sql, this::mapRowToOrder, userId);
	}

	// Обновить статус заказа (например, для админа)
	public void updateOrderStatus(int id, String status) {
		String sql = "UPDATE `order` SET status = ? WHERE id = ?";
		jdbcTemplate.update(sql, status, id);
	}

	// Получить все заказы (для админки)
	public List<Order> getAllOrders() {
		String sql = "SELECT * FROM `order`";
		return jdbcTemplate.query(sql, this::mapRowToOrder);
	}

	// Маппер
	private Order mapRowToOrder(ResultSet rs, int rowNum) throws SQLException {
		Order order = new Order();
		order.setId(rs.getInt("id"));
		order.setOrderId(rs.getString("orderid"));
		order.setStatus(rs.getString("status"));
		order.setPaymentType(rs.getString("paymentType"));
		order.setUserId(rs.getInt("userId"));

		java.sql.Timestamp ts = rs.getTimestamp("date");
		if (ts != null) {
			order.setDate(ts.toLocalDateTime()); // ✅ конвертация Timestamp → LocalDateTime
		}

		return order;
	}
	/** (опционально) Получить заказ по строковому orderId */
	public Order getOrderByOrderId(String orderId) {
		String sql = "SELECT * FROM `order` WHERE orderid = ?";
		List<Order> list = jdbcTemplate.query(sql, this::mapRowToOrder, orderId);
		return list.isEmpty() ? null : list.get(0);
	}

}
