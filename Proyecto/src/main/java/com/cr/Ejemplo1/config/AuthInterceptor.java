package com.cr.Ejemplo1.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                             HttpServletResponse response, 
                             Object handler) throws Exception {
        
        HttpSession session = request.getSession(false);
        String requestURI = request.getRequestURI();
        
        
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        
        String[] publicPaths = {
            "/login", 
            "/registro", 
            "/iniciarSesion",
            "/logout",
            "/css/", 
            "/js/",
            "/public/",
            "/imgs/",
            "/error",
            "/index",
            "/resultados",
            "/"
        };
        
        
        for (String path : publicPaths) {
            if (requestURI.startsWith(path)) {
                return true;
            }
        }
        
       
        if (session == null || session.getAttribute("id") == null) {
            response.sendRedirect("/login");
            return false;
        }
        
        return true;
    }
}