package com.didamartials.proyecto.config; // Ajusta el paquete según tu proyecto

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SeguridadInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 1. OBTENER LA SESIÓN
        HttpSession session = request.getSession();

        // 2. VERIFICAR SI HAY USUARIO (Si no hay, mandar al login)
        if (session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("/iniciarSesion"); // Ojo con la ruta de tu login
            return false; // Bloquea el paso
        }

        // 3. AGREGAR LOS ENCABEZADOS ANTI-CACHÉ (Para el botón Atrás)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        return true; // Si hay sesión, deja pasar al controlador
    }
}