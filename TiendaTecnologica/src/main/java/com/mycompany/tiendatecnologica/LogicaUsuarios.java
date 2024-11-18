/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendatecnologica;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author patriciaaguayo
 */
public class LogicaUsuarios {
    
    private Conexion conex = new Conexion();
    private Connection conexion = conex.getConnection();
    ArrayList<String>Codigos;
    private String codigoOriginal;
    
    public LogicaUsuarios(){
        Codigos = new ArrayList<>();
    }
    
    // Métodos para Buscar Usuario y traer su información
    
    public void BuscarAlumno(javax.swing.JTextField idUsuarioU, javax.swing.JTextField emailUsuario, javax.swing.JTextField direccionUsuario,
            javax.swing.JTextField nombreUsuario) {
    
        if(idUsuarioU.getText().isBlank()){
            
            JOptionPane.showMessageDialog(null, "Debes ingresar un id para buscar un usuario");
            
        }else{
           
            guardarCodigos();

            String codigoIngresado = idUsuarioU.getText().trim(); 
            
            
            int idIngresado;
            
            try {
                idIngresado = Integer.parseInt(codigoIngresado);
                
            } catch (NumberFormatException e) {
                
                JOptionPane.showMessageDialog(null, "Id debe contener solo números");
                idUsuarioU.setText(""); 
                return; 
            }

            boolean encontrado = comprobarCodigo(codigoIngresado); 

            if (encontrado) {
                datosUsuario(idIngresado, idUsuarioU, emailUsuario, direccionUsuario, nombreUsuario); 
                guardarCodigos(); 

            } else {
                JOptionPane.showMessageDialog(null, "Id " + codigoIngresado + " no encontrado");
                idUsuarioU.setText("");  
            } 
        }    
        
    }
    
    // Método para obtener datos de los usuarios
    
    public void datosUsuario(int codigoUsuario, javax.swing.JTextField idUsuarioU, javax.swing.JTextField emailUsuario, javax.swing.JTextField direccionUsuario,
            javax.swing.JTextField nombreUsuario) {

        String consulta = "select usuarios.idUsuario, usuarios.nombreUsuario, usuarios.email, direcciones.calle, \n" +
                          "direcciones.numero, direcciones.ciudad, direcciones.pais from usuarios\n" +
                          "JOIN direcciones ON usuarios.idUsuario = direcciones.idUsuario\n" +
                          "where usuarios.idUsuario = ?";


       
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            
            ps = conexion.prepareStatement(consulta);
            ps.setInt(1, codigoUsuario); 

            rs = ps.executeQuery();

            if (rs.next()) {
                
                int id = rs.getInt("idUsuario");
                String nombre = rs.getString("nombreUsuario");
                String email = rs.getString("email");
                String calle = rs.getString("calle");
                String numero = rs.getString("numero");
                String ciudad = rs.getString("ciudad");
                String pais = rs.getString("pais");
                

                // Colocar los valores en los campos de texto
                
                String idTexto = String.valueOf(id);
                
                idUsuarioU.setText(idTexto);
                codigoOriginal = idTexto;
                nombreUsuario.setText(nombre);
                emailUsuario.setText(email);
                
                String direccion = calle + ", " + numero + ", " + ciudad + ", " + pais;
                
                direccionUsuario.setText(direccion);

            } else {
                
                JOptionPane.showMessageDialog(null, "No se encontró el usuario con el id: " + codigoUsuario);
            }

        } catch (SQLException e) {
           
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al ejecutar la consulta");

        } finally {
            try {
                
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void limpiar(javax.swing.JTextField idUsuarioU, javax.swing.JTextField emailUsuario, javax.swing.JTextField direccionUsuario,
            javax.swing.JTextField nombreUsuario){
        
        idUsuarioU.setText("");
        emailUsuario.setText("");
        direccionUsuario.setText("");
        nombreUsuario.setText("");
    }
    
    public void guardarCodigos() {
        
        Codigos.clear();  // Limpia la lista antes de cargar los códigos nuevamente

        Connection conexion = Conexion.getConnection();  
        String consulta = "SELECT idUsuario FROM usuarios";
        Statement sts = null;
        ResultSet rs = null;

        try {
            sts = conexion.createStatement();  
            rs = sts.executeQuery(consulta);  

            while (rs.next()) {
                String codigo = rs.getString("idUsuario").trim();  // Para eliminar espacios en blanco
                Codigos.add(codigo);  // Agregar cada código a la lista
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los códigos: " + e.getMessage());

        } finally {
            try {
                if (rs != null) rs.close();
                if (sts != null) sts.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Métodos para buscar y almacenar los id de los usuarios
   
   public boolean comprobarCodigo(String codigoIngresado) {
       
        codigoIngresado = codigoIngresado.trim();

        return Codigos.contains(codigoIngresado);
    }
    
}
