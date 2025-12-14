package com.cr.Ejemplo1.controladores;

// Importaciones para JDBC
import com.cr.Ejemplo1.BD;
import com.cr.Ejemplo1.modelo.Campeonato;
import com.cr.Ejemplo1.modelo.ModalidadData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorCrearCampeonato {

    private static final Logger logger = LoggerFactory.getLogger(ControladorCrearCampeonato.class);

    // Objeto para deserializar el JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    // =====================================================================
    // POST - GUARDAR CAMPEONATO
    // =====================================================================
    @PostMapping("/guardar-campeonato")
    public String guardarCampeonato(@ModelAttribute Campeonato campeonato, HttpSession session) {

        String idCreadorString = (String) session.getAttribute("id");
        Integer idCreador;

        if (idCreadorString == null) {
            logger.error("Usuario no logueado o ID no encontrado en la sesión.");
            return "redirect:/login";
        }

        try {
            idCreador = Integer.parseInt(idCreadorString);
        } catch (NumberFormatException e) {
            logger.error("El ID de sesión no es un número válido: {}", idCreadorString, e);
            return "error-page";
        }

        logger.info(
            "Iniciando guardado del Campeonato '{}' por el usuario ID: {}",
            campeonato.getNombre(),
            idCreador
        );

        String sqlInsert = """
            INSERT INTO campeonato
            (nombre, fecha_inicio, fecha_fin, ubicacion, num_areas, json_modalidades, id_admin)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (
            Connection con = BD.conexion();
            PreparedStatement insertar = con.prepareStatement(sqlInsert)
        ) {

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
    // GET - LISTA DE CAMPEONATOS
    // =====================================================================
    @GetMapping("/campeonato/lista")
    public String mostrarCampeonatos(Model model) {

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
        """;

        List<Campeonato> listaCampeonatos = new ArrayList<>();

        try (
            Connection con = BD.conexion();
            PreparedStatement stmt = con.prepareStatement(sqlSelect);
            ResultSet rs = stmt.executeQuery()
        ) {

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
}
