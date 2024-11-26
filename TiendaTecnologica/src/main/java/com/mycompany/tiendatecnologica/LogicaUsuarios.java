/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendatecnologica;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
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
    HashMap<Integer, String> Categorias;
    private ArrayList<Producto> productosActuales;
    
    public LogicaUsuarios(){
        CodigosUsuarios = new ArrayList<>();
        CodigosProductos = new ArrayList<>();
        Categorias = new HashMap<>();
        productosActuales = new ArrayList<>();
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
    
    // Método para buscar las compras realizadas en la interfaz de Historial
    
    public void BuscarUsuarioHistorial(javax.swing.JTextField idUsuarioH, javax.swing.JTextArea Historial) {
    
        if(idUsuarioH.getText().isBlank()){
            
            JOptionPane.showMessageDialog(null, "Debes ingresar un id para buscar el historial de un usuario");
            
        }else{
           
            guardarCodigosUsuarios();

            String codigoIngresado = idUsuarioH.getText().trim(); 
            
            
            int idIngresado;
            
            try {
                idIngresado = Integer.parseInt(codigoIngresado);
                
            } catch (NumberFormatException e) {
                
                JOptionPane.showMessageDialog(null, "Id debe contener solo números");
                idUsuarioH.setText(""); 
                return; 
            }

            boolean encontrado = comprobarCodigoUsuario(codigoIngresado); 

            if (encontrado) {
                buscarHistorial(idIngresado, idUsuarioH, Historial); 
                guardarCodigosUsuarios(); 

            } else {
                JOptionPane.showMessageDialog(null, "Id " + codigoIngresado + " no encontrado");
                idUsuarioH.setText("");  
            } 
        }    
        
    }

    public void buscarHistorial(int codigoUsuario, javax.swing.JTextField idUsuarioH, javax.swing.JTextArea Historial) {

        String consulta = "SELECT usuarios.idUsuario, historialCompras.idHistorial, \n" +
                          "historialCompras.idProducto, historialCompras.cantidad, \n" +
                          "historialCompras.fecha FROM usuarios\n" +
                          "JOIN historialCompras ON usuarios.idUsuario = historialCompras.idUsuario\n" +
                          "WHERE usuarios.idUsuario = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = conexion.prepareStatement(consulta);
            ps.setInt(1, codigoUsuario); 

            rs = ps.executeQuery();

            Historial.setText(""); 

            boolean tieneHistorial = false;

            while (rs.next()) {

                int idU = rs.getInt("idUsuario");
                int idH = rs.getInt("idHistorial");
                int idP = rs.getInt("idProducto");
                int cantidad = rs.getInt("cantidad");
                Date fecha = rs.getDate("fecha");

                
                SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
                String fechaH = formato.format(fecha);

                
                String historial = "\n ID Historial: " + idH + ",  ID Producto: " + idP + 
                                   ",  Cantidad: " + cantidad + ",  Fecha: " + fechaH + "\n";

                Historial.append(historial);

                tieneHistorial = true;
            }

            if (!tieneHistorial) {
                JOptionPane.showMessageDialog(null, "No se encontró el historial de compras para el usuario con id: " + codigoUsuario);
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
    
    // Método para limpiar los campos de la interfaz compras
    
    public void limpiarHistorial(javax.swing.JTextField idUsuarioH, javax.swing.JTextArea Historial){
        
        idUsuarioH.setText("");
        Historial.setText("");

    }
    
    public void buscarProductos(javax.swing.JComboBox<String> Categorias, 
                            javax.swing.JLabel Producto1, javax.swing.JLabel Producto2, 
                            javax.swing.JLabel Producto3, InterfazProductos productos) {

        productosActuales.clear();

        // Obtener la categoría seleccionada 
        
        String categoria = Categorias.getSelectedItem().toString().toLowerCase();
        categoria = mayusculaPrimeraLetra(categoria);

        // Comprovar la selección de categoría
        
        if (categoria.equals("Seleccione categoria")) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar una categoría.");
            return;
        }

        int idCategoria = this.obtenerIdCategoria(categoria);

        if (idCategoria == -1) {
            JOptionPane.showMessageDialog(null, "Error: la categoría seleccionada no es válida.");
            return;
        }

        String consulta = """
            SELECT productos.idProducto, productos.nombreProducto, productos.precio, 
                   productos.descripcion, productos.caracteristicas, productos.inventario, 
                   imagenesProducto.imagenUrl
            FROM productos
            JOIN categorias ON categorias.idCategoria = productos.idCategoria
            LEFT JOIN imagenesProducto ON imagenesProducto.idProducto = productos.idProducto
            WHERE productos.idCategoria = ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(consulta)) {
            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                
                // Guardar un producto por ID
                
                Map<Integer, Producto> productosMap = new HashMap<>();

                while (rs.next()) {
                    int id = rs.getInt("idProducto");
                    String nombre = rs.getString("nombreProducto");
                    double precio = rs.getDouble("precio");
                    String descripcion = rs.getString("descripcion");
                    String caracteristicas = rs.getString("caracteristicas");
                    int cantidad = rs.getInt("inventario");
                    String imagenUrl = rs.getString("imagenUrl");

                    // Obtener producto existente o crear uno nuevo
                    
                    Producto producto = productosMap.getOrDefault(id, 
                        new Producto(id, nombre, precio, descripcion, caracteristicas, null, null, cantidad));

                    // Asignar imágenes correctamente
                    
                    if (imagenUrl != null) {
                        
                        if (imagenUrl.endsWith("1.png") && producto.getImagenUrl1() == null) { // imagen 1
                            producto.setImagenUrl1(imagenUrl);
                        
                        } else if (imagenUrl.endsWith("2.png") && producto.getImagenUrl2() == null) { // imagen 2
                            producto.setImagenUrl2(imagenUrl);
                        }
                    }

                    // Guardar el producto actualizado en la lista
                    
                    productosMap.put(id, producto);
                }

                productosActuales.addAll(productosMap.values());
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al buscar productos: " + e.getMessage());
            return;
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
            return;
        }

        // Establecer las fotos de los productos por categoría
        
        JLabel[] etiquetas = {Producto1, Producto2, Producto3};

        for (int i = 0; i < etiquetas.length; i++) {
            
            if (i < productosActuales.size()) {
                Producto producto = productosActuales.get(i);

                // Pone la primera foto de cada producto
                
                if (producto.getImagenUrl1() != null && !producto.getImagenUrl1().isEmpty()) {
                    etiquetas[i].setIcon(new ImageIcon(producto.getImagenUrl1())); 
                
                } else {
                    etiquetas[i].setIcon(null);
                    etiquetas[i].setText("Sin imagen disponible"); // Avisa si no se encuentra imagen
                }

                etiquetas[i].setToolTipText(producto.getNombre()); // Hace que al pasar el cursor muestre el nombre del producto
               
                etiquetas[i].setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cambiar el cursor al de una mano  
                
                etiquetas[i].setVisible(true); // Hace visibles las fotos de los productos

                // Muestra por consola las rutas de las dos fotos por producto para asegurar que no haya error
                
                System.out.println("Imagen cargada para producto: " + producto.getImagenUrl1());
                System.out.println("Imagen secundaria para producto: " + producto.getImagenUrl2());

                // Agrega un evento para que al hacer clic nos dirija a otra ventana con la información de ese producto
                
                int finalI = i; // Obtene que la id del producto que se ha clicado
                
                etiquetas[i].addMouseListener(new MouseAdapter() {
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        InterfazInfoProducto interfazProducto = new InterfazInfoProducto();
                        
                        // Así se puede indentidicar que producto se ha seleccionado y obtener sus datos
                        
                        interfazProducto.setProducto(productosActuales.get(finalI)); 
                        productos.dispose(); // Cierra la ventana anterior y así no hay bucles
                        interfazProducto.setVisible(true); // Hace visible la ventana con la info de ese producto
                        
                    }
                });
                
            } else { // Cuando no hay más productos
            
                etiquetas[i].setIcon(null);
                etiquetas[i].setText("Sin producto");
                etiquetas[i].setVisible(false);
            }
        }
    }
    
    // Métodos para buscar y almacenar las categorias de los productos
    
    public boolean comprobarCategoria(int idCategoria) {
        
        return Categorias.containsKey(idCategoria);
    }

    public void guardarCategorias() {
        
        Categorias.clear();  

        Connection conexion = Conexion.getConnection();  
        String consulta = "SELECT idCategoria, nombreCategoria FROM categorias"; 
        Statement sts = null;
        ResultSet rs = null;

        try {
            sts = conexion.createStatement();  
            rs = sts.executeQuery(consulta);  

            while (rs.next()) {
                int idCategoria = rs.getInt("idCategoria"); 
                String nombreCategoria = rs.getString("nombreCategoria").trim(); 
                Categorias.put(idCategoria, nombreCategoria); 
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar las categorias: " + e.getMessage());

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
    
    // Método para obtener el nombre de la categoria usando su ID
    
    public String getNombreCategoria(int idCategoria) {
        return Categorias.get(idCategoria); 
    }

    // Método para obtener el HashMap de categorias
    
    public HashMap<Integer, String> getCategorias() {
        return Categorias;
    }
    
    public int obtenerIdCategoria(String categoria) {
       
        guardarCategorias();

        int idCategoria = -1;

        for (Map.Entry<Integer, String> entry : Categorias.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(categoria)) {
                idCategoria = entry.getKey(); 
                break; 
            }
        }

        return idCategoria; 
    }
    
    public static String mayusculaPrimeraLetra(String palabra) {
    
        if (palabra == null || palabra.isEmpty()) {
        return palabra; 
    }
    return palabra.substring(0, 1).toUpperCase() + palabra.substring(1).toLowerCase();
}
    
}

