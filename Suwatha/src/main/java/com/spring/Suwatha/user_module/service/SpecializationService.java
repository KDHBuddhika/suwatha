package com.spring.Suwatha.user_module.service;


import com.spring.Suwatha.user_module.entity.Specialization;
import com.spring.Suwatha.user_module.repository.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository repository;
    
    public List<Specialization> getAllSpecialization(){
        return repository.findAll();
    }
    
    
}
