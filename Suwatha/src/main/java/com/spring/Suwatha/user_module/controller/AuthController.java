package com.spring.Suwatha.user_module.controller;


import com.spring.Suwatha.shared.security.JwtUtil;
import com.spring.Suwatha.user_module.dto.AdminCreateDto;
import com.spring.Suwatha.user_module.dto.AuthRequestDto;
import com.spring.Suwatha.user_module.dto.AuthResponse;
import com.spring.Suwatha.user_module.service.AdminService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AdminService adminService;
    
   
    
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> createAuthenticationToken(@RequestBody AuthRequestDto authRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        AuthResponse response = new AuthResponse(jwt);
        
        return ResponseEntity.ok(response);
        
        
    }
    
    
    @PostMapping("/adminCreate")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody AdminCreateDto adminDto) {
        adminService.createAdmin(adminDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Admin created successfully");
    }

}
