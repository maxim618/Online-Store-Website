package com.ecommerce.service;

import com.ecommerce.dao.ProductDao;
import com.ecommerce.entities.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> getAllLatestProducts() {
        return productDao.getAllLatestProducts();
    }

    public List<Product> getDiscountedProducts() {
        return productDao.getDiscountedProducts();
    }
    public List<Product> getProductsByCategory(int categoryId) {
        return productDao.getAllProductsByCategoryId(categoryId);
    }
    public Product getProductById(int productId) {
        return productDao.getProductsByProductId(productId);
    }


}

