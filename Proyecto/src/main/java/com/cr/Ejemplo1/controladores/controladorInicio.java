/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cr.Ejemplo1.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author andre
 */
@Controller 
public class controladorInicio {
    
    @GetMapping("/")
    public String inicio() {
        // CAMBIO IMPORTANTE:
        // Antes buscaba en templates/index.html
        // Ahora busca en templates/public/index.html
        return "public/index";
    }
}