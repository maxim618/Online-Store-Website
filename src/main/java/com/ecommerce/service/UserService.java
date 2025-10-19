package com.ecommerce.service;

import com.ecommerce.dao.UserDao;
import com.ecommerce.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAllUsers() {
        return userDao.getAllUser();
    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    public boolean saveUser(User user) {
        return userDao.saveUser(user);
    }

    public User getUserByEmailPassword(String email, String password) {
        return userDao.getUserByEmailPassword(email, password);
    }

    //  Новый вариант — без перебора всех email
    public String getUserEmailByEmail(String email) {
        return userDao.existsByEmail(email) ? email : null;
    }
    public User getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public void updateUserPasswordByEmail(String newPassword, String email) {
        userDao.updateUserPasswordByEmail(newPassword, email);
    }
}
