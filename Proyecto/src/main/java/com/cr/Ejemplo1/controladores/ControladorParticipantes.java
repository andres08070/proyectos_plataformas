package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
import com.cr.Ejemplo1.modelo.InscripcionDetalle;
import com.cr.Ejemplo1.modelo.ModalidadData; 


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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode; 
import com.fasterxml.jackson.core.JsonProcessingException; 
import java.io.IOException;

@Controller
public class ControladorParticipantes {

    private static final Logger logger = LoggerFactory.getLogger(ControladorParticipantes.class);
    private final ObjectMapper objectMapper = new ObjectMapper(); 

   

    @GetMapping("/campeonato/inscripciones/pendientes/{idCampeonato}")
    public String listarInscripcionesPendientes(@PathVariable("idCampeonato") Long idCampeonato,
                                                Model model,
                                                HttpServletResponse response,
                                                HttpSession session) {
        
        
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        Integer idUsuario = (Integer) session.getAttribute("id");
        if (idUsuario == null) {
            logger.warn("Acceso no autorizado a inscripciones pendientes.");
            return "redirect:/auth/inicioSesion"; 
        }

        String nombreCampeonato = "";
        List<ModalidadData> modalidades = new ArrayList<>();
        
        
        String sqlCampData = "SELECT nombre, json_modalidades FROM campeonato WHERE id = ? AND id_admin = ?";
        
        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sqlCampData)) {

            stmt.setLong(1, idCampeonato);
            stmt.setInt(2, idUsuario); 
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nombreCampeonato = rs.getString("nombre");
                String jsonModalidades = rs.getString("json_modalidades");
                
               
                if (jsonModalidades != null && !jsonModalidades.trim().isEmpty()) {
                    modalidades = extractAllModalidades(jsonModalidades);
                }
                
            } else {
                 model.addAttribute("errorMsg", "Campeonato no encontrado o no tienes permisos.");
                 return "error-page";
            }
        } catch (SQLException e) {
            logger.error("Error SQL al obtener datos del campeonato {}", idCampeonato, e);
            model.addAttribute("errorMsg", "Error al cargar datos del campeonato.");
            return "error-page";
        } catch (JsonProcessingException e) {
            
            logger.error("Error al procesar JSON de modalidades para el campeonato {}", idCampeonato, e);
            model.addAttribute("errorMsg", "Error de configuración: El JSON de modalidades es inválido.");
            return "error-page";
        } catch (IOException e) {
             
            logger.error("Error I/O al leer JSON de modalidades para el campeonato {}", idCampeonato, e);
            model.addAttribute("errorMsg", "Error interno al leer datos de la base de datos.");
            return "error-page";
        }
       
        String sqlSelect = """
            SELECT
                ci.id_inscripcion,
                u.nombreC,
                u.sexo,
                u.edad,
                u.cinturon_rango,
                ci.id_modalidad,
                ci.fecha_inscripcion
            FROM campeonatos_inscripcion ci
            INNER JOIN usuarios u ON ci.id_usuario = u.ID_documento
            INNER JOIN campeonato c ON ci.id_campeonato = c.id
            WHERE ci.id_campeonato = ? AND ci.estado = 'pendiente' AND c.id_admin = ?
            """;

        List<InscripcionDetalle> inscripcionesPendientes = new ArrayList<>();

        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sqlSelect)) {

            stmt.setLong(1, idCampeonato);
            stmt.setInt(2, idUsuario); 

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InscripcionDetalle detalle = new InscripcionDetalle();
                detalle.setIdInscripcion(rs.getLong("id_inscripcion"));
                detalle.setNombreParticipante(rs.getString("nombreC"));
                detalle.setSexo(rs.getString("sexo"));
                detalle.setEdad(rs.getInt("edad"));
                detalle.setCinturonRango(rs.getString("cinturon_rango"));
                
                String idModalidadInscripcion = rs.getString("id_modalidad");
                
                
                String nombreModalidad = "Modalidad No Encontrada";
                for (ModalidadData m : modalidades) {
                    if (m.getIdModalidad().equals(idModalidadInscripcion)) {
                        nombreModalidad = m.getName();
                        break;
                    }
                }
                
                detalle.setIdModalidad(idModalidadInscripcion);
                detalle.setNombreModalidad(nombreModalidad); 
                
                detalle.setFechaInscripcion(rs.getTimestamp("fecha_inscripcion").toLocalDateTime());
                
                inscripcionesPendientes.add(detalle);
            }

        } catch (SQLException e) {
            logger.error("Error SQL al obtener inscripciones pendientes para el campeonato {}", idCampeonato, e);
            model.addAttribute("errorMsg", "Error al cargar las inscripciones de la base de datos.");
            return "error-page";
        }

        model.addAttribute("idCampeonato", idCampeonato);
        model.addAttribute("nombreCampeonato", nombreCampeonato); 
        model.addAttribute("inscripciones", inscripcionesPendientes);
        return "campeonato/manage/MostrarInscritos";
    }
    
    
    @PostMapping("/campeonato/inscripciones/procesar")
    public String procesarInscripcion(@RequestParam("id_inscripcion") Long idInscripcion,
                                      @RequestParam("accion") String accion,
                                      @RequestParam("id_campeonato") Long idCampeonato,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {

        
        Integer idUsuario = (Integer) session.getAttribute("id");
        if (idUsuario == null) {
            return "redirect:/auth/inicioSesion";
        }

        String nuevoEstado;
        if ("aceptar".equals(accion)) {
            nuevoEstado = "aceptada";
        } else if ("rechazar".equals(accion)) {
            nuevoEstado = "rechazada";
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Acción de procesamiento no válida.");
            return "redirect:/campeonato/inscripciones/pendientes/" + idCampeonato;
        }

        String sqlUpdate = """
            UPDATE campeonatos_inscripcion ci
            INNER JOIN campeonato c ON ci.id_campeonato = c.id
            SET ci.estado = ?
            WHERE ci.id_inscripcion = ? AND c.id_admin = ?
            """;
        
        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sqlUpdate)) {

            stmt.setString(1, nuevoEstado);
            stmt.setLong(2, idInscripcion);
            stmt.setInt(3, idUsuario); 

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                redirectAttributes.addFlashAttribute("successMsg", "Inscripción ID " + idInscripcion + " fue " + nuevoEstado + ".");
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "Error: No se encontró la inscripción o no tienes permisos para modificarla.");
            }

        } catch (SQLException e) {
            logger.error("Error SQL al procesar la inscripción {}", idInscripcion, e);
            redirectAttributes.addFlashAttribute("errorMsg", "Error interno al actualizar la base de datos.");
        }

        return "redirect:/campeonato/inscripciones/pendientes/" + idCampeonato;
    }



    private List<ModalidadData> extractAllModalidades(String jsonModalidades) 
            throws JsonProcessingException, IOException {

        List<ModalidadData> modalidadesList = new ArrayList<>();
        
       
        JsonNode rootNode = objectMapper.readTree(jsonModalidades);

        
        rootNode.fields().forEachRemaining(entry -> {
            String idModalidad = entry.getKey();
            JsonNode modalidadNode = entry.getValue();
            
            try {
                
                ModalidadData modalidad = 
                    objectMapper.treeToValue(modalidadNode, ModalidadData.class);

                
                modalidad.setIdModalidad(idModalidad);
                modalidadesList.add(modalidad);
            } catch (JsonProcessingException e) {
                
                logger.error("Error al convertir ModalidadData con ID: {}", idModalidad, e);
            }
        });

        return modalidadesList;
    }
}