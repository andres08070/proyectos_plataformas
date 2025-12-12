package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControladorCrearCampeonato {

    @Autowired
    private HttpSession session;

    @PostMapping("/guardar-campeonato")
    public String guardarCampeonato(
            @RequestParam("nombre") String nombre,
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin,
            @RequestParam("ubicacion") String ubicacion,

            @RequestParam(value = "modalidades", required = false) List<String> modalidades,

            @RequestParam(value = "combate_pesos_data", required = false) String combatePesos,
            @RequestParam(value = "combate_rangos_data", required = false) String combateRangos,
            @RequestParam(value = "combate_edades_data", required = false) String combateEdades,
            @RequestParam(value = "combate_genero_data", required = false) String combateGenero,

            @RequestParam(value = "figuras_pesos_data", required = false) String figurasPesos,
            @RequestParam(value = "figuras_rangos_data", required = false) String figurasRangos,
            @RequestParam(value = "figuras_edades_data", required = false) String figurasEdades,
            @RequestParam(value = "figuras_genero_data", required = false) String figurasGenero,

            @RequestParam(value = "defensa_pesos_data", required = false) String defensaPesos,
            @RequestParam(value = "defensa_rangos_data", required = false) String defensaRangos,
            @RequestParam(value = "defensa_edades_data", required = false) String defensaEdades,
            @RequestParam(value = "defensa_genero_data", required = false) String defensaGenero,

            @RequestParam(value = "demo_pesos_data", required = false) String demoPesos,
            @RequestParam(value = "demo_rangos_data", required = false) String demoRangos,
            @RequestParam(value = "demo_edades_data", required = false) String demoEdades,
            @RequestParam(value = "demo_genero_data", required = false) String demoGenero,

            @RequestParam("numAreas") int numAreas,
            Model model
    ) {

        // Convertir lista en String
        String modalidadesString = (modalidades != null)
                ? String.join(",", modalidades)
                : null;

        // SQL FINAL
        String sql = "INSERT INTO campeonato (" +
                "nombre, fechaInicio, fechaFin, ubicacion, modalidades, " +
                "combate_pesos_data, combate_rangos_data, combate_edades_data, combate_genero_data, " +
                "figuras_pesos_data, figuras_rangos_data, figuras_edades_data, figuras_genero_data, " +
                "defensa_pesos_data, defensa_rangos_data, defensa_edades_data, defensa_genero_data, " +
                "demo_pesos_data, demo_rangos_data, demo_edades_data, demo_genero_data, " +
                "numAreas) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = BD.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, fechaInicio);
            ps.setString(3, fechaFin);
            ps.setString(4, ubicacion);
            ps.setString(5, modalidadesString);

            // COMBATE
            ps.setString(6, combatePesos);
            ps.setString(7, combateRangos);
            ps.setString(8, combateEdades);
            ps.setString(9, combateGenero);

            // FIGURAS
            ps.setString(10, figurasPesos);
            ps.setString(11, figurasRangos);
            ps.setString(12, figurasEdades);
            ps.setString(13, figurasGenero);

            // DEFENSA
            ps.setString(14, defensaPesos);
            ps.setString(15, defensaRangos);
            ps.setString(16, defensaEdades);
            ps.setString(17, defensaGenero);

            // DEMO
            ps.setString(18, demoPesos);
            ps.setString(19, demoRangos);
            ps.setString(20, demoEdades);
            ps.setString(21, demoGenero);

            // TATAMIS
            ps.setInt(22, numAreas);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("msg", "Error al guardar datos del campeonato");
            return "errorBD"; // si quieres cambiarlo, me dices
        }

        model.addAttribute("msg", "Campeonato creado exitosamente");
        return "dashboard/inicio";
    }
}
