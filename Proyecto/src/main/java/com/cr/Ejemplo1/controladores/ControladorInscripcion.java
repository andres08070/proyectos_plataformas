package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
import com.cr.Ejemplo1.modelo.Campeonato;
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

@Controller
public class ControladorInscripcion {

    private static final Logger logger = LoggerFactory.getLogger(ControladorInscripcion.class);
    private final ObjectMapper objectMapper = new ObjectMapper(); 

    // Métodos auxiliares (se mantienen al final de la clase)

    @GetMapping("/inscripciones/{id}")
    public String mostrarDetalleCampeonato(@PathVariable("id") Long id, Model model,HttpSession session) {

        Campeonato campeonato = null;
        List<Map<String, String>> modalidadesProcesadas = new ArrayList<>();

        // 1️⃣ Obtener id del usuario desde sesión
        String idUsuario = (String) session.getAttribute("id");
        if (idUsuario == null) {
            return "redirect:/login";
        }

        String sqlSelect = "SELECT c.id, c.nombre, c.ubicacion, c.json_modalidades, " +
                           "u.nombreC AS nombre_creador " +
                           "FROM campeonato c " +
                           "INNER JOIN usuarios u ON c.id_admin = u.ID_documento " +
                           "WHERE c.id = ?";

        String sqlModalidadesInscritas =
            "SELECT id_modalidad FROM campeonatos_inscripcion " +
            "WHERE id_campeonato = ? AND id_usuario = ?";

        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sqlSelect)) {

            // ===========================
            // 2️⃣ Modalidades ya inscritas
            // ===========================
            Set<String> modalidadesInscritas = new HashSet<>();

            try (PreparedStatement ps = con.prepareStatement(sqlModalidadesInscritas)) {
                ps.setLong(1, id);
                ps.setString(2, idUsuario);

                ResultSet rsModalidades = ps.executeQuery();
                while (rsModalidades.next()) {
                    modalidadesInscritas.add(rsModalidades.getString("id_modalidad"));
                }
            }

            // ===========================
            // 3️⃣ Obtener campeonato
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

                String jsonModalidades = rs.getString("json_modalidades");

                if (jsonModalidades != null && !jsonModalidades.trim().isEmpty()) {

                    JsonNode rootNode = objectMapper.readTree(jsonModalidades);

                    // ===========================
                    // 4️⃣ Procesar JSON filtrando
                    // ===========================
                    rootNode.fields().forEachRemaining(entry -> {
                        String idModalidad = entry.getKey();

                        // ❌ Ya inscrito → no mostrar
                        if (modalidadesInscritas.contains(idModalidad)) {
                            return;
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
            logger.error("Error al cargar inscripciones:", e);
            model.addAttribute("errorMsg", "Error al cargar las inscripciones.");
            return "error-page";
        }

        return "campeonato/manage/Inscripciones";
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
            if (arrayNode.size() > 0) {
                // Devolvemos el array como una cadena JSON para una fácil visualización
                return arrayNode.toString(); 
            }
        }
        return "";
    }
    @PostMapping("/inscripciones/inscribir")
    public String inscribirUsuarioModalidad(
            @RequestParam("idCampeonato") Long idCampeonato,
            @RequestParam("idModalidad") String idModalidad,
            jakarta.servlet.http.HttpSession session) {


        Object idSesion = session.getAttribute("id");
        Long idUsuario = Long.valueOf(idSesion.toString());

        System.out.println("ID Campeonato: " + idCampeonato);
        System.out.println("ID Usuario sesión: " + idUsuario);
        System.out.println("ID Modalidad: " + idModalidad);

        if (idCampeonato == null || idUsuario == null || idModalidad == null || idModalidad.isEmpty()) {
            logger.error("Datos inválidos para inscripción");
            return "redirect:/error";
        }

        String sqlInsert = """
            INSERT INTO campeonatos_inscripcion 
            (id_campeonato, id_usuario, id_modalidad)
            VALUES (?, ?, ?)
        """;

        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sqlInsert)) {

            stmt.setLong(1, idCampeonato);
            stmt.setLong(2, idUsuario);
            stmt.setString(3, idModalidad);

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error al guardar la inscripción:", e);
        }

        return "redirect:/inscripciones/" + idCampeonato;
    }


}