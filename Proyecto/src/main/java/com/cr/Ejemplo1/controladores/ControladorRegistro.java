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
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller 
public class ControladorRegistro {
    
    ArrayList <usuarios> usuario = new ArrayList();
    @Autowired
    private EmailService emailService;

    @Autowired
    private HttpSession session;

    private String generarCodigo() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam int ID_documento, 
                            @RequestParam String nombreC, 
                            @RequestParam String sexo, 
                            @RequestParam String nacionalidad, 
                            @RequestParam String correo,
                            @RequestParam String contraseña,
                            @RequestParam String Confirmar,Model model) {
        
        String verificarBD = "SELECT COUNT(*) FROM usuarios WHERE ID_documento = ?";
        
        String codigo = generarCodigo();
        session.setAttribute("usuarioLogueado",nombreC);
        session.setAttribute("id",ID_documento);
        session.setAttribute("correo", correo);
        session.setAttribute("codigo", codigo);
        emailService.enviarCodigo(correo, codigo);
        
        try (Connection con = BD.conexion();
        PreparedStatement verificar = con.prepareStatement(verificarBD)) {

               // Verificar si el ID ya existe
                verificar.setInt(1,ID_documento);               
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
        

        if (contraseña.equals(Confirmar)){
            usuario.add(new usuarios(ID_documento,nombreC,null,0,contraseña,correo,sexo,nacionalidad));
            model.addAttribute("msg", "Se envió un código a: " + correo);
            return "verificar";
        }
        model.addAttribute("msg","La contraseña no coincide");
        return "registro";
    }
    
    
    
    @PostMapping("/verificar")
    public String verificarCodigo(@RequestParam String codigoIngresado, Model model) {

        String codigoCorrecto = (String) session.getAttribute("codigo");

        String insertarBD = "INSERT INTO usuarios (ID_documento, nombreC, cinturon_rango, edad, contraseña,correo, sexo, nacionalidad) "+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        if (codigoIngresado.equals(codigoCorrecto)) {

            try (Connection con = BD.conexion();
                 PreparedStatement insertar = con.prepareStatement(insertarBD)) {

                // Solo toma un usuario (según tu intención)
                usuarios e = usuario.get(0);

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

            model.addAttribute("msg", "Registro verificado e ingresado exitosamente.");
            usuario.clear();
            return "inicio";
        }

        model.addAttribute("msg", "Código incorrecto.");
        return "verificar";
    }


}