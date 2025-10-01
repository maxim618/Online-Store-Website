package com.ecommerce.controller;

import com.ecommerce.dao.CategoryDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.entities.Category;
import com.ecommerce.entities.Message;
import com.ecommerce.entities.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/operation")
public class AddOperationController {

    private final CategoryDao categoryDao;
    private final ProductDao productDao;

    public AddOperationController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    @PostMapping
    public String handleOperation(
            @RequestParam("operation") String operation,
            @RequestParam(required = false) String category_name,
            @RequestParam(required = false) MultipartFile category_img,
            @RequestParam(required = false) Integer cid,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Float price,
            @RequestParam(required = false) Integer discount,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) MultipartFile photo,
            @RequestParam(required = false) MultipartFile product_img,
            @RequestParam(required = false) Integer pid,
            @RequestParam(required = false) Integer categoryType,
            @RequestParam(required = false) String image,
            HttpSession session
    ) throws IOException {

        Message message = null;

        if ("addCategory".equalsIgnoreCase(operation)) {
            Category category = new Category(category_name, category_img.getOriginalFilename());
            boolean ok = categoryDao.saveCategory(category);
            saveFile(category_img, "Product_imgs");
            message = ok ? success("Category added successfully!!")
                    : error("Something went wrong! Try again!!");
            session.setAttribute("message", message);
            return "redirect:/admin.jsp";

        } else if ("addProduct".equalsIgnoreCase(operation)) {
            int safeDiscount = (discount != null && discount >= 0 && discount <= 100) ? discount : 0;
            Product product = new Product(name, description, price, safeDiscount, quantity,
                    photo.getOriginalFilename(), categoryType);
            boolean ok = productDao.saveProduct(product);
            saveFile(photo, "Product_imgs");
            message = ok ? success("Product added successfully!!")
                    : error("Something went wrong! Try again!!");
            session.setAttribute("message", message);
            return "redirect:/admin.jsp";

        } else if ("updateCategory".equalsIgnoreCase(operation)) {
            if (category_img != null && !category_img.isEmpty()) {
                Category category = new Category(cid, category_name, category_img.getOriginalFilename());
                categoryDao.updateCategory(category);
                saveFile(category_img, "Product_imgs");
            } else {
                Category category = new Category(cid, category_name, image);
                categoryDao.updateCategory(category);
            }
            session.setAttribute("message", success("Category updated successfully!!"));
            return "redirect:/display_category.jsp";

        } else if ("deleteCategory".equalsIgnoreCase(operation)) {
            categoryDao.deleteCategory(cid);
            return "redirect:/display_category.jsp";

        } else if ("updateProduct".equalsIgnoreCase(operation)) {
            int safeDiscount = (discount != null && discount >= 0 && discount <= 100) ? discount : 0;
            int resolvedCid = (categoryType != null && categoryType > 0) ? categoryType : cid;

            if (product_img != null && !product_img.isEmpty()) {
                Product product = new Product(pid, name, description, price, safeDiscount,
                        quantity, product_img.getOriginalFilename(), resolvedCid);
                productDao.updateProduct(product);
                saveFile(product_img, "Product_imgs");
            } else {
                Product product = new Product(pid, name, description, price, safeDiscount,
                        quantity, image, resolvedCid);
                productDao.updateProduct(product);
            }
            session.setAttribute("message", success("Product updated successfully!!"));
            return "redirect:/display_products.jsp";

        } else if ("deleteProduct".equalsIgnoreCase(operation)) {
            productDao.deleteProduct(pid);
            return "redirect:/display_products.jsp";
        }

        session.setAttribute("message", error("Unknown operation!"));
        return "redirect:/admin.jsp";
    }

    // --- helpers ---

    private void saveFile(MultipartFile file, String folder) throws IOException {
        if (file != null && !file.isEmpty()) {
            String uploadDir = new File("src/main/webapp/" + folder).getAbsolutePath();
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            file.transferTo(new File(dir, file.getOriginalFilename()));
        }
    }

    private Message success(String text) {
        return new Message(text, "success", "alert-success");
    }

    private Message error(String text) {
        return new Message(text, "error", "alert-danger");
    }
}
