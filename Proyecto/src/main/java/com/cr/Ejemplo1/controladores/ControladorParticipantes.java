package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
import com.cr.Ejemplo1.modelo.InscripcionDetalle;
import com.cr.Ejemplo1.modelo.ModalidadData; 
// import com.cr.Ejemplo1.modelo.CampeonatoModalidades; // <-- YA NO ES NECESARIO

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

// Importaciones de Jackson para JSON
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode; // <-- NUEVA IMPORTACIÓN
import com.fasterxml.jackson.core.JsonProcessingException; // <-- NUEVA IMPORTACIÓN
import java.io.IOException;

@Controller
public class ControladorParticipantes {

    private static final Logger logger = LoggerFactory.getLogger(ControladorParticipantes.class);
    private final ObjectMapper objectMapper = new ObjectMapper(); 

    // =========================================================================
    // 1. Método GET: Listar Inscripciones Pendientes (CORREGIDO CON JSONNODE)
    // =========================================================================

    @GetMapping("/campeonato/inscripciones/pendientes/{idCampeonato}")
    public String listarInscripcionesPendientes(@PathVariable("idCampeonato") Long idCampeonato,
                                                Model model,
                                                HttpServletResponse response,
                                                HttpSession session) {
        
        // ... (Prevención de Cache y Verificación de Sesión, sin cambios) ...
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
        
        // --- PARTE A: OBTENER NOMBRE DEL CAMPEONATO Y JSON DE MODALIDADES ---
        String sqlCampData = "SELECT nombre, json_modalidades FROM campeonato WHERE id = ? AND id_admin = ?";
        
        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sqlCampData)) {

            stmt.setLong(1, idCampeonato);
            stmt.setInt(2, idUsuario); 
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nombreCampeonato = rs.getString("nombre");
                String jsonModalidades = rs.getString("json_modalidades");
                
                // 🛑 NUEVA LÓGICA DE PROCESAMIENTO JSON (Tomada de ControladorInscripcion)
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
            // Captura errores específicos de Jackson
            logger.error("Error al procesar JSON de modalidades para el campeonato {}", idCampeonato, e);
            model.addAttribute("errorMsg", "Error de configuración: El JSON de modalidades es inválido.");
            return "error-page";
        } catch (IOException e) {
             // Esto ya no debería ocurrir si el formato es el correcto
            logger.error("Error I/O al leer JSON de modalidades para el campeonato {}", idCampeonato, e);
            model.addAttribute("errorMsg", "Error interno al leer datos de la base de datos.");
            return "error-page";
        }
        // ---------------------------------------------------------------------

        // --- PARTE B: OBTENER INSCRIPCIONES Y MAPEAR NOMBRES (Sin cambios) ---
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
                
                // Mapeo Clave: Buscar el nombre en la lista de modalidades
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
    
    // =========================================================================
    // 2. Método POST: Procesar Aceptar/Rechazar (Sin cambios)
    // =========================================================================

    @PostMapping("/campeonato/inscripciones/procesar")
    public String procesarInscripcion(@RequestParam("id_inscripcion") Long idInscripcion,
                                      @RequestParam("accion") String accion,
                                      @RequestParam("id_campeonato") Long idCampeonato,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {

        // ... (La lógica de actualización SQL y redirección es idéntica y correcta) ...
        // ... (Se mantiene la misma lógica para el POST) ...
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


    // =====================================================================
    // MÉTODO AUXILIAR - NUEVO PARA PROCESAR EL JSON TIPO MAP
    // =====================================================================
    
    /**
     * Procesa el JSON de modalidades que tiene la estructura de un Map 
     * (ID -> ModalidadData) y devuelve una lista de ModalidadData.
     */
    private List<ModalidadData> extractAllModalidades(String jsonModalidades) 
            throws JsonProcessingException, IOException {

        List<ModalidadData> modalidadesList = new ArrayList<>();
        
        // 1. Leer el JSON como un objeto genérico (JsonNode)
        JsonNode rootNode = objectMapper.readTree(jsonModalidades);

        // 2. Iterar sobre los campos (claves/valores) del objeto principal
        rootNode.fields().forEachRemaining(entry -> {
            String idModalidad = entry.getKey();
            JsonNode modalidadNode = entry.getValue();
            
            try {
                // 3. Convertir el nodo de valor (el objeto ModalidadData) a la clase Java
                ModalidadData modalidad = 
                    objectMapper.treeToValue(modalidadNode, ModalidadData.class);

                // 4. Asignar manualmente el ID (ya que la clave no es un campo interno)
                modalidad.setIdModalidad(idModalidad);
                modalidadesList.add(modalidad);
            } catch (JsonProcessingException e) {
                // Si alguna modalidad individual falla en el mapeo, se registra y se salta
                logger.error("Error al convertir ModalidadData con ID: {}", idModalidad, e);
            }
        });

        return modalidadesList;
    }
}