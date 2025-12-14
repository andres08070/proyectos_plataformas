package com.cr.Ejemplo1.util;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionUtil {
    
    public boolean isUserLoggedIn(HttpSession session) {
        return session != null && session.getAttribute("id") != null;
    }
    
    public String getUserId(HttpSession session) {
        if (session == null) return null;
        return (String) session.getAttribute("id");
    }
    
    public void invalidateSession(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}