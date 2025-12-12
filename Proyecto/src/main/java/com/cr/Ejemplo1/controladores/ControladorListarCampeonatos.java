package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD; // Tu clase para la conexión a BD
import com.cr.Ejemplo1.modelo.Campeonato;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControladorListarCampeonatos {

    /**
     * 1. Muestra la lista de todos los campeonatos.
     * Mapeado a: /campeonatos/lista
     */
    @GetMapping("/campeonatos/lista")
    public String listarCampeonatos(Model model) {
        
        List<Campeonato> campeonatos = new ArrayList<>();
        // Asegúrate de que tu tabla en BD tenga una columna ID (Ej: 'id_campeonato')
        String sql = "SELECT id, nombre, fechaInicio, ubicacion FROM campeonato ORDER BY fechaInicio DESC";

        try (Connection con = BD.conexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Campeonato camp = new Campeonato(
                    rs.getInt("id"),  // Asume que el nombre de la columna es 'id'
                    rs.getString("nombre"),
                    rs.getString("fechaInicio"),
                    rs.getString("ubicacion")
                );
                campeonatos.add(camp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("msg", "Error al cargar la lista de campeonatos.");
            return "errorBD";
        }

        model.addAttribute("listaCampeonatos", campeonatos);
        return "campeonato/ListaCampeonatos"; // Nombre de la plantilla Thymeleaf
    }

    /**
     * 2. Muestra el formulario para un campeonato específico.
     * Mapeado a: /campeonatos/formulario?id=X
     */
    @GetMapping("/campeonatos/formulario")
    public String mostrarFormularioInscripcion(@RequestParam("id") int idCampeonato, Model model) {
        
        // Opcional: Podrías buscar el nombre completo del campeonato aquí
        String nombreCampeonato = "Campeonato ID " + idCampeonato; 
        
        // Simplemente pasamos el ID y el nombre al modelo para que Thymeleaf los use
        model.addAttribute("idCampeonato", idCampeonato);
        model.addAttribute("nombreCampeonato", nombreCampeonato); 
        
        return "campeonato/formulario-inscripcion"; // Nombre de la plantilla Thymeleaf
    }
}