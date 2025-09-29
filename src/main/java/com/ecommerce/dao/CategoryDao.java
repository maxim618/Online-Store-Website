package com.ecommerce.dao;

import com.ecommerce.entities.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CategoryDao {

	private final JdbcTemplate jdbcTemplate;

	public CategoryDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// Маппер для Category
	private Category mapRowToCategory(ResultSet rs, int rowNum) throws SQLException {
		Category category = new Category();
		category.setCategoryId(rs.getInt("cid"));
		category.setCategoryName(rs.getString("name"));
		category.setCategoryImage(rs.getString("image"));
		return category;
	}

	public boolean saveCategory(Category category) {
		String sql = "INSERT INTO category(name, image) VALUES(?, ?)";
		return jdbcTemplate.update(sql,
				category.getCategoryName(),
				category.getCategoryImage()) > 0;
	}

	public List<Category> getAllCategories() {
		String sql = "SELECT * FROM category";
		return jdbcTemplate.query(sql, this::mapRowToCategory);
	}

	public Category getCategoryById(int cid) {
		String sql = "SELECT * FROM category WHERE cid=?";
		return jdbcTemplate.queryForObject(sql, this::mapRowToCategory, cid);
	}

	public String getCategoryName(int catId) {
		String sql = "SELECT name FROM category WHERE cid=?";
		return jdbcTemplate.queryForObject(sql, String.class, catId);
	}

	public void updateCategory(Category cat) {
		String sql = "UPDATE category SET name=?, image=? WHERE cid=?";
		jdbcTemplate.update(sql,
				cat.getCategoryName(),
				cat.getCategoryImage(),
				cat.getCategoryId());
	}

	public void deleteCategory(int cid) {
		String sql = "DELETE FROM category WHERE cid=?";
		jdbcTemplate.update(sql, cid);
	}

	public int categoryCount() {
		String sql = "SELECT COUNT(*) FROM category";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}
}
