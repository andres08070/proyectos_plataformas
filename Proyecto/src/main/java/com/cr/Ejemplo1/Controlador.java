/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cr.Ejemplo1;


import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller 
public class Controlador {

    @GetMapping("/")
    public String mostrarFormulario(){
        return "registro";
    }
    @Autowired
    private EmailService emailService;

    @Autowired
    private HttpSession session;

    // Generar código aleatorio
    private String generarCodigo() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam int NumeroDocumento, 
                            @RequestParam String nombre, 
                            @RequestParam String sexo, 
                            @RequestParam String Nacionalidad, 
                            @RequestParam String correo,
                            @RequestParam String contraseña,
                            @RequestParam String Confirmar,Model model) {

        
        // Generar código de verificación
        String codigo = generarCodigo();

        // Guardar datos en sesión temporal
        session.setAttribute("nombre", nombre);
        session.setAttribute("correo", correo);
        session.setAttribute("codigo", codigo);

        // Enviar correo
        emailService.enviarCodigo(correo, codigo);

        // Enviar mensaje a una página donde se ingresa el código
        
        if (contraseña.equals(Confirmar)){
            System.out.println("bien");
            model.addAttribute("msg", "Se envió un código a: " + correo);
            return "verificar";
        }
        model.addAttribute("msg","Contraseña incorrecta");
        return "registro";
    }
    @PostMapping("/verificar")
    public String verificarCodigo(@RequestParam String codigoIngresado, Model model) {

        String codigoCorrecto = (String) session.getAttribute("codigo");

        if (codigoIngresado.equals(codigoCorrecto)) {
            model.addAttribute("msg", "Registro verificado exitosamente.");
            return "verificar";
        }

        model.addAttribute("msg", "Código incorrecto.");
        return "verificar";
    }

}



