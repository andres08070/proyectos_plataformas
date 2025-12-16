package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
import com.cr.Ejemplo1.usuarios;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorPerfil {

    private static final Logger logger = LoggerFactory.getLogger(ControladorPerfil.class);

    
    @GetMapping("/perfil")
    public String mostrarPerfil(Model model,
                                HttpServletResponse response,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        
        Integer idUsuario = (Integer) session.getAttribute("id");
        if (idUsuario == null) {
            logger.warn("Intento de acceso no autorizado a /perfil");
            redirectAttributes.addFlashAttribute("mensajeError", "Debes iniciar sesión para ver tu perfil.");
            return "redirect:/auth/inicioSesion";
        }

        logger.info("Usuario ID: {} accediendo a su perfil", idUsuario);

        
        String sqlSelect = """
            SELECT 
                ID_documento,
                nombreC,
                correo,
                sexo,
                edad,
                cinturon_rango,
                nacionalidad
            FROM usuarios 
            WHERE ID_documento = ?
        """;

        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sqlSelect)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuarios perfil = new usuarios();
                perfil.setID_documento(rs.getInt("ID_documento"));
                perfil.setNombreC(rs.getString("nombreC"));
                perfil.setCorreo(rs.getString("correo"));
                perfil.setSexo(rs.getString("sexo"));
                perfil.setEdad(rs.getInt("edad"));
                perfil.setCinturon_rango(rs.getString("cinturon_rango"));
                perfil.setNacionalidad(rs.getString("nacionalidad"));

                model.addAttribute("perfil", perfil);
                logger.info("Perfil cargado para usuario ID: {}", idUsuario);

            } else {
                logger.error("No se encontró el perfil para usuario ID: {}", idUsuario);
                redirectAttributes.addFlashAttribute("mensajeError", "No se pudo cargar tu perfil.");
                return "redirect:/inicio";
            }

        } catch (SQLException e) {
            logger.error("Error SQL al cargar perfil del usuario ID: {}", idUsuario, e);
            redirectAttributes.addFlashAttribute("mensajeError", "Error al cargar el perfil.");
            return "redirect:/inicio";
        }

        return "dashboard/perfil";
    }

    
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam("nombre") String nombre,
                                   @RequestParam("correo") String correo,
                                   @RequestParam("sexo") String sexo,
                                   @RequestParam("edad") int edad,
                                   @RequestParam("cinturon_rango") String cinturonRango,
                                   @RequestParam("nacionalidad") String nacionalidad,
                                   @RequestParam(value = "contraseña", required = false) String contraseña,
                                   @RequestParam(value = "confirmar_contraseña", required = false) String confirmarContraseña,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        
        Integer idUsuario = (Integer) session.getAttribute("id");
        if (idUsuario == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Debes iniciar sesión para actualizar tu perfil.");
            return "redirect:/auth/inicioSesion";
        }

        logger.info("Usuario ID: {} actualizando su perfil", idUsuario);

        
        if (contraseña != null && !contraseña.trim().isEmpty()) {
            if (!contraseña.equals(confirmarContraseña)) {
                redirectAttributes.addFlashAttribute("mensajeError", "Las contraseñas no coinciden.");
                return "redirect:/perfil";
            }
        }

       
        StringBuilder sqlUpdate = new StringBuilder("UPDATE usuarios SET ");
        sqlUpdate.append("nombreC = ?, ");
        sqlUpdate.append("correo = ?, ");
        sqlUpdate.append("sexo = ?, ");
        sqlUpdate.append("edad = ?, ");
        sqlUpdate.append("cinturon_rango = ?, ");
        sqlUpdate.append("nacionalidad = ?");
        
        
        boolean actualizarContraseña = (contraseña != null && !contraseña.trim().isEmpty());
        if (actualizarContraseña) {
            sqlUpdate.append(", contraseña = ?");
        }
        
        sqlUpdate.append(" WHERE ID_documento = ?");

        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sqlUpdate.toString())) {

            
            stmt.setString(1, nombre);
            stmt.setString(2, correo);
            stmt.setString(3, sexo);
            stmt.setInt(4, edad);
            stmt.setString(5, cinturonRango);
            stmt.setString(6, nacionalidad);
            
            int paramIndex = 7;
            if (actualizarContraseña) {
                stmt.setString(paramIndex, contraseña);
                paramIndex++;
            }
            
            stmt.setInt(paramIndex, idUsuario);

            int filasActualizadas = stmt.executeUpdate();

            if (filasActualizadas > 0) {
                
                session.setAttribute("usuarioLogueado", nombre);
                
                logger.info("Perfil actualizado exitosamente para usuario ID: {}", idUsuario);
                redirectAttributes.addFlashAttribute("mensajeExito", "¡Perfil actualizado correctamente!");
            } else {
                logger.warn("No se pudo actualizar el perfil para usuario ID: {}", idUsuario);
                redirectAttributes.addFlashAttribute("mensajeError", "No se pudo actualizar el perfil.");
            }

        } catch (SQLException e) {
            logger.error("Error SQL al actualizar perfil del usuario ID: {}", idUsuario, e);
            
            
            if (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("correo")) {
                redirectAttributes.addFlashAttribute("mensajeError", "El correo electrónico ya está en uso por otro usuario.");
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar el perfil. Inténtalo de nuevo.");
            }
            
            return "redirect:/perfil";
        }

        return "redirect:/perfil";
    }
}