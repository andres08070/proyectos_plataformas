/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cr.Ejemplo1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author andre
 */
public class BD {
    static String url ="jdbc:mysql://localhost:3306/campeonatos";
    static String user ="root";
    static String pass ="";
    public static Connection conexion(){
        Connection con =null;
        try{
            con=DriverManager.getConnection(url, user, pass);
            System.out.println("Conectado a la BD");
        }catch(SQLException e){
            e.printStackTrace();
        }
        return con;
    }
}
