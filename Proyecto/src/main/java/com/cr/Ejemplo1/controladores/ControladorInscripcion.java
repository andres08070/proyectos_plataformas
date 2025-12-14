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
import java.util.ArrayList; // Importación necesaria
import java.util.LinkedHashMap; // Importación necesaria
import java.util.List; // Importación necesaria
import java.util.Map; // Importación necesaria
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

    // Métodos auxiliares (se mantienen al final de la clase)

    @GetMapping("/inscripciones/{id}")
public String mostrarDetalleCampeonato(
        @PathVariable("id") Long id,
        @RequestParam(value = "showInscribed", defaultValue = "false") boolean showInscribed,
        @RequestParam(value = "view", required = false) String view,
        Model model,
        HttpSession session,
        HttpServletResponse response
) {

        // 🔒 PREVENIR CACHE
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // ✅ VERIFICAR SESIÓN
        Object idSesion = session.getAttribute("id");
        if (idSesion == null) {
            return "redirect:/auth/inicioSesion";
        }

        Long idUsuario = Long.valueOf(idSesion.toString());

        Campeonato campeonato = null;
        List<Map<String, String>> modalidadesProcesadas = new ArrayList<>();

        // 🔧 CONSULTA DEL CAMPEONATO (Sin cambios)
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

        // 🔧 CONSULTA MODALIDADES YA INSCRITAS (Sin cambios)
        String sqlModalidadesInscritas = """
            SELECT id_modalidad
            FROM campeonatos_inscripcion
            WHERE id_campeonato = ? AND id_usuario = ?
        """;

        try (Connection con = BD.conexion();
                PreparedStatement stmt = con.prepareStatement(sqlSelect)) {

            // ===========================
            // 1️⃣ Modalidades ya inscritas (Sin cambios)
            // ===========================
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

            // ===========================
            // 2️⃣ Obtener campeonato (Sin cambios)
            // ===========================
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

                    // =============================================
                    // 3️⃣ Procesar modalidades (Lógica Condicional)
                    // =============================================
                    rootNode.fields().forEachRemaining(entry -> {
                        String idModalidad = entry.getKey();
                        boolean estaInscrito = modalidadesInscritas.contains(idModalidad);

                        // Lógica de Filtro: 
                        // Si showInscribed es TRUE, sólo se procesan las inscritas.
                        // Si showInscribed es FALSE (o no se pasó), sólo se procesan las NO inscritas.
                        if (showInscribed) {
                            // Modo: Mostrar INSCRITAS
                            if (!estaInscrito) {
                                return; // Saltar (No está inscrito)
                            }
                        } else {
                            // Modo: Mostrar DISPONIBLES (Original)
                            if (estaInscrito) {
                                return; // Saltar (Ya está inscrito)
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
            // Asegúrate de tener 'logger' definido o usa System.err.println para depuración
            // logger.error("❌ Error al cargar inscripciones", e); 
            model.addAttribute("errorMsg", "Error al cargar las inscripciones.");
            return "error-page";
        }

        if ("readonly".equals(view)) {
            return "campeonato/manage/detallescreado";
        }
        // 👉 Vista normal (con inscripción)
        return "campeonato/manage/inscripciones";
    }



    /**
     * Función auxiliar para obtener y limpiar valores de cadena (String) de un JsonNode.
     * Retorna una cadena vacía si el campo es nulo o está vacío.
     */
    private String getStringValue(JsonNode parentNode, String fieldName) {
        if (parentNode.has(fieldName) && !parentNode.get(fieldName).isNull()) {
            String value = parentNode.get(fieldName).asText();
            return (value != null && !value.trim().isEmpty()) ? value.trim() : "";
        }
        return "";
    }

    /**
     * Función auxiliar para obtener valores de array de un JsonNode solo si el array tiene elementos.
     * Retorna una cadena vacía si el array no existe o está vacío.
     */
    private String getArrayValue(JsonNode parentNode, String fieldName) {
        if (parentNode.has(fieldName) && parentNode.get(fieldName).isArray()) {

            ArrayNode arrayNode = (ArrayNode) parentNode.get(fieldName);
            List<String> valores = new ArrayList<>();

            for (JsonNode item : arrayNode) {
                valores.add(item.asText());
            }

            // Une los valores con coma y espacio
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
        logger.error("❌ Error al guardar la inscripción", e);
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

        // CONSULTA: Obtener todas las inscripciones del usuario, con datos del campeonato y la modalidad
        String sql = """
            SELECT
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

        // Usaremos un Map para agrupar por campeonato
        Map<Long, Map<String, Object>> campeonatosMap = new LinkedHashMap<>();

        try (
            Connection con = BD.conexion();
            PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setLong(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long idCampeonato = rs.getLong("id_campeonato");
                    
                    // =======================================================
                    // 1. CREAR EL CAMPEONATO (Solo si no existe en el Map)
                    // =======================================================
                    if (!campeonatosMap.containsKey(idCampeonato)) {
                        Map<String, Object> campeonatoData = new LinkedHashMap<>();
                        campeonatoData.put("idCampeonato", idCampeonato);
                        campeonatoData.put("nombreCampeonato", rs.getString("nombre_campeonato"));
                        campeonatoData.put("ubicacion", rs.getString("ubicacion"));
                        campeonatoData.put("fechaInicio", rs.getDate("fecha_inicio"));
                        campeonatoData.put("fechaFin", rs.getDate("fecha_fin"));
                        campeonatoData.put("creadorNombre", rs.getString("creador_nombre"));
                        
                        // NOTA: 'estado', 'rol', 'nombreParticipante', 'categoriaUsuario'
                        // se repiten, pero si se necesitan una vez en la vista, se usan los de la primera fila.
                        campeonatoData.put("nombreParticipante", rs.getString("nombre_participante"));
                        campeonatoData.put("categoriaUsuario", rs.getString("categoria_usuario"));
                        
                        campeonatoData.put("modalidadesInscritas", new ArrayList<Map<String, Object>>()); // Lista para modalidades
                        
                        campeonatosMap.put(idCampeonato, campeonatoData);
                    }

                    // =======================================================
                    // 2. PROCESAR LA MODALIDAD Y SU ESTADO
                    // =======================================================
                    
                    String idModalidad = rs.getString("id_modalidad");
                    String jsonModalidades = rs.getString("json_modalidades");
                    String estadoInscripcion = rs.getString("estado_inscripcion"); // <-- EL ESTADO DE ESTA MODALIDAD
                    
                    ModalidadData modalidad = extractModalidadDetails(jsonModalidades, idModalidad);
                    
                    if (modalidad != null) {
                        // Crear un Map para la modalidad, incluyendo su estado
                        Map<String, Object> modalidadDetalle = new LinkedHashMap<>();
                        modalidadDetalle.put("idModalidad", modalidad.getIdModalidad());
                        modalidadDetalle.put("nombreModalidad", modalidad.getName());
                        modalidadDetalle.put("descripcionModalidad", modalidad.getDesc());
                        modalidadDetalle.put("estadoInscripcion", estadoInscripcion); // <-- AQUÍ VA EL ESTADO
                        // Opcional: Agregar otros detalles de la modalidad si la vista los necesita (peso, rango, etc.)
                        
                        // Agregar el Map de modalidad a la lista del campeonato
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

        // Convertir el mapa a lista para la vista
        List<Map<String, Object>> inscripcionesAgrupadas = new ArrayList<>(campeonatosMap.values());
        
        System.out.println("📦 TOTAL CAMPEONATOS INSCRITOS: " + inscripcionesAgrupadas.size());

        model.addAttribute("inscripciones", inscripcionesAgrupadas);
        return "campeonato/manage/mis-inscripciones";
    }


    // =====================================================================
    // MÉTODO AUXILIAR - EXTRAER MODALIDAD (Sin cambios, es correcto)
    // =====================================================================
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
    
    // ... (Otros métodos auxiliares si existen) ...
}