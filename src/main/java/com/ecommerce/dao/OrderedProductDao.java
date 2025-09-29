package com.ecommerce.dao;

import com.ecommerce.entities.OrderedProduct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderedProductDao {

	private final JdbcTemplate jdbcTemplate;

	public OrderedProductDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// Маппер из ResultSet -> OrderedProduct
	private OrderedProduct mapRowToOrderedProduct(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
		OrderedProduct orderedProduct = new OrderedProduct();
		orderedProduct.setName(rs.getString("name"));
		orderedProduct.setQuantity(rs.getInt("quantity"));
		orderedProduct.setPrice(rs.getFloat("price"));
		orderedProduct.setImage(rs.getString("image"));
		orderedProduct.setOrderId(rs.getInt("orderid"));
		return orderedProduct;
	}

	public void insertOrderedProduct(OrderedProduct ordProduct) {
		String sql = "INSERT INTO ordered_product(name, quantity, price, image, orderid) VALUES (?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql,
				ordProduct.getName(),
				ordProduct.getQuantity(),
				ordProduct.getPrice(),
				ordProduct.getImage(),
				ordProduct.getOrderId()
		);
	}

	public List<OrderedProduct> getAllOrderedProduct(int oid) {
		String sql = "SELECT * FROM ordered_product WHERE orderid = ?";
		return jdbcTemplate.query(sql, this::mapRowToOrderedProduct, oid);
	}
}
