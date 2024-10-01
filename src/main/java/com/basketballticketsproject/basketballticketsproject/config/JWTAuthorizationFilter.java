package com.basketballticketsproject.basketballticketsproject.config;

import java.io.IOException;

import com.basketballticketsproject.basketballticketsproject.service.JwtService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private static final String PREFIX = "Bearer ";
    @Autowired
    private JwtService jwtService;
    @Autowired
    private  UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if("/cbgranada-api/v1/login".equals(path) || "/cbgranada-api/v1/addUser".equals(path)){
            chain.doFilter(request,response);
            return;
        }
        try {
            final String token = getTokenFromRquest(request);
            if(token != null){
                String username = jwtService.getUsernameFromToken(token);
                if(username!= null && SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if(jwtService.isTokenValid(token,userDetails)){
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),null,userDetails.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        }  catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}");
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}");
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}");
        }
        chain.doFilter(request, response);
    }

    private String getTokenFromRquest(HttpServletRequest request) {
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(jwtToken != null && (StringUtils.hasText(jwtToken) || jwtToken.startsWith(PREFIX))){
            return jwtToken.substring(7);
        }
        return null;
    }
}
