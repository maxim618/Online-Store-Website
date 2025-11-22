package com.ecommerce.controller;

import com.ecommerce.dao.UserDaoLegacy;
import com.ecommerce.entities.Message;
import com.ecommerce.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UpdateUserController {

    private final UserDaoLegacy userDao;

    public UpdateUserController(UserDaoLegacy userDao) {
        this.userDao = userDao;
    }

    /**
     * Обновление только адреса (checkout.jsp)
     */
    @PostMapping("/changeAddress")
    public String changeAddress(
            @RequestParam("user_address") String address,
            @RequestParam("city") String city,
            @RequestParam("pincode") String pincode,
            @RequestParam("state") String state,
            HttpSession session
    ) {
        User oldUser = (User) session.getAttribute("activeUser");
        if (oldUser == null) {
            session.setAttribute("message", new Message("Please log in first.", "error", "alert-danger"));
            return "redirect:/login.jsp";
        }

        User user = new User();
        user.setUserId(oldUser.getUserId());
        user.setUserName(oldUser.getUserName());
        user.setUserEmail(oldUser.getUserEmail());
        user.setUserPassword(oldUser.getUserPassword());
        user.setUserPhone(oldUser.getUserPhone());
        user.setUserGender(oldUser.getUserGender());
        user.setDateTime(oldUser.getDateTime());
        user.setUserAddress(address);
        user.setUserCity(city);
        user.setUserPincode(pincode);
        user.setUserState(state);

        userDao.updateUserAddresss(user);
        session.setAttribute("activeUser", user);

        return "redirect:/checkout.jsp";
    }

    /**
     * Обновление полной информации пользователя (profile.jsp)
     */
    @PostMapping("/update")
    public String updateUser(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("mobile_no") String phone,
            @RequestParam("gender") String gender,
            @RequestParam("address") String address,
            @RequestParam("city") String city,
            @RequestParam("pincode") String pincode,
            @RequestParam("state") String state,
            HttpSession session
    ) {
        User oldUser = (User) session.getAttribute("activeUser");
        if (oldUser == null) {
            session.setAttribute("message", new Message("Please log in first.", "error", "alert-danger"));
            return "redirect:/login.jsp";
        }

        User user = new User(name, email, phone, gender, address, city, pincode, state);
        user.setUserId(oldUser.getUserId());
        user.setUserPassword(oldUser.getUserPassword());
        user.setDateTime(oldUser.getDateTime());

        userDao.updateUser(user);
        session.setAttribute("activeUser", user);

        session.setAttribute("message",
                new Message("User information updated successfully!!", "success", "alert-success"));

        return "redirect:/profile.jsp";
    }

    /**
     * Удаление пользователя (display_users.jsp)
     */
    @PostMapping("/delete")
    public String deleteUser(
            @RequestParam("uid") int uid
    ) {
        userDao.deleteUser(uid);
        return "redirect:/display_users.jsp";
    }
}
