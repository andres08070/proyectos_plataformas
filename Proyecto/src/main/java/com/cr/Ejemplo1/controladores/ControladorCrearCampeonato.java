package com.cr.Ejemplo1.controladores;

// Importaciones para JDBC
import com.cr.Ejemplo1.BD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cr.Ejemplo1.modelo.Campeonato;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;


@Controller
public class ControladorCrearCampeonato {

    private static final Logger logger = LoggerFactory.getLogger(ControladorCrearCampeonato.class);
    
    // Objeto para deserializar el JSON, inicializado una vez
    private final ObjectMapper objectMapper = new ObjectMapper(); 
    
    // ... (Método guardarCampeonato - POST - sin cambios) ...

    @PostMapping("/guardar-campeonato")
    public String guardarCampeonato(@ModelAttribute Campeonato campeonato) {
        
        logger.info("Iniciando guardado del Campeonato: {}", campeonato.getNombre());
        
        // **NOTA: La descripción también fue eliminada de aquí para la inserción**
        String sqlInsert = "INSERT INTO campeonato (nombre, fecha_inicio, fecha_fin, ubicacion, num_areas, json_modalidades) " +
                           "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = BD.conexion();
             PreparedStatement insertar = con.prepareStatement(sqlInsert)) {
            
            insertar.setString(1, campeonato.getNombre());
            // insertar.setString(2, campeonato.getDescripcion()); <-- ELIMINADO
            
            Date sqlFechaInicio = Date.valueOf(campeonato.getFechaInicio());
            Date sqlFechaFin = Date.valueOf(campeonato.getFechaFin());
            
            insertar.setDate(2, sqlFechaInicio); // El índice cambia
            insertar.setDate(3, sqlFechaFin);    // El índice cambia
            
            insertar.setString(4, campeonato.getUbicacion()); // El índice cambia
            insertar.setInt(5, campeonato.getNumAreas());    // El índice cambia
            
            insertar.setString(6, campeonato.getJsonModalidades()); // El índice cambia
            
            int filasAfectadas = insertar.executeUpdate();
            
            if (filasAfectadas > 0) {
                 logger.info("Campeonato '{}' guardado exitosamente.", campeonato.getNombre());
                 return "redirect:/inicio";
            } else {
                 logger.error("El guardado del Campeonato '{}' falló (0 filas afectadas).", campeonato.getNombre());
                 return "error-page";
            }
            
        } catch (SQLException e) {
            logger.error("ERROR CRÍTICO al intentar guardar el Campeonato en la BD:", e);
            e.printStackTrace();
            return "error-page";
        } catch (Exception e) {
            logger.error("Error inesperado:", e);
            return "error-page";
        }
    }


    @GetMapping("/campeonato/lista") 
    public String mostrarCampeonatos(Model model) { // <-- Aceptar el objeto Model
        
        // La consulta SQL solo obtiene los campos que se mostrarán en la lista.
        String sqlSelect = "SELECT id, nombre, fecha_inicio, fecha_fin, ubicacion FROM campeonato";
        
        List<Campeonato> listaCampeonatos = new ArrayList<>();

        System.out.println("\n--- Recopilando Campeonatos de la BD para la vista ---");
        
        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sqlSelect);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                
                // Creamos un objeto Campeonato simple (solo con los datos que necesitamos)
                Campeonato camp = new Campeonato();
                camp.setId(rs.getLong("id")); // Asumo que tienes el setter getId/setId
                camp.setNombre(rs.getString("nombre"));
                
                // Conversión de fechas a LocalDate
                camp.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate()); 
                camp.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                
                camp.setUbicacion(rs.getString("ubicacion"));
                
                listaCampeonatos.add(camp);
            }

        } catch (SQLException e) {
            logger.error("Error al obtener la lista de campeonatos de la BD:", e);
            e.printStackTrace();
            model.addAttribute("errorMsg", "Hubo un error al cargar la lista de campeonatos.");
            return "error-page";
        }
        
        // 2. Adjuntar la lista al modelo de Spring
        model.addAttribute("campeonatos", listaCampeonatos);
        
        // 3. Devolver el nombre de la nueva plantilla HTML
        return "campeonato/lista-campeonatos"; 
    }

}