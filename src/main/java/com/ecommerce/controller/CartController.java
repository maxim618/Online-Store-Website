package com.ecommerce.controller;

import com.ecommerce.dao.CartDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.entities.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    private final CartDao cartDao;
    private final ProductDao productDao;

    public CartController(CartDao cartDao, ProductDao productDao) {
        this.cartDao = cartDao;
        this.productDao = productDao;
    }

    @GetMapping("/cart/operation")
    public String handleCartOperation(
            @RequestParam("cid") int cid,
            @RequestParam("opt") int opt,
            HttpSession session) {

        int qty = cartDao.getQuantityById(cid);
        int pid = cartDao.getProductId(cid);
        int availableQty = productDao.getProductQuantityById(pid);

        if (opt == 1) {
            // Увеличить количество
            if (availableQty > 0) {
                cartDao.updateQuantity(cid, qty + 1);
                productDao.updateQuantity(pid, availableQty - 1);
            } else {
                session.setAttribute("message",
                        new Message("Product out of stock!", "error", "alert-danger"));
            }
        } else if (opt == 2) {
            // Уменьшить количество
            cartDao.updateQuantity(cid, qty - 1);
            productDao.updateQuantity(pid, availableQty + 1);
        } else if (opt == 3) {
            // Удалить товар из корзины
            cartDao.removeProduct(cid);
            session.setAttribute("message",
                    new Message("Product removed from cart!", "success", "alert-success"));
            productDao.updateQuantity(pid, availableQty + qty);
        }

        return "redirect:/cart"; // cart.jsp
    }
}
