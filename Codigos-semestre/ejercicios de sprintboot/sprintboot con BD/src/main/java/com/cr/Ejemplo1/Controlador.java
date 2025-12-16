/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cr.Ejemplo1;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller 
public class Controlador {
    ArrayList<estudiante> est = new ArrayList<>();
    
    //Metodos para acceder a la BD
    public String insertarEstudiante(int ID_estudiante, String Nombre, int Edad) {

    String verificarSQL = "SELECT COUNT(*) FROM estudiantes WHERE ID_estudiante = ?";
    String insertarSQL = "INSERT INTO estudiantes (ID_estudiante, Nombre, Edad) VALUES (?, ?, ?)";

    try (Connection con = BD.conexion();
         PreparedStatement verificar = con.prepareStatement(verificarSQL)) {

        // Verificar si el ID ya existe
        verificar.setInt(1, ID_estudiante);
        var rs = verificar.executeQuery();

        if (rs.next() && rs.getInt(1) > 0) {
            return "ERROR: ID ya existe";
        }

        // Insertar si NO existe
        try (PreparedStatement insertar = con.prepareStatement(insertarSQL)) {

            insertar.setInt(1, ID_estudiante);
            insertar.setString(2, Nombre);
            insertar.setInt(3, Edad);

            insertar.executeUpdate();
            return "Estudiante insertado correctamente";
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return "ERROR en BD";
    }
}
    public String actualizarEstudiante(int ID_estudiante, String nombre, int edad) {
    String sql = "UPDATE estudiantes SET nombre = ?, edad = ? WHERE ID_estudiante = ?";

    try (Connection con = BD.conexion();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, nombre);
        ps.setInt(2, edad);
        ps.setInt(3, ID_estudiante);

        int filas = ps.executeUpdate();

        if (filas > 0) {
            return "Actualizado correctamente";
        } else {
            return "ERROR: ID no encontrado";
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return "ERROR en la base de datos";
    }
}
    public ArrayList<estudiante> listarEstudiantes() {
    ArrayList<estudiante> lista = new ArrayList<>();

    String sql = "SELECT * FROM estudiantes";

    try (Connection con = BD.conexion();
         PreparedStatement ps = con.prepareStatement(sql);
         var rs = ps.executeQuery()) {

        while (rs.next()) {
            estudiante e = new estudiante(
                rs.getInt("ID_estudiante"),
                rs.getString("Nombre"),
                rs.getInt("Edad")
            );
            lista.add(e);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return lista;
}
    public String eliminarEstudianteBD(int ID_estudiante) {
        String sql = "DELETE FROM estudiantes WHERE ID_estudiante = ?";

        try (Connection con = BD.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ID_estudiante);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                return "Estudiante eliminado correctamente";
            } else {
                return "ERROR: ID no encontrado";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR al eliminar";
        }
    }

    //Metodos para mostrar los html i
    @GetMapping("/")
    public String mostrarFormulario(){
        return "index";
    }

    @GetMapping("/ingresar")
    public String mostraringresar() {
        return "ingresar";
    }
    
    @GetMapping("/mostrarlista")
    public String mostrarLista(Model model) {
        est=listarEstudiantes();
        model.addAttribute("lista", est);
        return "listacompleta";
    }
    
    @GetMapping("/buscarIp")
    public String mostrarBuscar() {
        return "buscarIp";
    }
    
    @GetMapping("/ActualizarDatos")
    public String mostrarActualizar() {
        return "ActualizarDatos";
    }
    @GetMapping("/eliminar")
    public String mostrarEliminar() {
        return "eliminar";
    }

    @PostMapping("/añadir")
    public String añadir(@RequestParam int id,@RequestParam String nombre,@RequestParam int edad,Model model) {
        
        for (estudiante e : est) {
            if (e.getId() == id) {
                model.addAttribute("mensaje", "ID repetido!!!!");
                return "ingresar";
            }
        }
        insertarEstudiante(id, nombre, edad);
        model.addAttribute("mensaje", "Estudiante agregado correctamente");
        System.out.println(est);

        return "ingresar";
    }


    @GetMapping("/buscar")
    public String buscar(@RequestParam int id, Model model) {

        for (estudiante e : est) {
            if (e.getId()==id) {
                model.addAttribute("resultado", e);
                return "buscarIp";
            }
        }

        model.addAttribute("mensaje", "No se encontró el estudiante");
        return "buscarIp";
    }

    

    @PutMapping("/actualizar")
    public String actualizarEstudianteController(@RequestParam("id") int id,@RequestParam("nombre") String nombre,@RequestParam("edad") int edad,Model model) {

        String mensaje = actualizarEstudiante(id, nombre, edad);
        model.addAttribute("mensaje", mensaje);

        return "actualizarDatos";
    }


    @DeleteMapping("/eliminar")
    public String eliminarEstudiante(@RequestParam("id") int id, Model model) {

        String mensaje = eliminarEstudianteBD(id);
        model.addAttribute("mensaje", mensaje);
        return "eliminar";
    }




}



