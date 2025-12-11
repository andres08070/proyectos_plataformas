/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
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

    // 1. Ruta Raíz: Muestra el index.html con los dos botones
    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    // 2. Ruta para mostrar el formulario de Login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "inicioSesion"; // Debe coincidir con el nombre de tu archivo HTML de login
    }
        @GetMapping("/registro")
    public String mostrarFormulario(){
        return "registro";
    }
    // 3. Lógica para procesar el Inicio de Sesión
    @PostMapping("/iniciarSesion")
    public String procesarLogin(@RequestParam String correo, 
                                @RequestParam String contraseña, 
                                Model model) {
        
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND contraseña = ?";
        
        try (Connection con = BD.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, correo);
            ps.setString(2, contraseña);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // ¡Login Exitoso!
                // Guardamos datos básicos en sesión para saber quién está conectado
                session.setAttribute("usuarioLogueado", rs.getString("nombreC"));
                session.setAttribute("rangoUsuario", rs.getString("cinturon_rango"));
                session.setAttribute("id", rs.getString("ID_documento"));
                System.out.println(session.getAttribute("id"));                
                return "inicio"; // Redirige a la página principal del sistema (dashboard)
            } else {
                // Login Fallido
                model.addAttribute("msg", "Correo o contraseña incorrectos.");
                return "inicioSesion";
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("msg", "Error de conexión con la base de datos.");
            return "inicioSesion";
        }
    }
    
    // Opcional: Cerrar sesión
    @GetMapping("/logout")
    public String cerrarSesion() {
        session.invalidate();
        return "redirect:/";
    }
}
