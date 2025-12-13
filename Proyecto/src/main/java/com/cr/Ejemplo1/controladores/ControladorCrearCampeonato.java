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
import jakarta.servlet.http.HttpSession;
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
    public String guardarCampeonato(@ModelAttribute Campeonato campeonato, HttpSession session) {
        
        // 1. OBTENER Y CONVERTIR EL ID DEL USUARIO DESDE LA SESIÓN (SOLUCIÓN)
        // Se asume que el ID se guardó como String en la sesión, así que lo leemos como String.
        String idCreadorString = (String) session.getAttribute("id");
        Integer idCreador = null;
        
        if (idCreadorString != null) {
            try {
                // Intentamos convertir la cadena de texto a número entero
                idCreador = Integer.parseInt(idCreadorString);
            } catch (NumberFormatException e) {
                logger.error("El valor del ID de sesión '{}' no es un número válido. Posible problema de formato.", idCreadorString, e);
                return "error-page"; // Manejar el error de formato
            }
        } else {
            logger.error("Error: Usuario no logueado o ID no encontrado en la sesión.");
            // Si el ID no está, redirigir al login
            return "redirect:/login"; 
        }

        // --- El resto del código de guardado es el mismo ---
        
        logger.info("Iniciando guardado del Campeonato '{}' por el usuario ID: {}", campeonato.getNombre(), idCreador);
        
        String sqlInsert = "INSERT INTO campeonato (nombre, fecha_inicio, fecha_fin, ubicacion, num_areas, json_modalidades, id_admin) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)"; 

        try (Connection con = BD.conexion();
             PreparedStatement insertar = con.prepareStatement(sqlInsert)) {
            
            // Parámetros 1 a 6
            insertar.setString(1, campeonato.getNombre());
            
            Date sqlFechaInicio = Date.valueOf(campeonato.getFechaInicio());
            Date sqlFechaFin = Date.valueOf(campeonato.getFechaFin());
            
            insertar.setDate(2, sqlFechaInicio);
            insertar.setDate(3, sqlFechaFin);
            
            insertar.setString(4, campeonato.getUbicacion());
            insertar.setInt(5, campeonato.getNumAreas());
            insertar.setString(6, campeonato.getJsonModalidades()); 
            
            // AGREGAR EL ID DEL CREADOR (Parámetro 7)
            insertar.setInt(7, idCreador); // <-- Nuevo parámetro

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
    public String mostrarCampeonatos(Model model) {
        
        // 1. CONSULTA SQL MODIFICADA: Usamos JOIN para obtener el nombre del creador (u.nombreC)
        String sqlSelect = "SELECT c.id, c.nombre, c.fecha_inicio, c.fecha_fin, c.ubicacion, u.nombreC AS nombre_creador " +
                           "FROM campeonato c " +
                           "INNER JOIN usuarios u ON c.id_admin = u.ID_documento";
        
        List<Campeonato> listaCampeonatos = new ArrayList<>();

        System.out.println("\n--- Recopilando Campeonatos de la BD para la vista ---");
        
        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sqlSelect);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                
                // Creamos un objeto Campeonato y leemos todos los datos
                Campeonato camp = new Campeonato();
                camp.setId(rs.getLong("id"));
                camp.setNombre(rs.getString("nombre"));
                
                // Conversión de fechas a LocalDate
                camp.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate()); 
                camp.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                
                camp.setUbicacion(rs.getString("ubicacion"));
                
                // 2. LEER EL NOMBRE DEL CREADOR USANDO EL ALIAS 'nombre_creador'
                camp.setNombreCreador(rs.getString("nombre_creador"));
                
                listaCampeonatos.add(camp);
            }

        } catch (SQLException e) {
            logger.error("Error al obtener la lista de campeonatos de la BD:", e);
            e.printStackTrace();
            model.addAttribute("errorMsg", "Hubo un error al cargar la lista de campeonatos.");
            return "error-page";
        }
        
        // Adjuntar la lista al modelo de Spring
        model.addAttribute("campeonatos", listaCampeonatos);
        
        return "campeonato/lista-campeonatos";
    }

}