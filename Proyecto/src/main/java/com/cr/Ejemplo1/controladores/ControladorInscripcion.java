package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
import com.cr.Ejemplo1.modelo.Campeonato;
import com.cr.Ejemplo1.modelo.ModalidadData;
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
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
import org.springframework.web.bind.annotation.PathVariable;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorInscripcion {

    private static final Logger logger = LoggerFactory.getLogger(ControladorInscripcion.class);
    private final ObjectMapper objectMapper = new ObjectMapper(); 

   
    @GetMapping("/inscripciones/{id}")
public String mostrarDetalleCampeonato(
        @PathVariable("id") Long id,
        @RequestParam(value = "showInscribed", defaultValue = "false") boolean showInscribed,
        @RequestParam(value = "view", required = false) String view,
        Model model,
        HttpSession session,
        HttpServletResponse response
) {

        
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        
        Object idSesion = session.getAttribute("id");
        if (idSesion == null) {
            return "redirect:/auth/inicioSesion";
        }

        Long idUsuario = Long.valueOf(idSesion.toString());

        Campeonato campeonato = null;
        List<Map<String, String>> modalidadesProcesadas = new ArrayList<>();

        
        String sqlSelect = """
            SELECT
                c.id,
                c.nombre,
                c.ubicacion,
                c.json_modalidades,
                c.fecha_inicio,
                c.fecha_fin,
                c.num_areas,
                u.nombreC AS nombre_creador
            FROM campeonato c
            INNER JOIN usuarios u ON c.id_admin = u.ID_documento
            WHERE c.id = ?
        """;

        
        String sqlModalidadesInscritas = """
            SELECT id_modalidad
            FROM campeonatos_inscripcion
            WHERE id_campeonato = ? AND id_usuario = ?
        """;

        try (Connection con = BD.conexion();
                PreparedStatement stmt = con.prepareStatement(sqlSelect)) {

            
            Set<String> modalidadesInscritas = new HashSet<>();

            try (PreparedStatement ps = con.prepareStatement(sqlModalidadesInscritas)) {
                ps.setLong(1, id);
                ps.setLong(2, idUsuario);

                try (ResultSet rsModalidades = ps.executeQuery()) {
                    while (rsModalidades.next()) {
                        modalidadesInscritas.add(rsModalidades.getString("id_modalidad"));
                    }
                }
            }

            
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    model.addAttribute("errorMsg", "Campeonato no encontrado.");
                    return "error-page";
                }

                campeonato = new Campeonato();
                campeonato.setId(rs.getLong("id"));
                campeonato.setNombre(rs.getString("nombre"));
                campeonato.setUbicacion(rs.getString("ubicacion"));
                campeonato.setNombreCreador(rs.getString("nombre_creador"));
                campeonato.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                campeonato.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                campeonato.setNumAreas(rs.getInt("num_areas"));

                String jsonModalidades = rs.getString("json_modalidades");

                if (jsonModalidades != null && !jsonModalidades.trim().isEmpty()) {
                    JsonNode rootNode = objectMapper.readTree(jsonModalidades);

                    
                    rootNode.fields().forEachRemaining(entry -> {
                        String idModalidad = entry.getKey();
                        boolean estaInscrito = modalidadesInscritas.contains(idModalidad);

                        
                        if (showInscribed) {
                            
                            if (!estaInscrito) {
                                return; 
                            }
                        } else {
                           
                            if (estaInscrito) {
                                return; 
                            }
                        }

                        JsonNode modalidadNode = entry.getValue();
                        Map<String, String> modalidadLimpia = new LinkedHashMap<>();
                        modalidadLimpia.put("idModalidad", idModalidad);

                        String nombre = getStringValue(modalidadNode, "name");
                        String descripcion = getStringValue(modalidadNode, "desc");
                        String peso = getArrayValue(modalidadNode, "peso");
                        String rango = getArrayValue(modalidadNode, "rango");
                        String edad = getArrayValue(modalidadNode, "edad");
                        String genero = getStringValue(modalidadNode, "genero");

                        if (!nombre.isEmpty()) modalidadLimpia.put("Nombre", nombre);
                        if (!descripcion.isEmpty()) modalidadLimpia.put("Descripción", descripcion);
                        if (!peso.isEmpty()) modalidadLimpia.put("Peso(s)", peso);
                        if (!rango.isEmpty()) modalidadLimpia.put("Rango(s)", rango);
                        if (!edad.isEmpty()) modalidadLimpia.put("Edad(es)", edad);
                        if (!genero.isEmpty()) modalidadLimpia.put("Género", genero);

                        if (modalidadLimpia.size() > 1) {
                            modalidadesProcesadas.add(modalidadLimpia);
                        }
                    });
                }

                model.addAttribute("campeonato", campeonato);
                model.addAttribute("modalidades", modalidadesProcesadas);
            }

        } catch (Exception e) {
            
            model.addAttribute("errorMsg", "Error al cargar las inscripciones.");
            return "error-page";
        }

        if ("readonly".equals(view)) {
            return "campeonato/manage/detallescreado";
        }
        
        return "campeonato/manage/inscripciones";
    }



    
    private String getStringValue(JsonNode parentNode, String fieldName) {
        if (parentNode.has(fieldName) && !parentNode.get(fieldName).isNull()) {
            String value = parentNode.get(fieldName).asText();
            return (value != null && !value.trim().isEmpty()) ? value.trim() : "";
        }
        return "";
    }

    
    private String getArrayValue(JsonNode parentNode, String fieldName) {
        if (parentNode.has(fieldName) && parentNode.get(fieldName).isArray()) {

            ArrayNode arrayNode = (ArrayNode) parentNode.get(fieldName);
            List<String> valores = new ArrayList<>();

            for (JsonNode item : arrayNode) {
                valores.add(item.asText());
            }

           
            return String.join(", ", valores);
        }
        return "";
    }

    @PostMapping("/inscripciones/inscribir")
    public String inscribirUsuarioModalidad(
            @RequestParam("idCampeonato") Long idCampeonato,
            @RequestParam("idModalidad") String idModalidad,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        Object idSesion = session.getAttribute("id");

        if (idSesion == null) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    "Debes iniciar sesión para inscribirte."
            );
            return "redirect:/auth/inicioSesion";
        }

        Long idUsuario = Long.valueOf(idSesion.toString());

        System.out.println("📝 INSCRIPCIÓN");
        System.out.println("Campeonato: " + idCampeonato);
        System.out.println("Usuario: " + idUsuario);
        System.out.println("Modalidad: " + idModalidad);

        String sqlInsert = """
            INSERT INTO campeonatos_inscripcion
            (id_campeonato, id_usuario, id_modalidad)
            VALUES (?, ?, ?)
        """;

        try (
            Connection con = BD.conexion();
            PreparedStatement stmt = con.prepareStatement(sqlInsert)
        ) {

            stmt.setLong(1, idCampeonato);
            stmt.setLong(2, idUsuario);
            stmt.setString(3, idModalidad);

            stmt.executeUpdate();

            redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "¡Inscripción exitosa! Has sido inscrito en la modalidad seleccionada."
            );

        } catch (SQLException e) {
            logger.error("Error al guardar la inscripción", e);
            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    "No se pudo completar la inscripción. Inténtalo de nuevo."
            );
        }

        return "redirect:/inscripciones/" + idCampeonato;
    }

    
    @GetMapping("/mis-inscripciones")
    public String mostrarMisInscripciones(
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Object idSesion = session.getAttribute("id");

        if (idSesion == null) {
            redirectAttributes.addFlashAttribute(
                    "mensajeAdvertencia",
                    "Debes iniciar sesión para ver tus inscripciones."
            );
            return "redirect:/auth/inicioSesion";
        }

        Long idUsuario = Long.valueOf(idSesion.toString());
        System.out.println("👉 ID USUARIO SESIÓN: " + idUsuario);

       
        String sql = """
            SELECT
                ci.id_inscripcion,
                ci.id_modalidad,
                c.id AS id_campeonato,
                c.nombre AS nombre_campeonato,
                c.ubicacion,
                c.fecha_inicio,
                c.fecha_fin,
                c.json_modalidades,
                u.nombreC AS creador_nombre,
                COALESCE(ci.estado, 'PENDIENTE') AS estado_inscripcion,
                usr.nombreC AS nombre_participante,
                usr.cinturon_rango AS categoria_usuario
            FROM campeonatos_inscripcion ci
            INNER JOIN campeonato c ON ci.id_campeonato = c.id
            INNER JOIN usuarios u ON c.id_admin = u.ID_documento
            INNER JOIN usuarios usr ON ci.id_usuario = usr.ID_documento
            WHERE ci.id_usuario = ?
            ORDER BY c.fecha_inicio DESC
            """;

        
        Map<Long, Map<String, Object>> campeonatosMap = new LinkedHashMap<>();

        try (
            Connection con = BD.conexion();
            PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setLong(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long idCampeonato = rs.getLong("id_campeonato");
                    Long idInscripcion = rs.getLong("id_inscripcion");
                    
                    
                    if (!campeonatosMap.containsKey(idCampeonato)) {
                        Map<String, Object> campeonatoData = new LinkedHashMap<>();
                        campeonatoData.put("idCampeonato", idCampeonato);
                        campeonatoData.put("nombreCampeonato", rs.getString("nombre_campeonato"));
                        campeonatoData.put("ubicacion", rs.getString("ubicacion"));
                        campeonatoData.put("fechaInicio", rs.getDate("fecha_inicio"));
                        campeonatoData.put("fechaFin", rs.getDate("fecha_fin"));
                        campeonatoData.put("creadorNombre", rs.getString("creador_nombre"));
                        
                        
                        campeonatoData.put("nombreParticipante", rs.getString("nombre_participante"));
                        campeonatoData.put("categoriaUsuario", rs.getString("categoria_usuario"));
                        
                        campeonatoData.put("modalidadesInscritas", new ArrayList<Map<String, Object>>()); 
                        
                        campeonatosMap.put(idCampeonato, campeonatoData);
                    }

                   
                    
                    String idModalidad = rs.getString("id_modalidad");
                    String jsonModalidades = rs.getString("json_modalidades");
                    String estadoInscripcion = rs.getString("estado_inscripcion"); 
                    
                    ModalidadData modalidad = extractModalidadDetails(jsonModalidades, idModalidad);
                    
                    if (modalidad != null) {
                       
                        Map<String, Object> modalidadDetalle = new LinkedHashMap<>();
                        modalidadDetalle.put("idInscripcion", idInscripcion); 
                        modalidadDetalle.put("idModalidad", modalidad.getIdModalidad());
                        modalidadDetalle.put("nombreModalidad", modalidad.getName());
                        modalidadDetalle.put("descripcionModalidad", modalidad.getDesc());
                        modalidadDetalle.put("estadoInscripcion", estadoInscripcion); 
                       
                        
                        
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> modalidadesList = 
                            (List<Map<String, Object>>) campeonatosMap.get(idCampeonato).get("modalidadesInscritas");
                        
                        modalidadesList.add(modalidadDetalle);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("❌ Error al cargar inscripciones del usuario {}", idUsuario, e);
            model.addAttribute("errorMsg", "Error al cargar inscripciones.");
            return "error-page";
        }

        
        List<Map<String, Object>> inscripcionesAgrupadas = new ArrayList<>(campeonatosMap.values());
        
        System.out.println("📦 TOTAL CAMPEONATOS INSCRITOS: " + inscripcionesAgrupadas.size());

        model.addAttribute("inscripciones", inscripcionesAgrupadas);
        return "campeonato/manage/mis-inscripciones";
    }
    
   

    @PostMapping("/mis-inscripciones/eliminar/{idInscripcion}")
    public String eliminarInscripcion(
            @PathVariable("idInscripcion") Long idInscripcion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Object idSesion = session.getAttribute("id");

        if (idSesion == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Debes iniciar sesión para realizar esta acción.");
            return "redirect:/auth/inicioSesion";
        }

        Long idUsuario = Long.valueOf(idSesion.toString());

        logger.info("Usuario ID: {} intentando eliminar inscripción ID: {}", idUsuario, idInscripcion);

        
        String sqlVerificar = """
            SELECT COUNT(*) 
            FROM campeonatos_inscripcion 
            WHERE id_inscripcion = ? AND id_usuario = ?
        """;

        String sqlEliminar = "DELETE FROM campeonatos_inscripcion WHERE id_inscripcion = ? AND id_usuario = ?";

        try (Connection con = BD.conexion()) {

           
            try (PreparedStatement stmtVerificar = con.prepareStatement(sqlVerificar)) {
                stmtVerificar.setLong(1, idInscripcion);
                stmtVerificar.setLong(2, idUsuario);

                try (ResultSet rs = stmtVerificar.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        logger.warn("Usuario ID: {} no tiene permiso para eliminar inscripción ID: {}", idUsuario, idInscripcion);
                        redirectAttributes.addFlashAttribute("mensajeError", 
                            "No tienes permiso para eliminar esta inscripción o no existe.");
                        return "redirect:/mis-inscripciones";
                    }
                }
            }

            
            try (PreparedStatement stmtEliminar = con.prepareStatement(sqlEliminar)) {
                stmtEliminar.setLong(1, idInscripcion);
                stmtEliminar.setLong(2, idUsuario);

                int filasEliminadas = stmtEliminar.executeUpdate();

                if (filasEliminadas > 0) {
                    logger.info("Inscripción ID: {} eliminada exitosamente por usuario ID: {}", idInscripcion, idUsuario);
                    redirectAttributes.addFlashAttribute("mensajeExito", 
                        "Inscripción eliminada correctamente.");
                } else {
                    logger.warn("No se pudo eliminar la inscripción ID: {}", idInscripcion);
                    redirectAttributes.addFlashAttribute("mensajeError", 
                        "No se pudo eliminar la inscripción.");
                }
            }

        } catch (SQLException e) {
            logger.error("Error SQL al eliminar inscripción ID: {}", idInscripcion, e);
            redirectAttributes.addFlashAttribute("mensajeError", 
                "❌ Error de base de datos al eliminar la inscripción.");
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar inscripción ID: {}", idInscripcion, e);
            redirectAttributes.addFlashAttribute("mensajeError", 
                "❌ Error inesperado. Por favor, contacta al administrador.");
        }

        return "redirect:/mis-inscripciones";
    }


   
    private ModalidadData extractModalidadDetails(
            String jsonModalidades,
            String idModalidad
    ) throws com.fasterxml.jackson.core.JsonProcessingException {

        if (jsonModalidades == null || jsonModalidades.isBlank()) {
            return null;
        }

        JsonNode rootNode = objectMapper.readTree(jsonModalidades);
        JsonNode modalidadNode = rootNode.get(idModalidad);

        if (modalidadNode == null) {
            return null;
        }

        ModalidadData modalidad =
                    objectMapper.treeToValue(modalidadNode, ModalidadData.class);

        modalidad.setIdModalidad(idModalidad);
        return modalidad;
    }
    
    
}