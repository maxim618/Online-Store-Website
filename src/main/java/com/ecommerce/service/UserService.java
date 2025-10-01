package com.ecommerce.service;

import com.ecommerce.dao.UserDao;
import com.ecommerce.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAllUsers() {
        return userDao.getAllUser(); //  правильный вызов
    }

    public User getUserById(int id) {
        // В UserDao нет getUserById, можно добавить:
        // SELECT * FROM user WHERE userid=?
        throw new UnsupportedOperationException("Метод getUserById пока не реализован в UserDao");
    }
}
