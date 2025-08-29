package com.spring.Suwatha.user_module.controller;


import com.spring.Suwatha.user_module.entity.Specialization;
import com.spring.Suwatha.user_module.service.SpecializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @GetMapping("/h")
    public String hello (){
       return "hello";
    }
    
    @Autowired
    private SpecializationService specializationService;
    
    
    @GetMapping ("/get-specialization")
    public ResponseEntity<List<Specialization>> getAllSpecialization()
    {
        List<Specialization> specializations = specializationService.getAllSpecialization();
        return ResponseEntity.ok(specializations);
    }
    
}
