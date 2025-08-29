package com.spring.Suwatha.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@AllArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    
        final String authorizationHeader = request.getHeader("Authorization");
    
        String username = null;
        String jwt = null;
    
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
            }
    
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            }
            filterChain.doFilter(request, response);
        }catch (io.jsonwebtoken.ExpiredJwtException ex) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, "JWT token has expired");
        } catch (io.jsonwebtoken.SignatureException ex) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, "Invalid JWT signature");
        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            setErrorResponse(HttpStatus.BAD_REQUEST, response, "Malformed JWT token");
        } catch (Exception ex) {
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, "Something went wrong with JWT processing");
        }
        
    }
    
    private void setErrorResponse(HttpStatus status, HttpServletResponse response, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"timestamp\":\"" + new Date() + "\",\"status\":" + status.value()
                + ",\"error\":\"" + message + "\"}");
    }
  
}
