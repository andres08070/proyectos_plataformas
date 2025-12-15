package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControladorPrincipal {
    
    @Autowired
    private HttpSession session; 

    // ==========================================
    // RUTAS PÚBLICAS (Login y Registro - GET)
    // ==========================================

    @GetMapping("/auth/inicioSesion")
    public String mostrarLogin() {
        return "auth/inicioSesion";
    }
    
    @GetMapping("/registro")
    public String mostrarFormulario(){
        return "auth/registro";
    }
    
    // ==========================================
    // RUTAS PRIVADAS (Protegidas por sesión)
    // ==========================================
    
    @GetMapping("/inicio")
    public String inicio(HttpServletResponse response, HttpSession session, Model model) {
        // 🔒 Prevenir cache
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // ✅ Verificar sesión (ID COMO INTEGER)
        Integer idUsuario = (Integer) session.getAttribute("id");
        if (idUsuario == null) {
            return "redirect:/auth/inicioSesion";
        }

        return "dashboard/inicio";
    } 

    // ==========================================
    // RUTAS DE ACCIÓN (POST)
    // ==========================================

    @PostMapping("/iniciarSesion")
    public String procesarLogin(@RequestParam String correo, 
                                @RequestParam String contraseña, 
                                Model model,
                                HttpServletResponse response) {
        
        // 🔒 PREVENIR CACHE EN LOGIN
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        String sql = "SELECT nombreC, cinturon_rango, ID_documento FROM usuarios WHERE correo = ? AND contraseña = ?";
        
        try (Connection con = BD.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, correo);
            ps.setString(2, contraseña);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // ✅ Login Exitoso: Guardar datos en sesión (TIPOS CORRECTOS)
                session.setAttribute("usuarioLogueado", rs.getString("nombreC"));
                session.setAttribute("rangoUsuario", rs.getString("cinturon_rango"));
                session.setAttribute("id", rs.getInt("ID_documento")); // 👈 AQUÍ ESTABA EL ERROR
                
                System.out.println("Usuario logueado con ID: " + session.getAttribute("id"));
                
                return "redirect:/inicio";
            } else {
                model.addAttribute("msg", "Correo o contraseña incorrectos.");
                return "auth/inicioSesion";
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("msg", "Error de conexión con la base de datos.");
            return "auth/inicioSesion";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // 🔒 PREVENIR CACHE
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        // Invalidar toda la sesión
        session.invalidate();
        
        return "redirect:/auth/inicioSesion?logout=true";
    }
}
