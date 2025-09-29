package com.ecommerce.controller;

import com.ecommerce.dao.CartDao;
import com.ecommerce.dao.OrderDao;
import com.ecommerce.dao.OrderedProductDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.entities.*;
import com.ecommerce.helper.MailMessenger;
import com.ecommerce.helper.OrderIdGenerator;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    private final OrderDao orderDao;
    private final CartDao cartDao;
    private final OrderedProductDao orderedProductDao;
    private final ProductDao productDao;

    public OrderController(OrderDao orderDao,
                           CartDao cartDao,
                           OrderedProductDao orderedProductDao,
                           ProductDao productDao) {
        this.orderDao = orderDao;
        this.cartDao = cartDao;
        this.orderedProductDao = orderedProductDao;
        this.productDao = productDao;
    }

    @PostMapping("/order")
    public String handleOrder(
            @RequestParam("payementMode") String paymentType,
            HttpSession session) {

        String from = (String) session.getAttribute("from");
        User user = (User) session.getAttribute("activeUser");
        String orderId = OrderIdGenerator.getOrderId();
        String status = "Order Placed";

        if ("cart".equalsIgnoreCase(from)) {
            // заказ из корзины
            int orderDbId = orderDao.insertOrder(new Order(orderId, status, paymentType, user.getUserId()));

            List<Cart> cartItems = cartDao.getCartListByUserId(user.getUserId());
            for (Cart item : cartItems) {
                Product prod = productDao.getProductsByProductId(item.getProductId());
                OrderedProduct orderedProduct = new OrderedProduct(
                        prod.getProductName(),
                        item.getQuantity(),
                        prod.getProductPriceAfterDiscount(),
                        prod.getProductImages(),
                        orderDbId
                );
                orderedProductDao.insertOrderedProduct(orderedProduct);
            }

            cartDao.removeAllProduct();
            session.removeAttribute("from");
            session.removeAttribute("totalPrice");

        } else if ("buy".equalsIgnoreCase(from)) {
            // заказ одного товара (Buy Now)
            int pid = (int) session.getAttribute("pid");
            int orderDbId = orderDao.insertOrder(new Order(orderId, status, paymentType, user.getUserId()));

            Product prod = productDao.getProductsByProductId(pid);
            OrderedProduct orderedProduct = new OrderedProduct(
                    prod.getProductName(),
                    1,
                    prod.getProductPriceAfterDiscount(),
                    prod.getProductImages(),
                    orderDbId
            );
            orderedProductDao.insertOrderedProduct(orderedProduct);

            productDao.updateQuantity(pid, productDao.getProductQuantityById(pid) - 1);

            session.removeAttribute("from");
            session.removeAttribute("pid");
        }

        session.setAttribute("order", "success");

        // уведомление по email
        MailMessenger.successfullyOrderPlaced(
                user.getUserName(),
                user.getUserEmail(),
                orderId,
                new Date().toString()
        );

        return "redirect:/"; // index.jsp
    }
}
