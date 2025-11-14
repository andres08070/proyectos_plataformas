/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cr.Ejemplo1;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Controlador {

    @GetMapping("/")
    public String mostrarFormulario() {
        return "index"; // carga el template index.html
    }

    @PostMapping("/palindromo")
    public String verificarPalindromo(@RequestParam("palabra") String palabra, Model model) {
        String limpio = palabra.replaceAll("\\s+", "").toLowerCase();
        String invertido = new StringBuilder(limpio).reverse().toString();
        boolean esPalindromo = limpio.equals(invertido);

        model.addAttribute("palabra", palabra);
        model.addAttribute("resultado", esPalindromo ? "Sí es palíndroma" : "No es palíndroma");

        return "resultado"; 
    }
}

