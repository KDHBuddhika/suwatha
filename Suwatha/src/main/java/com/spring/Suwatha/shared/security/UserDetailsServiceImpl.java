package com.spring.Suwatha.shared.security;

import com.spring.Suwatha.user_module.entity.Admin;
import com.spring.Suwatha.user_module.entity.Therapist;
import com.spring.Suwatha.user_module.repository.AdminRepository;
import com.spring.Suwatha.user_module.repository.TherapistRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final AdminRepository adminRepository;
    private final TherapistRepository therapistRepository;
    
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    
//        return adminRepository.findByEmail(email)
//                .map(admin -> new User(
//                        admin.getEmail(),
//                        admin.getPasswordHash(),
//                        Collections.singletonList(() -> "ROLE_ADMIN")
//                ))
//                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + email));
    
        // First, try to find the user as an Admin
        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            // If found, create a UserDetails object with ROLE_ADMIN
            return new User(
                    admin.getEmail(),
                    admin.getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }
    
        // If not found as an Admin, try to find the user as a Therapist
        Optional<Therapist> therapistOptional = therapistRepository.findByEmail(email);
        if (therapistOptional.isPresent()) {
            Therapist therapist = therapistOptional.get();
        
            // IMPORTANT: Check if the therapist's account is active before allowing login.
            if (!therapist.isActive()) {
                throw new UsernameNotFoundException("Therapist account is not active for email: " + email);
            }
        
            // If found and active, create a UserDetails object with ROLE_THERAPIST
            return new User(
                    therapist.getEmail(),
                    therapist.getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_THERAPIST"))
            );
        }
    
        // If the user is not found in either repository, throw an exception.
        throw new UsernameNotFoundException("User not found with email: " + email);
    
    }
    
    
}
