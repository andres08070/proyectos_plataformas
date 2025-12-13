package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD; 
import com.cr.Ejemplo1.modelo.Campeonato;
import com.fasterxml.jackson.databind.ObjectMapper; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ControladorInscripcion {

    // Instancia única de Jackson para mejor rendimiento
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/inscripciones/{id}")
    public String verInscripciones(@PathVariable("id") Long id, Model model) {

        Campeonato campeonato = null;
        
        // 1. INICIALIZACIÓN "FINAL": 
        // Creamos las listas aquí y NO las volvemos a asignar con "=".
        // Usaremos .add() o .addAll() para modificarlas.
        List<String> listaModalidades = new ArrayList<>();
        List<String> listaCinturones = new ArrayList<>();

        // SQL: Añadimos 'descripcion' porque está en tu modelo
        String sql = "SELECT id, nombre, descripcion, json_modalidades FROM campeonato WHERE id = ?";

        try (Connection con = BD.conexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    campeonato = new Campeonato();
                    campeonato.setId(rs.getLong("id"));
                    campeonato.setNombre(rs.getString("nombre"));
                    campeonato.setDescripcion(rs.getString("descripcion")); // Nuevo campo
                    
                    String jsonTexto = rs.getString("json_modalidades");
                    campeonato.setJsonModalidades(jsonTexto);

                    // 2. LÓGICA ROBUSTA DE PARSEO JSON
                    if (jsonTexto != null && !jsonTexto.trim().isEmpty()) {
                        try {
                            String jsonLimpio = jsonTexto.trim();
                            
                            if (jsonLimpio.startsWith("[")) {
                                // CASO A: Es una lista ["A", "B"]
                                String[] array = objectMapper.readValue(jsonLimpio, String[].class);
                                if (array != null) {
                                    // Usamos addAll para no romper la regla "effectively final"
                                    listaModalidades.addAll(Arrays.asList(array));
                                }
                                
                            } else if (jsonLimpio.startsWith("{")) {
                                // CASO B: Es un objeto {"0":"A"}
                                com.fasterxml.jackson.databind.JsonNode nodo = objectMapper.readTree(jsonLimpio);
                                // Iteramos el nodo y agregamos a la lista existente
                                nodo.forEach(item -> listaModalidades.add(item.asText()));
                                
                            } else {
                                // CASO C: Texto plano sin formato JSON
                                listaModalidades.add(jsonLimpio);
                            }
                        } catch (Exception e) {
                            System.err.println("Error recuperando modalidades: " + e.getMessage());
                            listaModalidades.add("Modalidad General"); // Fallback
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "error-page";
        }

        // Si no se encontró el campeonato, redirigir
        if (campeonato == null) {
            return "redirect:/campeonato/lista";
        }

        // 3. GENERAR LISTA DE CINTURONES (Estática)
        listaCinturones = Arrays.asList(
            "Blanco", "Amarillo", "Naranja", "Verde", 
            "Azul", "Marrón", "Negro"
        );

        // 4. PASAR DATOS A LA VISTA
        model.addAttribute("campeonato", campeonato);
        
        // Validación final visual (si quedó vacía, mostramos algo genérico)
        if (listaModalidades.isEmpty()) {
            listaModalidades.add("Competición General");
        }
        
        model.addAttribute("listaModalidades", listaModalidades);
        model.addAttribute("listaCinturones", listaCinturones);

        return "campeonato/manage/Inscripciones"; 
    }
}