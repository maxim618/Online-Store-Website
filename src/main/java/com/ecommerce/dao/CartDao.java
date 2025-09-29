package com.ecommerce.dao;

import com.ecommerce.entities.Cart;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CartDao {

	private final JdbcTemplate jdbcTemplate;

	public CartDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// Маппер
	private Cart mapRowToCart(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
		Cart cart = new Cart();
		cart.setCartId(rs.getInt("id"));
		cart.setUserId(rs.getInt("uid"));
		cart.setProductId(rs.getInt("pid"));
		cart.setQuantity(rs.getInt("quantity"));
		return cart;
	}

	public boolean addToCart(Cart cart) {
		String sql = "INSERT INTO cart(uid, pid, quantity) VALUES(?, ?, ?)";
		return jdbcTemplate.update(sql, cart.getUserId(), cart.getProductId(), cart.getQuantity()) > 0;
	}

	public List<Cart> getCartListByUserId(int uid) {
		String sql = "SELECT * FROM cart WHERE uid = ?";
		return jdbcTemplate.query(sql, this::mapRowToCart, uid);
	}

	public int getQuantity(int uid, int pid) {
		String sql = "SELECT quantity FROM cart WHERE uid = ? AND pid = ?";
		return jdbcTemplate.queryForObject(sql, Integer.class, uid, pid);
	}

	public int getQuantityById(int id) {
		String sql = "SELECT quantity FROM cart WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, Integer.class, id);
	}

	public void updateQuantity(int id, int qty) {
		String sql = "UPDATE cart SET quantity = ? WHERE id = ?";
		jdbcTemplate.update(sql, qty, id);
	}

	public void removeProduct(int cid) {
		String sql = "DELETE FROM cart WHERE id = ?";
		jdbcTemplate.update(sql, cid);
	}

	public void removeAllProduct() {
		String sql = "DELETE FROM cart";
		jdbcTemplate.update(sql);
	}

	public int getIdByUserIdAndProductId(int uid, int pid) {
		String sql = "SELECT id FROM cart WHERE uid = ? AND pid = ?";
		return jdbcTemplate.queryForObject(sql, Integer.class, uid, pid);
	}

	public int getCartCountByUserId(int uid) {
		String sql = "SELECT COUNT(*) FROM cart WHERE uid = ?";
		return jdbcTemplate.queryForObject(sql, Integer.class, uid);
	}

	public int getProductId(int cid) {
		String sql = "SELECT pid FROM cart WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, Integer.class, cid);
	}
}
