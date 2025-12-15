package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
import com.cr.Ejemplo1.modelo.Campeonato;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorCrearCampeonato {

    private static final Logger logger = LoggerFactory.getLogger(ControladorCrearCampeonato.class);

    // =====================================================================
    // POST - GUARDAR CAMPEONATO
    // =====================================================================
    @PostMapping("/guardar-campeonato")
    public String guardarCampeonato(@ModelAttribute Campeonato campeonato,
                                    HttpSession session,
                                    HttpServletResponse response,
                                    RedirectAttributes redirectAttributes) {

        // 🔒 PREVENIR CACHE
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // ✅ VERIFICAR SESIÓN (CORRECTO)
        Integer idCreador = (Integer) session.getAttribute("id");
        if (idCreador == null) {
            logger.error("Usuario no logueado. Redirigiendo a login.");
            return "redirect:/auth/inicioSesion";
        }

        logger.info("Iniciando guardado del Campeonato '{}' por el usuario ID: {}",
                campeonato.getNombre(), idCreador);

        String sqlInsert = """
            INSERT INTO campeonato
            (nombre, fecha_inicio, fecha_fin, ubicacion, num_areas, json_modalidades, id_admin)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = BD.conexion();
             PreparedStatement insertar = con.prepareStatement(sqlInsert)) {

            insertar.setString(1, campeonato.getNombre());
            insertar.setDate(2, Date.valueOf(campeonato.getFechaInicio()));
            insertar.setDate(3, Date.valueOf(campeonato.getFechaFin()));
            insertar.setString(4, campeonato.getUbicacion());
            insertar.setInt(5, campeonato.getNumAreas());
            insertar.setString(6, campeonato.getJsonModalidades());
            insertar.setInt(7, idCreador);

            int filasAfectadas = insertar.executeUpdate();

            if (filasAfectadas > 0) {
                logger.info("Campeonato '{}' guardado exitosamente.", campeonato.getNombre());
                redirectAttributes.addFlashAttribute("mensajeExito", "Campeonato creado exitosamente.");
                return "redirect:/campeonato/manage/mis-campeonatos";
            }

            logger.error("No se insertó el campeonato.");
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo crear el campeonato.");
            return "redirect:/campeonato/manage/mis-campeonatos";  // Redirigir a la lista de mis campeonatos

        } catch (SQLException e) {
            logger.error("Error SQL al guardar campeonato:", e);
            redirectAttributes.addFlashAttribute("mensajeError", "Error al crear el campeonato. Inténtalo de nuevo.");
            return "redirect:/campeonato/manage/mis-campeonatos";
        } catch (Exception e) {
            logger.error("Error inesperado:", e);
            redirectAttributes.addFlashAttribute("mensajeError", "Error inesperado. Inténtalo de nuevo.");
            return "redirect:/campeonato/manage/mis-campeonatos";
        }
    }

    // =====================================================================
    // GET - LISTA DE CAMPEONATOS
    // =====================================================================
    @GetMapping("/campeonato/lista")
    public String mostrarCampeonatos(Model model,
                                     HttpServletResponse response,
                                     HttpSession session) {

        // 🔒 PREVENIR CACHE
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // ✅ VERIFICAR SESIÓN (CORRECTO)
        Integer idUsuario = (Integer) session.getAttribute("id");
        if (idUsuario == null) {
            logger.warn("Intento de acceso no autorizado a /campeonato/lista");
            session.setAttribute("redirectUrl", "/campeonato/lista");
            return "redirect:/auth/inicioSesion";
        }

        logger.info("Usuario ID: {} accediendo a lista de campeonatos", idUsuario);

        String sqlSelect = """
            SELECT
                c.id,
                c.nombre,
                c.fecha_inicio,
                c.fecha_fin,
                c.ubicacion,
                u.nombreC AS nombre_creador
            FROM campeonato c
            INNER JOIN usuarios u ON c.id_admin = u.ID_documento
            WHERE c.id_admin != ?
        """;

        List<Campeonato> listaCampeonatos = new ArrayList<>();

        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sqlSelect)) {

            stmt.setInt(1, idUsuario);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Campeonato camp = new Campeonato();
                camp.setId(rs.getLong("id"));
                camp.setNombre(rs.getString("nombre"));
                camp.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                camp.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                camp.setUbicacion(rs.getString("ubicacion"));
                camp.setNombreCreador(rs.getString("nombre_creador"));

                listaCampeonatos.add(camp);
            }

        } catch (SQLException e) {
            logger.error("Error al obtener campeonatos:", e);
            model.addAttribute("errorMsg", "Error al cargar campeonatos.");
            return "error-page";
        }

        model.addAttribute("campeonatos", listaCampeonatos);
        return "campeonato/lista-campeonatos";
    }

    // =====================================================================
    // GET - PÁGINA CREAR CAMPEONATO
    // =====================================================================
    @GetMapping("/CrearCampeonato")
    public String mostrarFormularioCrear(Model model,
                                         HttpServletResponse response,
                                         HttpSession session) {

        // 🔒 PREVENIR CACHE
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // ✅ VERIFICAR SESIÓN (CORRECTO)
        Integer idUsuario = (Integer) session.getAttribute("id");
        if (idUsuario == null) {
            logger.warn("Intento de acceso no autorizado a /CrearCampeonato");
            session.setAttribute("redirectUrl", "/CrearCampeonato");
            return "redirect:/auth/inicioSesion";
        }

        logger.info("Usuario ID: {} accediendo a CrearCampeonato", idUsuario);

        model.addAttribute("campeonato", new Campeonato());
        return "campeonato/CrearCampeonato";
    }
    
    @PostMapping("/campeonato/eliminar/{id}")
    public String eliminarCampeonato(@PathVariable("id") Long idCampeonato,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        // ✅ Verificar sesión
        Integer idUsuario = (Integer) session.getAttribute("id");
        if (idUsuario == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Debes iniciar sesión para realizar esta acción.");
            return "redirect:/auth/inicioSesion";
        }

        logger.info("Usuario ID: {} intentando eliminar campeonato ID: {}", idUsuario, idCampeonato);

        // SQL para eliminar primero las inscripciones (debido a la restricción de clave foránea)
        String sqlEliminarInscripciones = "DELETE FROM campeonatos_inscripcion WHERE id_campeonato = ?";
        String sqlEliminarCampeonato = "DELETE FROM campeonato WHERE id = ? AND id_admin = ?";

        try (Connection con = BD.conexion()) {
            // Iniciar transacción
            con.setAutoCommit(false);

            try {
                // 1. Eliminar inscripciones asociadas
                try (PreparedStatement stmtInscripciones = con.prepareStatement(sqlEliminarInscripciones)) {
                    stmtInscripciones.setLong(1, idCampeonato);
                    int filasInscripciones = stmtInscripciones.executeUpdate();
                    logger.info("Eliminadas {} inscripciones del campeonato ID: {}", filasInscripciones, idCampeonato);
                }

                // 2. Eliminar campeonato (solo si es del usuario)
                try (PreparedStatement stmtCampeonato = con.prepareStatement(sqlEliminarCampeonato)) {
                    stmtCampeonato.setLong(1, idCampeonato);
                    stmtCampeonato.setInt(2, idUsuario);
                    int filasCampeonato = stmtCampeonato.executeUpdate();

                    if (filasCampeonato > 0) {
                        con.commit();
                        logger.info("Campeonato ID: {} eliminado exitosamente por usuario ID: {}", idCampeonato, idUsuario);
                        redirectAttributes.addFlashAttribute("mensajeExito", 
                            "Campeonato eliminado correctamente.");
                    } else {
                        con.rollback();
                        logger.warn("No se pudo eliminar el campeonato. Posiblemente no existe o no tienes permisos.");
                        redirectAttributes.addFlashAttribute("mensajeError", 
                            "No se pudo eliminar el campeonato. Verifica que existe y que tienes permisos.");
                    }
                }

            } catch (SQLException e) {
                con.rollback();
                logger.error("Error durante la eliminación del campeonato ID: {}", idCampeonato, e);
                redirectAttributes.addFlashAttribute("mensajeError", 
                    "Error al eliminar el campeonato. Inténtalo de nuevo.");
                throw e;
            }

            con.setAutoCommit(true);

        } catch (SQLException e) {
            logger.error("Error SQL al eliminar campeonato ID: {}", idCampeonato, e);
            redirectAttributes.addFlashAttribute("mensajeError", 
                "Error de base de datos al eliminar el campeonato.");
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar campeonato ID: {}", idCampeonato, e);
            redirectAttributes.addFlashAttribute("mensajeError", 
                "Error inesperado. Por favor, contacta al administrador.");
        }

        return "redirect:/campeonato/manage/mis-campeonatos";
    }
}
