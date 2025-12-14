package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
import com.cr.Ejemplo1.modelo.Campeonato;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.cr.Ejemplo1.util.SessionUtil;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class ControladorCrearCampeonato {
    
     @Autowired
    private SessionUtil sessionUtil;

    private static final Logger logger = LoggerFactory.getLogger(ControladorCrearCampeonato.class);

    // =====================================================================
    // POST - GUARDAR CAMPEONATO (YA TIENE VERIFICACIÓN)
    // =====================================================================
    @PostMapping("/guardar-campeonato")
    public String guardarCampeonato(@ModelAttribute Campeonato campeonato, 
                                    HttpSession session,
                                    HttpServletResponse response) {
        
        // 🔒 PREVENIR CACHE
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        // Verificar sesión
        String idCreadorString = (String) session.getAttribute("id");
        if (idCreadorString == null) {
            logger.error("Usuario no logueado. Redirigiendo a login.");
            return "redirect:/auth/inicioSesion";
        }

        // Resto del código original...
        Integer idCreador;
        try {
            idCreador = Integer.parseInt(idCreadorString);
        } catch (NumberFormatException e) {
            logger.error("El ID de sesión no es un número válido: {}", idCreadorString, e);
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
                return "redirect:/inicio";
            }

            logger.error("No se insertó el campeonato.");
            return "error-page";

        } catch (SQLException e) {
            logger.error("Error SQL al guardar campeonato:", e);
            return "error-page";
        } catch (Exception e) {
            logger.error("Error inesperado:", e);
            return "error-page";
        }
    }

    // =====================================================================
    // GET - LISTA DE CAMPEONATOS (CORREGIDO - VERIFICA SESIÓN)
    // =====================================================================
    @GetMapping("/campeonato/lista")
public String mostrarCampeonatos(Model model, 
                                 HttpServletResponse response, 
                                 HttpSession session) {
    
    // 🔒 PREVENIR CACHE
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Expires", "0");
    
    // ✅ VERIFICAR SI EL USUARIO TIENE SESIÓN ACTIVA
    String idUsuarioStr = (String) session.getAttribute("id");
    if (idUsuarioStr == null || idUsuarioStr.trim().isEmpty()) {
        logger.warn("Intento de acceso no autorizado a /campeonato/lista");
        
        // Guardar la URL a la que intentaba acceder para redirigir después del login
        session.setAttribute("redirectUrl", "/campeonato/lista");
        
        return "redirect:/auth/inicioSesion";
    }
    
    int idUsuario;
    try {
        idUsuario = Integer.parseInt(idUsuarioStr);
    } catch (NumberFormatException e) {
        logger.error("El ID de sesión no es un número válido: {}", idUsuarioStr, e);
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
        WHERE c.id_admin != ?   -- Excluir los campeonatos del usuario actual
    """;

    List<Campeonato> listaCampeonatos = new ArrayList<>();

    try (Connection con = BD.conexion();
         PreparedStatement stmt = con.prepareStatement(sqlSelect)) {

        stmt.setInt(1, idUsuario);  // Establecer el parámetro

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
    // GET - PÁGINA PARA CREAR CAMPEONATO (TAMBIÉN DEBE VERIFICAR SESIÓN)
    // =====================================================================
    @GetMapping("/CrearCampeonato")
    public String mostrarFormularioCrear(Model model, 
                                         HttpServletResponse response, 
                                         HttpSession session) {
        
        // 🔒 PREVENIR CACHE
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        // ✅ VERIFICAR SESIÓN
        String idUsuario = (String) session.getAttribute("id");
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            logger.warn("Intento de acceso no autorizado a /CrearCampeonato");
            session.setAttribute("redirectUrl", "/CrearCampeonato");
            return "redirect:/auth/inicioSesion";
        }
        
        logger.info("Usuario ID: {} accediendo a CrearCampeonato", idUsuario);
        
        // Pasar un objeto campeonato vacío al formulario
        model.addAttribute("campeonato", new Campeonato());
        return "campeonato/CrearCampeonato";
    }
    
    
}