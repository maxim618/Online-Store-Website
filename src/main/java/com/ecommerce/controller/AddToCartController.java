package com.ecommerce.controller;

import com.ecommerce.dao.CartDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.entities.Cart;
import com.ecommerce.entities.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AddToCartController {

    private final CartDao cartDao;
    private final ProductDao productDao;

    public AddToCartController(CartDao cartDao, ProductDao productDao) {
        this.cartDao = cartDao;
        this.productDao = productDao;
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam("uid") int uid,
            @RequestParam("pid") int pid,
            HttpSession session) {

        int qty = cartDao.getQuantity(uid, pid);
        Message message;

        if (qty == 0) {
            Cart cart = new Cart(uid, pid, 1);
            cartDao.addToCart(cart);
            message = new Message("Product is added to cart successfully!", "success", "alert-success");
        } else {
            int cid = cartDao.getIdByUserIdAndProductId(uid, pid);
            cartDao.updateQuantity(cid, qty + 1);
            message = new Message("Product quantity is increased!", "success", "alert-success");
        }

        // уменьшаем количество товара на складе
        int availableQty = productDao.getProductQuantityById(pid);
        productDao.updateQuantity(pid, availableQty - 1);

        session.setAttribute("message", message);
        return "redirect:/viewProduct?pid=" + pid; // viewProduct.jsp
    }
}
