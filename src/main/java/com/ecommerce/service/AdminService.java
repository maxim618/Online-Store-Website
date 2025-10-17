// src/main/java/com/ecommerce/service/AdminService.java
package com.ecommerce.service;

import com.ecommerce.dao.AdminDao;
import com.ecommerce.entities.Admin;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final AdminDao adminDao;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminService(AdminDao adminDao, BCryptPasswordEncoder passwordEncoder) {
        this.adminDao = adminDao;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Admin> getAllAdmins() {
        return adminDao.getAllAdmins();
    }

    public boolean addAdmin(Admin admin) {
        // шифруем пароль перед сохранением
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminDao.addAdmin(admin);
    }

    public void deleteAdmin(int id) {
        adminDao.deleteAdmin(id);
    }

    // ✅ НОВОЕ: получить админа по email
    public Admin getAdminByEmail(String email) {
        return adminDao.getAdminByEmail(email);
    }

    // ✅ Удобная проверка пароля
    public boolean checkPassword(Admin admin, String rawPassword) {
        return admin != null && passwordEncoder.matches(rawPassword, admin.getPassword());
    }
    /** 🔑 Логин: проверка email + password */
    public Admin login(String email, String rawPassword) {
        Admin admin = adminDao.getAdminByEmail(email);
        if (admin != null && passwordEncoder.matches(rawPassword, admin.getPassword())) {
            return admin;
        }
        return null;
    }
    public boolean saveAdmin(Admin admin) {
        // при сохранении нового админа пароль шифруем
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        return adminDao.addAdmin(admin);
    }
}
