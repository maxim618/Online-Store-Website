package com.ecommerce.controller;

import com.ecommerce.dao.UserDao;
import com.ecommerce.entities.Message;
import com.ecommerce.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserDao userDao;

    public RegisterController(UserDao userDao) {
        this.userDao = userDao;
    }

    // Показ страницы регистрации
    @GetMapping
    public String showRegisterPage() {
        return "register"; // /WEB-INF/jsp/register.jsp
    }

    // Обработка формы регистрации
    @PostMapping
    public String processRegister(HttpServletRequest request, HttpSession session) {
        // Читаем параметры (поддержим оба возможных варианта имён полей)
        String name     = pick(request, "name", "user_name");
        String email    = pick(request, "email", "user_email");
        String password = pick(request, "password", "user_password");
        String phone    = pick(request, "phone", "user_phone");
        String gender   = pick(request, "gender", "user_gender");
        String address  = pick(request, "address", "user_address");
        String city     = pick(request, "city", "user_city");
        String pincode  = pick(request, "pincode", "user_pincode");
        String state    = pick(request, "state", "user_state");

        // Базовая валидация
        if (isBlank(name) || isBlank(email) || isBlank(password)) {
            session.setAttribute("message",
                    new Message("Name, Email и Password обязательны.", "error", "alert-danger"));
            return "redirect:/register";
        }

        // Проверка дубликата email (используем имеющийся метод DAO)
        try {
            boolean exists = userDao.getAllEmail().stream()
                    .anyMatch(e -> e != null && e.equalsIgnoreCase(email));
            if (exists) {
                session.setAttribute("message",
                        new Message("Пользователь с таким email уже существует.", "error", "alert-danger"));
                return "redirect:/register";
            }
        } catch (DataAccessException ex) {
            session.setAttribute("message",
                    new Message("Ошибка доступа к БД при проверке email.", "error", "alert-danger"));
            return "redirect:/register";
        }

        // Собираем сущность User
        User user = new User();
        user.setUserName(name);
        user.setUserEmail(email);
        user.setUserPassword(password);
        user.setUserPhone(phone);
        user.setUserGender(gender);
        user.setUserAddress(address);
        user.setUserCity(city);
        user.setUserPincode(pincode);
        user.setUserState(state);

        // Сохраняем
        try {
            boolean ok = userDao.saveUser(user);
            if (ok) {
                session.setAttribute("message",
                        new Message("Регистрация успешна! Войдите под своими данными.", "success", "alert-success"));
                return "redirect:/login";
            } else {
                session.setAttribute("message",
                        new Message("Не удалось создать пользователя.", "error", "alert-danger"));
                return "redirect:/register";
            }
        } catch (DataAccessException ex) {
            session.setAttribute("message",
                    new Message("Ошибка БД при сохранении пользователя.", "error", "alert-danger"));
            return "redirect:/register";
        }
    }

    // --- helpers ---

    private static String pick(HttpServletRequest req, String... keys) {
        for (String k : keys) {
            String v = req.getParameter(k);
            if (v != null && !v.trim().isEmpty()) return v.trim();
        }
        return null;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
