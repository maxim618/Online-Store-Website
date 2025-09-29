package com.ecommerce.dao;

import com.ecommerce.entities.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductDao {

	private final JdbcTemplate jdbcTemplate;

	public ProductDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// Маппер для Product
	private Product mapRowToProduct(ResultSet rs, int rowNum) throws SQLException {
		Product product = new Product();
		product.setProductId(rs.getInt("pid"));
		product.setProductName(rs.getString("name"));
		product.setProductDescription(rs.getString("description"));
		product.setProductPrice(rs.getFloat("price"));
		product.setProductQunatity(rs.getInt("quantity"));
		product.setProductDiscount(rs.getInt("discount"));
		product.setProductImages(rs.getString("image"));
		product.setCategoryId(rs.getInt("cid"));
		return product;
	}

	public boolean saveProduct(Product product) {
		String sql = "INSERT INTO product(name, description, price, quantity, discount, image, cid) VALUES(?, ?, ?, ?, ?, ?, ?)";
		return jdbcTemplate.update(sql,
				product.getProductName(),
				product.getProductDescription(),
				product.getProductPrice(),
				product.getProductQunatity(),
				product.getProductDiscount(),
				product.getProductImages(),
				product.getCategoryId()) > 0;
	}

	public List<Product> getAllProducts() {
		String sql = "SELECT * FROM product";
		return jdbcTemplate.query(sql, this::mapRowToProduct);
	}

	public List<Product> getAllLatestProducts() {
		String sql = "SELECT * FROM product ORDER BY pid DESC";
		return jdbcTemplate.query(sql, this::mapRowToProduct);
	}

	public Product getProductsByProductId(int pid) {
		String sql = "SELECT * FROM product WHERE pid=?";
		return jdbcTemplate.queryForObject(sql, this::mapRowToProduct, pid);
	}

	public List<Product> getAllProductsByCategoryId(int catId) {
		String sql = "SELECT * FROM product WHERE cid=?";
		return jdbcTemplate.query(sql, this::mapRowToProduct, catId);
	}

	public List<Product> getAllProductsBySearchKey(String search) {
		String sql = "SELECT * FROM product WHERE LOWER(name) LIKE ? OR LOWER(description) LIKE ?";
		String likeSearch = "%" + search.toLowerCase() + "%";
		return jdbcTemplate.query(sql, this::mapRowToProduct, likeSearch, likeSearch);
	}

	public List<Product> getDiscountedProducts() {
		String sql = "SELECT * FROM product WHERE discount >= 30 ORDER BY discount DESC";
		return jdbcTemplate.query(sql, this::mapRowToProduct);
	}

	public void updateProduct(Product product) {
		String sql = "UPDATE product SET name=?, description=?, price=?, quantity=?, discount=?, image=? WHERE pid=?";
		jdbcTemplate.update(sql,
				product.getProductName(),
				product.getProductDescription(),
				product.getProductPrice(),
				product.getProductQunatity(),
				product.getProductDiscount(),
				product.getProductImages(),
				product.getProductId());
	}

	public void updateQuantity(int id, int qty) {
		String sql = "UPDATE product SET quantity=? WHERE pid=?";
		jdbcTemplate.update(sql, qty, id);
	}

	public void deleteProduct(int pid) {
		String sql = "DELETE FROM product WHERE pid=?";
		jdbcTemplate.update(sql, pid);
	}

	public int productCount() {
		String sql = "SELECT COUNT(*) FROM product";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public float getProductPriceById(int pid) {
		String sql = "SELECT price, discount FROM product WHERE pid=?";
		return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
			float orgPrice = rs.getFloat("price");
			int discount = rs.getInt("discount");
			float discountPrice = (discount / 100.0f) * orgPrice;
			return orgPrice - discountPrice;
		}, pid);
	}

	public int getProductQuantityById(int pid) {
		String sql = "SELECT quantity FROM product WHERE pid=?";
		return jdbcTemplate.queryForObject(sql, Integer.class, pid);
	}
}
