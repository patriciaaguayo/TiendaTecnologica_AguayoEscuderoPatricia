/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendatecnologica;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 *
 * @author patriciaaguayo
 */
public class CrearBBDD {
    
    private Connection conexion;
    
    public CrearBBDD(Conexion conex){
        conexion = conex.getConnection();
    }
    
    public void insertarDatos() throws Exception {
      
        try {
            
            // Crear objeto Mapper para leer el JSON
            
            ObjectMapper lector = new ObjectMapper();
            JsonNode ruta = lector.readTree(new File("src/main/resources/InformacionTienda.json"));

            // Inserción de la tienda
            
            JsonNode tienda = ruta.get("tienda");
            insertarTienda(tienda);

            // Inserción de las categorías y productos
            
            JsonNode categorias = tienda.get("categorias");
            
            if (categorias != null && categorias.isArray()) {
                insertarCategorias(categorias);
                
            } else {
                System.err.println("\n No se encontraron categorías en el archivo JSON.");
            }

            // Inserción de usuarios y sus direcciones y historial de compras
            
            JsonNode usuarios = tienda.get("usuarios");
            
            for (JsonNode usuario : usuarios) {
                
                // Inserción del usuario
                
                insertarUsuario(usuario);

                // Inserción de la dirección del usuario
                
                insertarDireccion(usuario.get("direccion"), usuario.get("id").asInt());

                // Inserción del historial de compras
                
                JsonNode historialComprasNode = usuario.get("historialCompras");
                int idUsuario = usuario.get("id").asInt();
                insertarHistorialCompras(historialComprasNode, idUsuario);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Método para insertar los datos de la tabla tienda

    public void insertarTienda(JsonNode tiendaNode) throws SQLException {
        
        String nombreTienda = tiendaNode.get("nombre").asText();

        String checkTiendaQuery = "SELECT COUNT(*) FROM tienda WHERE nombreTienda = ?";
        PreparedStatement checkTiendaStatement = conexion.prepareStatement(checkTiendaQuery);
        checkTiendaStatement.setString(1, nombreTienda);
        ResultSet resultSet = checkTiendaStatement.executeQuery();
        resultSet.next();

        if (resultSet.getInt(1) > 0) {
            System.out.println("\n La tienda '" + nombreTienda + "' ya está registrada.");
            return;
        }

        String insertTiendaQuery = "INSERT INTO tienda (nombreTienda) VALUES (?)";
        PreparedStatement insertTiendaStatement = conexion.prepareStatement(insertTiendaQuery);
        insertTiendaStatement.setString(1, nombreTienda);
        insertTiendaStatement.executeUpdate();
        System.out.println("\n La tienda '" + nombreTienda + "' insertada correctamente.");
    }
    
    // Método para insertar los datos de la tabla categorias
    
    public void insertarCategorias(JsonNode categoriasNode) throws SQLException {
        
        if (categoriasNode == null || !categoriasNode.isArray()) {
            System.err.println("\n El nodo de categorías no es válido o está vacío.");
            return;
        }

        for (JsonNode categoria : categoriasNode) {
            JsonNode idNode = categoria.get("id");
            JsonNode nombreNode = categoria.get("nombre");

            if (idNode == null || idNode.isNull()) {
                System.err.println("\n Una categoría no tiene un 'id' válido.");
                continue;
            }

            if (nombreNode == null || nombreNode.isNull()) {
                System.err.println("\n La categoría con ID " + idNode.asInt() + " no tiene un 'nombre' válido.");
                continue;
            }

            int idCategoria = idNode.asInt();
            String nombreCategoria = nombreNode.asText();

            // Verifica si la categoría ya existe
            
            String checkCategoriaQuery = "SELECT COUNT(*) FROM categorias WHERE idCategoria = ?";
            PreparedStatement checkCategoriaStatement = conexion.prepareStatement(checkCategoriaQuery);
            checkCategoriaStatement.setInt(1, idCategoria);
            ResultSet resultSet = checkCategoriaStatement.executeQuery();
            resultSet.next();

            if (resultSet.getInt(1) > 0) {
                System.out.println("\n La categoría '" + nombreCategoria + "' ya está registrada.");
                
            } else {
                
                // Insertar categoría
                
                String insertCategoriaQuery = "INSERT INTO categorias (idCategoria, nombreCategoria) VALUES (?, ?)";
                PreparedStatement insertCategoriaStatement = conexion.prepareStatement(insertCategoriaQuery);
                insertCategoriaStatement.setInt(1, idCategoria);
                insertCategoriaStatement.setString(2, nombreCategoria);
                insertCategoriaStatement.executeUpdate();
                System.out.println("\n Categoría '" + nombreCategoria + "' insertada correctamente.");
            }

            // Insertar productos de la categoría
            
            JsonNode productosNode = categoria.get("productos");
            
            if (productosNode != null && productosNode.isArray()) {
                insertarProductos(productosNode, idCategoria);
                
            } else {
                System.err.println("\n La categoría '" + nombreCategoria + "' no tiene productos.");
            }
        }
    }
    
    // Método para insertar los datos de los profuctos en la tabla productos
    
    public void insertarProductos(JsonNode productosNode, int idCategoria) throws SQLException {
        
        for (JsonNode producto : productosNode) {
            int productoId = producto.get("id").asInt();
            String nombreProducto = producto.get("nombre").asText();

            // Verifica si el producto ya existe en la base de datos
            
            String checkProductoQuery = "SELECT COUNT(*) FROM productos WHERE idProducto = ?";
            PreparedStatement checkProductoStatement = conexion.prepareStatement(checkProductoQuery);
            checkProductoStatement.setInt(1, productoId);
            ResultSet resultSet = checkProductoStatement.executeQuery();
            resultSet.next();

            if (resultSet.getInt(1) > 0) {
                System.out.println("\n El producto con ID " + productoId + " ya está registrado.");
                continue;
            }

            // Hacer string de características separado por comas
            
            StringBuilder caracteristicasBuilder = new StringBuilder();
            JsonNode caracteristicasNode = producto.get("caracteristicas");
            
            if (caracteristicasNode != null) {
                caracteristicasNode.fieldNames().forEachRemaining(key -> { // Uso del lamda para ahorrar código
                    String valor = caracteristicasNode.get(key).asText();
                    caracteristicasBuilder.append(key).append(": ").append(valor).append(", ");
                });
            }
            
            // Elimina la última coma y espacio
            
            String caracteristicas = caracteristicasBuilder.toString().replaceAll(", $", "");

            // Insertar un producto en la base de datos
            
            String insertProductoQuery = "INSERT INTO productos (idProducto, nombreProducto, precio, descripcion, caracteristicas, inventario, idCategoria) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertProductoStatement = conexion.prepareStatement(insertProductoQuery);
            insertProductoStatement.setInt(1, productoId);
            insertProductoStatement.setString(2, nombreProducto);
            insertProductoStatement.setDouble(3, producto.get("precio").asDouble());
            insertProductoStatement.setString(4, producto.get("descripcion").asText());
            insertProductoStatement.setString(5, caracteristicas); // Características como string
            insertProductoStatement.setInt(6, producto.get("inventario").asInt());
            insertProductoStatement.setInt(7, idCategoria); // Usar idCategoria correcto
            insertProductoStatement.executeUpdate();
            System.out.println("\n El producto '" + nombreProducto + "' insertado correctamente.");

            // Insertar imágenes de los productos
            
            insertarImagenesProducto(producto.get("imagenes"), productoId);
        }
    }
    
    // Método para insertar los datos de la imágenes de los productos
    
    public void insertarImagenesProducto(JsonNode imagenesNode, int productoId) throws SQLException {
        
        for (JsonNode imagen : imagenesNode) {
            String imagenUrl = imagen.asText();

            String checkImagenQuery = "SELECT COUNT(*) FROM imagenesProducto WHERE imagenUrl = ? AND idProducto = ?"; // Verifica si la foto ya existe
            PreparedStatement checkImagenStatement = conexion.prepareStatement(checkImagenQuery);
            checkImagenStatement.setString(1, imagenUrl);
            checkImagenStatement.setInt(2, productoId);
            ResultSet resultSet = checkImagenStatement.executeQuery();
            resultSet.next();

            if (resultSet.getInt(1) > 0) {
                System.out.println("\n La imagen '" + imagenUrl + "' ya está registrada para el producto con ID " + productoId);
                continue;
            }

            String insertImagenQuery = "INSERT INTO imagenesProducto (idProducto, imagenUrl) VALUES (?, ?)";
            PreparedStatement insertImagenStatement = conexion.prepareStatement(insertImagenQuery);
            insertImagenStatement.setInt(1, productoId);
            insertImagenStatement.setString(2, imagenUrl);
            insertImagenStatement.executeUpdate();
            System.out.println("\n La imagen '" + imagenUrl + "' insertada correctamente para el producto con ID " + productoId);
        }
    }
    
    // Método para insertar los datos de los usuarios
    
    public void insertarUsuario(JsonNode usuarioNode) throws SQLException {
        
        int idUsuario = usuarioNode.get("id").asInt();

        String checkUsuarioQuery = "SELECT COUNT(*) FROM usuarios WHERE idUsuario = ?"; // Verifica si ese usuario ya existe
        PreparedStatement checkUsuarioStatement = conexion.prepareStatement(checkUsuarioQuery);
        checkUsuarioStatement.setInt(1, idUsuario);
        ResultSet resultSet = checkUsuarioStatement.executeQuery();
        resultSet.next();

        if (resultSet.getInt(1) > 0) {
            System.out.println("\n El usuario con id '" + idUsuario + "' ya está registrado.");
            return;
        }

        String insertUsuarioQuery = "INSERT INTO usuarios (idUsuario, nombreUsuario, email) VALUES (?, ?, ?)";
        PreparedStatement insertUsuarioStatement = conexion.prepareStatement(insertUsuarioQuery);
        insertUsuarioStatement.setInt(1, idUsuario);
        insertUsuarioStatement.setString(2, usuarioNode.get("nombre").asText());
        insertUsuarioStatement.setString(3, usuarioNode.get("email").asText());
        insertUsuarioStatement.executeUpdate();
        System.out.println("\n El usuario '" + usuarioNode.get("nombre").asText() + "' insertado correctamente.");
    }
    
    // Método para insertar los datos en la tabla direcciones
    
    public void insertarDireccion(JsonNode direccionNode, int usuarioId) throws SQLException {
        
        String checkDireccionQuery = "SELECT COUNT(*) FROM direcciones WHERE idUsuario = ?";
        PreparedStatement checkDireccionStatement = conexion.prepareStatement(checkDireccionQuery); // Verifica si un usuario ya tiene una dirección apuntada
        checkDireccionStatement.setInt(1, usuarioId);
        ResultSet resultSet = checkDireccionStatement.executeQuery();
        resultSet.next();

        if (resultSet.getInt(1) > 0) {
            System.out.println("\n La dirección para el usuario con ID " + usuarioId + " ya está registrada.");
            return;
        }

        String insertDireccionQuery = "INSERT INTO direcciones (idUsuario, calle, numero, ciudad, pais) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement insertDireccionStatement = conexion.prepareStatement(insertDireccionQuery);
        insertDireccionStatement.setInt(1, usuarioId);
        insertDireccionStatement.setString(2, direccionNode.get("calle").asText());
        insertDireccionStatement.setInt(3, direccionNode.get("numero").asInt());
        insertDireccionStatement.setString(4, direccionNode.get("ciudad").asText());
        insertDireccionStatement.setString(5, direccionNode.get("pais").asText());
        insertDireccionStatement.executeUpdate();
        System.out.println("\n La dirección insertada correctamente para el usuario con ID " + usuarioId);
    }
    
    // Método para insertar los datos del historial de compras
    
    public void insertarHistorialCompras(JsonNode historialComprasNode, int idUsuario) throws SQLException {
        
        for (JsonNode compra : historialComprasNode) {
            int idProducto = compra.get("productoId").asInt();
            int cantidad = compra.get("cantidad").asInt();
            String fecha = compra.get("fecha").asText();

            // Verificación de la compra para valores nulos o inválidos
            
            if (idProducto == 0 || cantidad == 0 || fecha == null || fecha.isEmpty()) {
                System.out.println("\n Compra no válida en el historial de compras del usuario con ID " + idUsuario);
                continue;
            }

            // Inserción del historial de compras en la base de datos
            
            String insertHistorialQuery = "INSERT INTO historialCompras (idUsuario, idProducto, cantidad, fecha) VALUES (?, ?, ?, ?)";
            PreparedStatement insertHistorialStatement = conexion.prepareStatement(insertHistorialQuery);
            insertHistorialStatement.setInt(1, idUsuario);  
            insertHistorialStatement.setInt(2, idProducto);  
            insertHistorialStatement.setInt(3, cantidad);   
            insertHistorialStatement.setString(4, fecha);
            insertHistorialStatement.executeUpdate();

            System.out.println("\n Compra registrada para el usuario con ID " + idUsuario + " en el historial.");
        }
    }
    
    // Método para ver que los datos del json se han guardado correctamente en la BBDD
    
    public void mostrarBBDD() throws SQLException{
        

        // Consultar y mostrar los datos de la tabla tienda
        
        String consultaTienda = "SELECT * FROM tienda";
        PreparedStatement tiendaStatement = conexion.prepareStatement(consultaTienda);
        ResultSet tiendaResultSet = tiendaStatement.executeQuery();
        System.out.println("\n Tiendas:");
        while (tiendaResultSet.next()) {
            System.out.println(" ID Tienda: " + tiendaResultSet.getInt("idTienda") + ", Nombre: " + tiendaResultSet.getString("nombreTienda"));
        }

        // Consultar y mostrar los datos de la tabla categorias
        
        String consultaCategorias = "SELECT * FROM categorias";
        PreparedStatement categoriasStatement = conexion.prepareStatement(consultaCategorias);
        ResultSet categoriasResultSet = categoriasStatement.executeQuery();
        System.out.println("\n Categorías:");
        while (categoriasResultSet.next()) {
            System.out.println(" ID Categoría: " + categoriasResultSet.getInt("idCategoria") + ", Nombre: " + categoriasResultSet.getString("nombreCategoria"));
        }

        // Consultar y mostrar los datos de la tabla productos
        
        String consultaProductos = "SELECT * FROM productos";
        PreparedStatement productosStatement = conexion.prepareStatement(consultaProductos);
        ResultSet productosResultSet = productosStatement.executeQuery();
        System.out.println("\n Productos:");
        while (productosResultSet.next()) {
            System.out.println(" ID Producto: " + productosResultSet.getInt("idProducto") + ", Nombre: " + productosResultSet.getString("nombreProducto") + 
                               ", Precio: " + productosResultSet.getDouble("precio") + ", Descripción: " + productosResultSet.getString("descripcion") +
                               ", Características: " + productosResultSet.getString("caracteristicas") + ", Inventario: " + productosResultSet.getInt("inventario"));
        }

        // Consultar y mostrar los datos de la tabla imagenesProducto
        
        String consultaImagenesProducto = "SELECT * FROM imagenesProducto";
        PreparedStatement imagenesProductoStatement = conexion.prepareStatement(consultaImagenesProducto);
        ResultSet imagenesProductoResultSet = imagenesProductoStatement.executeQuery();
        System.out.println("\n Imágenes de Productos:");
        while (imagenesProductoResultSet.next()) {
            System.out.println(" ID Imagen: " + imagenesProductoResultSet.getInt("idImagen") + ", ID Producto: " + imagenesProductoResultSet.getInt("idProducto") +
                               ", URL Imagen: " + imagenesProductoResultSet.getString("imagenUrl"));
        }

        // Consultar y mostrar los datos de la tabla usuarios
        
        String consultaUsuarios = "SELECT * FROM usuarios";
        PreparedStatement usuariosStatement = conexion.prepareStatement(consultaUsuarios);
        ResultSet usuariosResultSet = usuariosStatement.executeQuery();
        System.out.println("\n Usuarios:");
        while (usuariosResultSet.next()) {
            System.out.println(" ID Usuario: " + usuariosResultSet.getInt("idUsuario") + ", Nombre: " + usuariosResultSet.getString("nombreUsuario") + 
                               ", Email: " + usuariosResultSet.getString("email"));
        }

        // Consultar y mostrar los datos de la tabla direcciones
        
        String consultaDirecciones = "SELECT * FROM direcciones";
        PreparedStatement direccionesStatement = conexion.prepareStatement(consultaDirecciones);
        ResultSet direccionesResultSet = direccionesStatement.executeQuery();
        System.out.println("\n Direcciones:");
        while (direccionesResultSet.next()) {
            System.out.println(" ID Dirección: " + direccionesResultSet.getInt("idDireccion") + ", ID Usuario: " + direccionesResultSet.getInt("idUsuario") + 
                               ", Calle: " + direccionesResultSet.getString("calle") + ", Número: " + direccionesResultSet.getInt("numero") + 
                               ", Ciudad: " + direccionesResultSet.getString("ciudad") + ", País: " + direccionesResultSet.getString("pais"));
        }

        // Consultar y mostrar los datos de la tabla historialCompras
        
        String consultaHistorialCompras = "SELECT * FROM historialCompras";
        PreparedStatement historialComprasStatement = conexion.prepareStatement(consultaHistorialCompras);
        ResultSet historialComprasResultSet = historialComprasStatement.executeQuery();
        System.out.println("\n Historial de Compras:");
        while (historialComprasResultSet.next()) {
            System.out.println(" ID Historial: " + historialComprasResultSet.getInt("idHistorial") + ", ID Usuario: " + historialComprasResultSet.getInt("idUsuario") + 
                               ", ID Producto: " + historialComprasResultSet.getInt("idProducto") + ", Cantidad: " + historialComprasResultSet.getInt("cantidad") + 
                               ", Fecha: " + historialComprasResultSet.getDate("fecha"));
        }
    }
}
