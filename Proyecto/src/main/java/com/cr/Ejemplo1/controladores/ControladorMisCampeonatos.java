package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
import com.cr.Ejemplo1.modelo.Campeonato;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
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

@Controller
public class ControladorMisCampeonatos {

    private static final Logger logger = LoggerFactory.getLogger(ControladorMisCampeonatos.class);

    @GetMapping("/campeonato/manage/mis-campeonatos")
    public String mostrarMisCampeonatos(Model model,
                                        HttpServletResponse response,
                                        HttpSession session) {

        
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        
        Integer idUsuario = (Integer) session.getAttribute("id");

        if (idUsuario == null) {
            logger.warn("Intento de acceso no autorizado a /campeonato/manage/mis-campeonatos");
            session.setAttribute("redirectUrl", "/campeonato/manage/mis-campeonatos");
            return "redirect:/auth/inicioSesion";
        }

        logger.info("Usuario ID: {} accediendo a sus campeonatos", idUsuario);

        
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
            WHERE c.id_admin = ?
        """;

        List<Campeonato> misCampeonatos = new ArrayList<>();

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

                misCampeonatos.add(camp);
            }

        } catch (SQLException e) {
            logger.error("Error al obtener mis campeonatos", e);
            model.addAttribute("errorMsg", "Error al cargar tus campeonatos.");
            return "error-page";
        }

        model.addAttribute("campeonatos", misCampeonatos);
        return "campeonato/manage/lista-mis-campeonatos";
    }
}
