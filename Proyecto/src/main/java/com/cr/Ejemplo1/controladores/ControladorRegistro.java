/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cr.Ejemplo1.controladores;

import com.cr.Ejemplo1.BD;
import com.cr.Ejemplo1.EmailService;
import com.cr.Ejemplo1.usuarios;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller 
public class ControladorRegistro {
    
    // CAMBIO IMPORTANTE: Ahora la ruta es "/registro", ya no "/"
    @GetMapping("/registro")
    public String mostrarFormulario(){
        return "registro";
    }

    @Autowired
    private EmailService emailService;

    @Autowired
    private HttpSession session;

    private String generarCodigo() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam int NumeroDocumento, 
                            @RequestParam String nombre, 
                            @RequestParam String sexo, 
                            @RequestParam String Nacionalidad,
                            @RequestParam int edad, 
                            @RequestParam String correo,
                            @RequestParam String contraseña,
                            @RequestParam String Confirmar, Model model) {
        
        // 1. Verificar contraseñas
        if (!contraseña.equals(Confirmar)){
            model.addAttribute("msg","La contraseña no coincide");
            return "registro";
        }

        // 2. SEGURIDAD: INTENTO DE CREAR EL USUARIO (VALIDACIÓN JAVA)
        usuarios usuarioTemporal;
        try {
            usuarioTemporal = new usuarios(NumeroDocumento, nombre, "Sin Rango", edad, contraseña, correo, sexo, Nacionalidad);
        
        } catch (IllegalArgumentException e) {
            model.addAttribute("msg", "Error de validación: " + e.getMessage());
            return "registro"; 
        }

        // 3. Verificar si existe en BD
        String verificarBD = "SELECT COUNT(*) FROM usuarios WHERE ID_documento = ?";
        
        try (Connection con = BD.conexion();
             PreparedStatement verificar = con.prepareStatement(verificarBD)) {

                verificar.setInt(1, NumeroDocumento);                
                var rs = verificar.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    model.addAttribute("msg", "Numero de documento ya registrado");
                    return "registro";
                }
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("msg", "Error con la BD");
            return "registro";
        }
        
        // 4. Si pasó la validación y la BD, generamos código y guardamos en sesión
        String codigo = generarCodigo();
        
        session.setAttribute("usuarioTemporal", usuarioTemporal); 
        session.setAttribute("codigo", codigo);
        session.setAttribute("correo", correo); 
        
        emailService.enviarCodigo(correo, codigo);
        
        model.addAttribute("msg", "Se envió un código a: " + correo);
        return "verificar";
    }
    
    
    @PostMapping("/verificar")
    public String verificarCodigo(@RequestParam String codigoIngresado, Model model) {

        String codigoCorrecto = (String) session.getAttribute("codigo");
        usuarios e = (usuarios) session.getAttribute("usuarioTemporal"); 

        if (codigoCorrecto == null || e == null) {
            model.addAttribute("msg", "La sesión ha expirado. Regístrate de nuevo.");
            return "registro";
        }

        if (codigoIngresado.equals(codigoCorrecto)) {
            
            String insertarBD = "INSERT INTO usuarios (ID_documento, nombreC, cinturon_rango, edad, contraseña,correo, sexo, nacionalidad) "+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection con = BD.conexion();
                 PreparedStatement insertar = con.prepareStatement(insertarBD)) {

                insertar.setInt(1, e.getID_documento());
                insertar.setString(2, e.getNombreC());
                insertar.setString(3, e.getCinturon_rango());
                insertar.setInt(4, e.getEdad());
                insertar.setString(5, e.getContraseña());
                insertar.setString(6, e.getCorreo());
                insertar.setString(7, e.getSexo());
                insertar.setString(8, e.getNacionalidad());

                insertar.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
                model.addAttribute("msg", "Error al guardar en la base de datos.");
                return "verificar";
            }

            // LIMPIEZA DE SESIÓN
            session.removeAttribute("codigo");
            session.removeAttribute("usuarioTemporal");
            
            // Opcional: Auto-login al registrarse (guardar nombre en sesión)
            session.setAttribute("usuarioLogueado", e.getNombreC());
            
            model.addAttribute("msg", "Registro verificado e ingresado exitosamente.");
            return "inicio";
        }

        model.addAttribute("msg", "Código incorrecto.");
        return "verificar";
    }
}