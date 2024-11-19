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
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author patriciaaguayo
 */
public class LogicaUsuarios {
    
    private Conexion conex = new Conexion();
    private Connection conexion = conex.getConnection();
    ArrayList<String>CodigosUsuarios;
    ArrayList<String>CodigosProductos;
    private String codigoOriginal;
    
    public LogicaUsuarios(){
        CodigosUsuarios = new ArrayList<>();
        CodigosProductos = new ArrayList<>();
    }
    
    // Métodos para Buscar Usuario y traer su información
    
    public void BuscarUsuario(javax.swing.JTextField idUsuarioU, javax.swing.JTextField emailUsuario, javax.swing.JTextField direccionUsuario,
            javax.swing.JTextField nombreUsuario) {
    
        if(idUsuarioU.getText().isBlank()){
            
            JOptionPane.showMessageDialog(null, "Debes ingresar un id para buscar un usuario");
            
        }else{
           
            guardarCodigosUsuarios();

            String codigoIngresado = idUsuarioU.getText().trim(); 
            
            
            int idIngresado;
            
            try {
                idIngresado = Integer.parseInt(codigoIngresado);
                
            } catch (NumberFormatException e) {
                
                JOptionPane.showMessageDialog(null, "Id debe contener solo números");
                idUsuarioU.setText(""); 
                return; 
            }

            boolean encontrado = comprobarCodigoUsuario(codigoIngresado); 

            if (encontrado) {
                datosUsuario(idIngresado, idUsuarioU, emailUsuario, direccionUsuario, nombreUsuario); 
                guardarCodigosUsuarios(); 

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
    
    // Método para limpiar los campos de la interfaz usuarios
    
    public void limpiarUsuarios(javax.swing.JTextField idUsuarioU, javax.swing.JTextField emailUsuario, javax.swing.JTextField direccionUsuario,
            javax.swing.JTextField nombreUsuario){
        
        idUsuarioU.setText("");
        emailUsuario.setText("");
        direccionUsuario.setText("");
        nombreUsuario.setText("");
    }
    
   
    
    public void guardarCodigosUsuarios() {
        
        CodigosUsuarios.clear();  // Limpia la lista antes de cargar los códigos nuevamente

        Connection conexion = Conexion.getConnection();  
        String consulta = "SELECT idUsuario FROM usuarios";
        Statement sts = null;
        ResultSet rs = null;

        try {
            sts = conexion.createStatement();  
            rs = sts.executeQuery(consulta);  

            while (rs.next()) {
                String codigo = rs.getString("idUsuario").trim();  // Para eliminar espacios en blanco
                CodigosUsuarios.add(codigo);  // Agregar cada código a la lista
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
   
   public boolean comprobarCodigoUsuario(String codigoIngresado) {
       
        codigoIngresado = codigoIngresado.trim();

        return CodigosUsuarios.contains(codigoIngresado);
    }
   
   public void guardarCodigosProductos() {
        
        CodigosProductos.clear();  // Limpia la lista antes de cargar los códigos nuevamente

        Connection conexion = Conexion.getConnection();  
        String consulta = "SELECT idProducto FROM productos";
        Statement sts = null;
        ResultSet rs = null;

        try {
            sts = conexion.createStatement();  
            rs = sts.executeQuery(consulta);  

            while (rs.next()) {
                String codigo = rs.getString("idProducto").trim();  // Para eliminar espacios en blanco
                CodigosProductos.add(codigo);  // Agregar cada código a la lista
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
   
   public boolean comprobarCodigoProducto(String codigoIngresado) {
       
        codigoIngresado = codigoIngresado.trim();

        return CodigosProductos.contains(codigoIngresado);
    }

 // Método para buscar, realizar y guardas compra en la interfaz de Compra
   
  public void RealizarCompra(javax.swing.JTextField idUsuarioC, javax.swing.JTextField idProductoC, javax.swing.JTextField Cantidad) {
    
        if(idUsuarioC.getText().isBlank()){
            
            JOptionPane.showMessageDialog(null, "Debes ingresar un id para realizar una compra como un usuario");
            
        }else{
           
            guardarCodigosUsuarios();

            String codigoIngresadoU = idUsuarioC.getText().trim(); 
            
            
            int idIngresadoU;
            
            try {
                idIngresadoU = Integer.parseInt(codigoIngresadoU);
                
            } catch (NumberFormatException e) {
                
                JOptionPane.showMessageDialog(null, "Id de usuario debe contener solo números");
                idUsuarioC.setText(""); 
                return; 
            }
            
            guardarCodigosProductos();
            
            String codigoIngresadoP = idProductoC.getText().trim();
            
            int idIngresadoP;
            
            try {
                idIngresadoP = Integer.parseInt(codigoIngresadoP);
                
            } catch (NumberFormatException e) {
                
                JOptionPane.showMessageDialog(null, "Id de producto debe contener solo números");
                idProductoC.setText(""); 
                return; 
            }
            
            // comprobación de la cantidad para que no sea un campo vacío ni que sea 0
            
            if (Cantidad.getText().isBlank()) {
                JOptionPane.showMessageDialog(null, "Debes ingresar una cantidad para realizar la compra");
                return;
            }

            int cantidadIngresada;

            try {
                
                cantidadIngresada = Integer.parseInt(Cantidad.getText().trim());
                
                if (cantidadIngresada <= 0) {
                    JOptionPane.showMessageDialog(null, "La cantidad debe ser un número mayor a 0");
                    Cantidad.setText("");
                    return;
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "La cantidad debe ser un número válido");
                Cantidad.setText("");
                return;
            }
            
            boolean encontradoU = comprobarCodigoUsuario(codigoIngresadoU); 
            boolean encontradoP = comprobarCodigoProducto(codigoIngresadoP);

            if (encontradoU && encontradoP) {
                
                insertarCompra(idIngresadoU, idIngresadoP, cantidadIngresada);
                JOptionPane.showMessageDialog(null, "Compra realizada con éxito");
                
                guardarCodigosUsuarios();
                guardarCodigosProductos();

            } else {
                JOptionPane.showMessageDialog(null, "Id de usuario o producto no encontrado");
                if (!encontradoU) idUsuarioC.setText("");
                if (!encontradoP) idProductoC.setText("");
            }
        }    
        
    } 
  
    public void insertarCompra(int idUsuario, int idProducto, int Cantidad){
        
        LocalDate fechaL = LocalDate.now();
        Date fecha = Date.valueOf(fechaL);
        
        String sql = "INSERT INTO historialCompras (idUsuario, idProducto, cantidad, fecha) VALUES (?,?,?,?)";
        
        try{
            
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setInt(2, idProducto);
            ps.setInt(3, Cantidad);
            ps.setDate(4, fecha);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                
                System.out.println("Compra insertada correctamente para el usuario con ID " + idUsuario);
            
            } else {
                System.out.println("No se pudo insertar la compra. Revisa los datos proporcionados.");
            }   
            
        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
        }
        
    }
    
    // Método para limpiar los campos de la interfaz compras
    
    public void limpiarCompras(javax.swing.JTextField idUsuarioC, javax.swing.JTextField idProductoC, javax.swing.JTextField Cantidad){
        
        idUsuarioC.setText("");
        idProductoC.setText("");
        Cantidad.setText("");

    }

    
}

