package com.spring.Suwatha.user_module.service;


import com.spring.Suwatha.user_module.dto.AdminCreateDto;
import com.spring.Suwatha.user_module.entity.Admin;
import com.spring.Suwatha.user_module.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Admin createAdmin(AdminCreateDto dto) {
        Admin admin = new Admin();
        admin.setEmail(dto.getEmail());
        admin.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        return adminRepository.save(admin);
    }
}
