package com.ecommerce.controller;

import com.ecommerce.dao.AdminDao;
import com.ecommerce.entities.Admin;
import com.ecommerce.entities.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/operation")
public class AdminController {

    private final AdminDao adminDao;

    public AdminController(AdminDao adminDao) {
        this.adminDao = adminDao;
    }

    @GetMapping
    public String handleOperation(
            @RequestParam("operation") String operation,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer id,
            HttpSession session
    ) {
        Message message = null;

        if ("save".equalsIgnoreCase(operation.trim())) {
            Admin admin = new Admin(name, email, phone, password);
            boolean ok = adminDao.saveAdmin(admin);
            message = ok
                    ? new Message("New admin registered successfully!", "success", "alert-success")
                    : new Message("Sorry! Something went wrong", "error", "alert-danger");

        } else if ("delete".equalsIgnoreCase(operation.trim())) {
            boolean ok = adminDao.deleteAdmin(id);
            message = ok
                    ? new Message("Admin deleted successfully!", "success", "alert-success")
                    : new Message("Sorry! Something went wrong", "error", "alert-danger");
        } else {
            message = new Message("Unknown operation!", "error", "alert-danger");
        }

        session.setAttribute("message", message);
        return "redirect:/display_admin.jsp";
    }

    @PostMapping
    public String handleOperationPost(
            @RequestParam("operation") String operation,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer id,
            HttpSession session
    ) {
        // делегируем в GET-метод
        return handleOperation(operation, name, email, password, phone, id, session);
    }
}
