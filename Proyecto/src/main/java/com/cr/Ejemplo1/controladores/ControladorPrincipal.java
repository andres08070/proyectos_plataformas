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
    
    // NOTA: Es mejor dejar el @Autowired de la sesión en el método o usar @RequestScope, 
    // pero lo mantengo aquí ya que así lo tenías.
    @Autowired
    private HttpSession session; 

    // ==========================================
    // RUTAS PÚBLICAS (Login y Registro - GET)
    // ==========================================

    @GetMapping("/login")
    public String mostrarLogin() {
        // Retorna: src/main/resources/templates/auth/inicioSesion.html
        return "auth/inicioSesion";
    }
    
    @GetMapping("/registro")
    public String mostrarFormulario(){
        // Retorna: src/main/resources/templates/auth/registro.html
        return "auth/registro";
    }
    
    // ==========================================
    // RUTAS PRIVADAS (Protegidas por Interceptor)
    // ==========================================

    @GetMapping("/CrearCampeonato")
    public String crearCampeonato() {
        // El Interceptor ya verificó la sesión y puso Anti-Caché.
        // Retorna: src/main/resources/templates/campeonato/CrearCampeonato.html
        return "campeonato/CrearCampeonato"; 
    }
    
    @GetMapping("/inicio")
    public String inicio() {
        // El Interceptor ya verificó la sesión y puso Anti-Caché.
        // Retorna: src/main/resources/templates/dashboard/inicio.html
        return "dashboard/inicio";
    }
    
    // ==========================================
    // RUTAS DE ACCIÓN (POST)
    // ==========================================

    @PostMapping("/iniciarSesion")
    public String procesarLogin(@RequestParam String correo, 
                                @RequestParam String contraseña, 
                                Model model) {
        
        String sql = "SELECT nombreC, cinturon_rango, ID_documento FROM usuarios WHERE correo = ? AND contraseña = ?";
        
        try (Connection con = BD.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, correo);
            ps.setString(2, contraseña);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Login Exitoso: Guardar datos en sesión
                session.setAttribute("usuarioLogueado", rs.getString("nombreC"));
                session.setAttribute("rangoUsuario", rs.getString("cinturon_rango"));
                session.setAttribute("id", rs.getString("ID_documento"));
                System.out.println("Usuario logueado: " + session.getAttribute("id"));
                
                // Redirigir al Dashboard
                return "redirect:/inicio"; // Usar redirect para evitar reenvío de formulario (POST)
            } else {
                // Login Fallido
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
    public String logout(HttpSession session, HttpServletResponse response) {
        // Limpiamos headers para asegurarnos de que el navegador no cachee la URL de salida
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        session.invalidate();  // Cierra la sesión
        return "redirect:/login"; // Redirigir al login (que es la página pública de entrada)
    }

}