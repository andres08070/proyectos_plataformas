/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cr.Ejemplo1;

import jakarta.annotation.PostConstruct;
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
    @PostConstruct
    public void iniciarLista() {
        est.add(new estudiante(1, "Carlos", 20));
        est.add(new estudiante(2, "María", 22));
    }
    
    @GetMapping("/")
    public String mostrarFormulario() {
        return "index";
    }

    @GetMapping("/ingresar")
    public String mostraringresar() {
        return "ingresar";
    }
    
    @GetMapping("/mostrarlista")
    public String mostrarLista(Model model) {
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


        est.add(new estudiante(id, nombre, edad));
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

        for (estudiante e : est) {
            if (e.getId() == id) {
                e.setNombre(nombre);
                e.setEdad(edad);

                model.addAttribute("mensaje", "Datos actualizados correctamente");
                return "actualizarDatos";
            }
        }

        model.addAttribute("mensaje", "ERROR: ID no encontrado");
        return "actualizarDatos";
    }


    @DeleteMapping("/eliminar")
    public String eliminarEstudiante(@RequestParam("id") int id, Model model) {

        boolean eliminado = est.removeIf(e -> e.getId() == id);

        if (eliminado) {
            model.addAttribute("mensaje", "Estudiante eliminado correctamente");
        } else {
            model.addAttribute("mensaje", "ERROR: ID no encontrado");
        }

        return "eliminar";
    }



}


