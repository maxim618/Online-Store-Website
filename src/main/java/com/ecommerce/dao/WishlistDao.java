package com.ecommerce.dao;

import com.ecommerce.entities.Wishlist;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WishlistDao {

	private final JdbcTemplate jdbcTemplate;

	public WishlistDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// Маппер: ResultSet -> Wishlist
	private Wishlist mapRowToWishlist(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
		Wishlist wishlist = new Wishlist();
		wishlist.setWishlistId(rs.getInt("idwishlist"));
		wishlist.setUserId(rs.getInt("iduser"));
		wishlist.setProductId(rs.getInt("idproduct"));
		return wishlist;
	}

	public boolean addToWishlist(Wishlist w) {
		String sql = "INSERT INTO wishlist(iduser, idproduct) VALUES (?, ?)";
		int rows = jdbcTemplate.update(sql, w.getUserId(), w.getProductId());
		return rows > 0;
	}

	public boolean getWishlist(int uid, int pid) {
		String sql = "SELECT COUNT(*) FROM wishlist WHERE iduser = ? AND idproduct = ?";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, uid, pid);
		return count != null && count > 0;
	}

	public List<Wishlist> getListByUserId(int uid) {
		String sql = "SELECT * FROM wishlist WHERE iduser = ?";
		return jdbcTemplate.query(sql, this::mapRowToWishlist, uid);
	}

	public void deleteWishlist(int uid, int pid) {
		String sql = "DELETE FROM wishlist WHERE iduser = ? AND idproduct = ?";
		jdbcTemplate.update(sql, uid, pid);
	}
}

