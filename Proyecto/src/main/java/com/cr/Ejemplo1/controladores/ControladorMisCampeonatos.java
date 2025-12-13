package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
import com.cr.Ejemplo1.modelo.Campeonato;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Controller
public class ControladorMisCampeonatos {

    // Utilidad para convertir el JSON de la BD a listas Java
    private final ObjectMapper objectMapper = new ObjectMapper();

    // =========================================================================
    // DTOs INTERNOS (Datos simulados temporales para la vista de detalle)
    // Estos se borrarán cuando tengas las tablas reales de 'categoria' y 'inscripcion'
    // =========================================================================
    public record CompetidorDTO(String nombre, int edad, String club) {}
    public record CategoriaDTO(String nombre, List<CompetidorDTO> competidores, CompetidorDTO oro, CompetidorDTO plata, CompetidorDTO bronce) {}


    // =========================================================================
    // MÉTODO 1: VER LA LISTA DE MIS CAMPEONATOS (AGENDA)
    // Ruta: /mis-campeonatos
    // =========================================================================
    @GetMapping("/mis-campeonatos")
    public String listarMisCampeonatos(HttpSession session, Model model) {
        
        // 1. Validar sesión
        String idUsuarioStr = (String) session.getAttribute("id");
        if (idUsuarioStr == null) {
            return "redirect:/login";
        }
        int idUsuario = Integer.parseInt(idUsuarioStr);

        List<Campeonato> misCampeonatos = new ArrayList<>();
        
        // 2. SQL: Traer campeonatos donde el usuario es el ADMINISTRADOR (Creador)
        // (A futuro aquí añadirás "OR id IN (SELECT id_camp FROM inscripciones WHERE id_user = ...)")
        String sql = "SELECT id, nombre, fecha_inicio, fecha_fin, ubicacion, descripcion, num_areas " +
                     "FROM campeonato WHERE id_admin = ?";

        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Campeonato c = new Campeonato();
                    c.setId(rs.getLong("id"));
                    c.setNombre(rs.getString("nombre"));
                    c.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                    c.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                    c.setUbicacion(rs.getString("ubicacion"));
                    c.setDescripcion(rs.getString("descripcion"));
                    c.setNumAreas(rs.getInt("num_areas"));
                    
                    misCampeonatos.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error cargando la agenda.");
        }

        model.addAttribute("misCampeonatos", misCampeonatos);
        
        // Retorna la vista de LISTA PRIVADA
        return "campeonato/manage/lista-mis-campeonatos"; 
    }


    // =========================================================================
    // MÉTODO 2: VER DETALLE Y GESTIÓN DE UN CAMPEONATO ESPECÍFICO
    // Ruta: /mis-campeonatos/{id}
    // =========================================================================
    @GetMapping("/mis-campeonatos/{id}")
    public String verDetalleCampeonato(@PathVariable("id") Long idCamp, HttpSession session, Model model) {
        
        // 1. Validar sesión
        String idUsuarioStr = (String) session.getAttribute("id");
        if (idUsuarioStr == null) return "redirect:/login";
        int idUsuario = Integer.parseInt(idUsuarioStr);

        Campeonato campeonato = null;
        List<String> listaModalidades = new ArrayList<>();
        String rolUsuario = "ESPECTADOR"; // Valores posibles: CREADOR, JUEZ, COMPETIDOR

        String sql = "SELECT id, nombre, descripcion, json_modalidades, id_admin FROM campeonato WHERE id = ?";

        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setLong(1, idCamp);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    campeonato = new Campeonato();
                    campeonato.setId(rs.getLong("id"));
                    campeonato.setNombre(rs.getString("nombre"));
                    campeonato.setDescripcion(rs.getString("descripcion"));
                    campeonato.setJsonModalidades(rs.getString("json_modalidades"));
                    
                    int idAdmin = rs.getInt("id_admin");

                    // 2. DETERMINAR ROL
                    if (idAdmin == idUsuario) {
                        rolUsuario = "CREADOR";
                    }
                    // TODO: Consultar tabla 'jueces' o 'inscripciones' para ver si tiene otro rol

                    // 3. PROCESAR MODALIDADES (JSON a Lista)
                    String jsonTexto = campeonato.getJsonModalidades();
                    if (jsonTexto != null && !jsonTexto.trim().isEmpty()) {
                        try {
                            String jsonLimpio = jsonTexto.trim();
                            if (jsonLimpio.startsWith("[")) {
                                String[] array = objectMapper.readValue(jsonLimpio, String[].class);
                                if(array != null) listaModalidades.addAll(Arrays.asList(array));
                            } else if (jsonLimpio.startsWith("{")) {
                                com.fasterxml.jackson.databind.JsonNode nodo = objectMapper.readTree(jsonLimpio);
                                nodo.forEach(item -> listaModalidades.add(item.asText()));
                            } else {
                                listaModalidades.add(jsonLimpio);
                            }
                        } catch (Exception e) { 
                            listaModalidades.add("General"); 
                        }
                    }
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            return "error-page"; 
        }

        if (campeonato == null) return "redirect:/mis-campeonatos";

        // ==========================================================================================
        // MOCK DATA (DATOS SIMULADOS) - Generamos datos falsos para que la vista no esté vacía
        // ==========================================================================================
        Map<String, List<CategoriaDTO>> mapaCategorias = new LinkedHashMap<>();

        // Si no hay modalidades, agregamos una por defecto
        if(listaModalidades.isEmpty()) listaModalidades.add("General");

        for (String modalidad : listaModalidades) {
            List<CategoriaDTO> categorias = new ArrayList<>();

            // Competidores ficticios
            CompetidorDTO c1 = new CompetidorDTO("Juan Pérez", 22, "Dojo Cobra");
            CompetidorDTO c2 = new CompetidorDTO("Maria Rodriguez", 25, "Team Eagle");
            CompetidorDTO c3 = new CompetidorDTO("Carlos Ruiz", 23, "Dojo Cobra");

            // Simulamos lógica de categorías según la modalidad
            if(modalidad.contains("Kumite") || modalidad.contains("Combate")) {
                // Categoría con resultados (Podio completo)
                categorias.add(new CategoriaDTO("Masculino Avanzado -75kg", List.of(c1, c3), c1, c3, null));
                // Categoría sin resultados aún
                categorias.add(new CategoriaDTO("Femenino Intermedio -60kg", List.of(c2), null, null, null));
            } else {
                // Categoría de Formas/Katas
                categorias.add(new CategoriaDTO("Formas Mixto Principiante", List.of(c1, c2, c3), null, null, null));
            }
            
            mapaCategorias.put(modalidad, categorias);
        }
        // ==========================================================================================

        // 4. PASAR DATOS AL MODELO (HTML)
        model.addAttribute("campeonato", campeonato);
        model.addAttribute("listaModalidades", listaModalidades); // Títulos de pestañas
        model.addAttribute("mapaCategorias", mapaCategorias);     // Contenido de pestañas
        model.addAttribute("rolUsuario", rolUsuario);             // Para permisos de botones

        // Retorna la vista de DETALLE / GESTIÓN
        return "campeonato/manage/detalle-mis-campeonatos";
    }
}