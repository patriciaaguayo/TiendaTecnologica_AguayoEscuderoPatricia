/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendatecnologica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author patriciaaguayo
 */
public class Conexion {
    
    private static Connection connection;
    private static String usuario = "root";
    private static String pass = "";
    private static String servidor = "127.0.0.1";
    private static String puertoIlerna = "3307";
    private static String puertoCasa = "3306";
    private static String bbdd = "TiendaOnline";
    //private static String urlIlerna = "jdbc:mysql://" + servidor+ ":" +puertoIlerna + "/"+bbdd;
    private static String urlCasa = "jdbc:mysql://" + servidor+ ":" +puertoCasa + "/"+bbdd;
    
    private static String driver = "com.mysql.cj.jdbc.Driver";
    
    
    public Conexion(){
        
    }

     // Método para obtener la conexión
    
    public static Connection getConnection() {
        
        try {
            
            // Cargar el driver de MySQL
            
            Class.forName(driver);
            
            // Establecer la conexión
            
            connection = DriverManager.getConnection(urlCasa, usuario, pass);
            //connection = DriverManager.getConnection(urlIlerna, usuario, pass);
            
            if (connection != null) {
                System.out.println("\n Conexión correcta");
            }
            
        } catch (SQLException e) {
            System.out.println("\n Error de conexión: " + e.getMessage());
            
        } catch (ClassNotFoundException e) {
            System.out.println("\n Error al cargar el driver: " + e.getMessage());
        }
        
        return connection;
    }
    
    // Método para cerrar la conexión
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("\n Conexión cerrada");
            }
        } catch (SQLException e) {
            System.out.println("\n Error al cerrar la conexión: " + e.getMessage());
        }
    }
    
}
