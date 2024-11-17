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
import java.sql.Statement;

/**
 *
 * @author patriciaaguayo
 */
public class CrearBBDD {
    
    private Connection conexion;
    
    public CrearBBDD(Conexion conex){
        conexion = conex.getConnection();
    }
    
    
    public void crearBBDD() {
        
        try {
            
            // Leer el archivo JSON
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("src/main/resources/basic.json"));        

            // Crear tablas si no existen
            
                String createUsersTable = """
                    CREATE IF NOT EXIST TABLE users (
                        id INT PRIMARY KEY,
                        name VARCHAR(100),
                        email VARCHAR(100),
                        isActive BOOLEAN
                    );
                """;
                conexion.createStatement().execute(createUsersTable);
            

            
                String createProductsTable = """
                    CREATE TABLE products (
                        id INT PRIMARY KEY,
                        name VARCHAR(100),
                        category VARCHAR(50),
                        price DECIMAL(10, 2)
                    );
                """;
                conexion.createStatement().execute(createProductsTable);
            

                String createOrdersTable = """
                    CREATE TABLE orders (
                        orderId INT PRIMARY KEY,
                        userId INT,
                        totalPrice DECIMAL(10, 2),
                        orderDate DATE,
                        FOREIGN KEY (userId) REFERENCES users(id)
                    );
                """;
                conexion.createStatement().execute(createOrdersTable);
            

                String createOrderProductsTable = """
                    CREATE TABLE order_products (
                        orderId INT,
                        productId INT,
                        quantity INT,
                        PRIMARY KEY (orderId, productId),
                        FOREIGN KEY (orderId) REFERENCES orders(orderId),
                        FOREIGN KEY (productId) REFERENCES products(id)
                    );
                """;
                conexion.createStatement().execute(createOrderProductsTable);
            

            // Insertar datos en la base de datos
            
            insertarDatos(rootNode, conexion);

            System.out.println("\n Datos importados correctamente.");
            conexion.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void insertarDatos(JsonNode rootNode, Connection connection) throws Exception {
        
        // Insertar datos en la tabla de usuarios
    
        JsonNode usersNode = rootNode.get("users");
        
        if (usersNode.isArray()) {
            String insertUserQuery = "INSERT INTO users (id, name, email, isActive) VALUES (?, ?, ?, ?)";
            String checkUserExistsQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
            PreparedStatement userStatement = connection.prepareStatement(insertUserQuery);
            PreparedStatement checkUserStatement = connection.prepareStatement(checkUserExistsQuery);

            for (JsonNode user : usersNode) {
                
                int userId = user.get("id").asInt();
                checkUserStatement.setInt(1, userId);
                ResultSet rs = checkUserStatement.executeQuery();
                
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.printf("Usuario con ID %d ya existe. Se omite la inserción.%n", userId);
                    continue; // Saltar al siguiente usuario
                }

                userStatement.setInt(1, userId);
                userStatement.setString(2, user.get("name").asText());
                userStatement.setString(3, user.get("email").asText());
                userStatement.setBoolean(4, user.get("isActive").asBoolean());
                userStatement.executeUpdate();
            }
        }

        // Insertar datos en la tabla de productos
        
        JsonNode productsNode = rootNode.get("products");
        
        if (productsNode.isArray()) {
            
            String insertProductQuery = "INSERT INTO products (id, name, category, price) VALUES (?, ?, ?, ?)";
            String checkProductExistsQuery = "SELECT COUNT(*) FROM products WHERE id = ?";
            PreparedStatement productStatement = connection.prepareStatement(insertProductQuery);
            PreparedStatement checkProductStatement = connection.prepareStatement(checkProductExistsQuery);

            for (JsonNode product : productsNode) {
                
                int productId = product.get("id").asInt();
                checkProductStatement.setInt(1, productId);
                ResultSet rs = checkProductStatement.executeQuery();
                
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.printf("Producto con ID %d ya existe. Se omite la inserción.%n", productId);
                    continue; // Saltar al siguiente producto
                }

                productStatement.setInt(1, productId);
                productStatement.setString(2, product.get("name").asText());
                productStatement.setString(3, product.get("category").asText());
                productStatement.setDouble(4, product.get("price").asDouble());
                productStatement.executeUpdate();
            }
        }

        // Insertar datos en la tabla de órdenes y productos asociados
        
        JsonNode ordersNode = rootNode.get("orders");
        
        if (ordersNode.isArray()) {
            
            String insertOrderQuery = "INSERT INTO orders (orderId, userId, totalPrice, orderDate) VALUES (?, ?, ?, ?)";
            String checkOrderExistsQuery = "SELECT COUNT(*) FROM orders WHERE orderId = ?";
            String insertOrderProductQuery = "INSERT INTO order_products (orderId, productId, quantity) VALUES (?, ?, ?)";
            String checkOrderProductExistsQuery = "SELECT COUNT(*) FROM order_products WHERE orderId = ? AND productId = ?";

            PreparedStatement orderStatement = connection.prepareStatement(insertOrderQuery);
            PreparedStatement checkOrderStatement = connection.prepareStatement(checkOrderExistsQuery);
            PreparedStatement orderProductStatement = connection.prepareStatement(insertOrderProductQuery);
            PreparedStatement checkOrderProductStatement = connection.prepareStatement(checkOrderProductExistsQuery);

            for (JsonNode order : ordersNode) {
                
                int orderId = order.get("orderId").asInt();
                checkOrderStatement.setInt(1, orderId);
                ResultSet rs = checkOrderStatement.executeQuery();
                
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.printf("Orden con ID %d ya existe. Se omite la inserción.%n", orderId);
                    continue; // Saltar a la siguiente orden
                }

                orderStatement.setInt(1, orderId);
                orderStatement.setInt(2, order.get("userId").asInt());
                orderStatement.setDouble(3, order.get("totalPrice").asDouble());
                orderStatement.setDate(4, java.sql.Date.valueOf(order.get("orderDate").asText()));
                orderStatement.executeUpdate();

                JsonNode productsInOrder = order.get("products");
                
                for (JsonNode product : productsInOrder) {
                    
                    int productId = product.get("productId").asInt();
                    checkOrderProductStatement.setInt(1, orderId);
                    checkOrderProductStatement.setInt(2, productId);
                    rs = checkOrderProductStatement.executeQuery();
                    
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.printf("Producto con ID %d ya está asociado a la orden %d. Se omite la inserción.%n", productId, orderId);
                        continue; // Saltar al siguiente producto en la orden
                    }

                    orderProductStatement.setInt(1, orderId);
                    orderProductStatement.setInt(2, productId);
                    orderProductStatement.setInt(3, product.get("quantity").asInt());
                    orderProductStatement.executeUpdate();
                }
            }
        }
    }
    
    public static void mostrarBBDD() {
        String url = "jdbc:mysql://127.0.0.1:3306/PruebaBBDDJson"; // Cambia "MiBaseDeDatos" por el nombre de tu base
        String username = "root"; // Cambia "root" si tienes otro usuario
        String password = ""; // Cambia "tu_contraseña" por tu contraseña

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {

            // Mostrar información de la tabla "users"
            System.out.println("Tabla: users");
            ResultSet usersResult = statement.executeQuery("SELECT * FROM users");
            while (usersResult.next()) {
                int id = usersResult.getInt("id");
                String name = usersResult.getString("name");
                String email = usersResult.getString("email");
                boolean isActive = usersResult.getBoolean("isActive");
                System.out.printf("ID: %d, Name: %s, Email: %s, Active: %b%n", id, name, email, isActive);
            }
            System.out.println();

            // Mostrar información de la tabla "products"
            System.out.println("Tabla: products");
            ResultSet productsResult = statement.executeQuery("SELECT * FROM products");
            while (productsResult.next()) {
                int id = productsResult.getInt("id");
                String name = productsResult.getString("name");
                String category = productsResult.getString("category");
                double price = productsResult.getDouble("price");
                System.out.printf("ID: %d, Name: %s, Category: %s, Price: %.2f%n", id, name, category, price);
            }
            System.out.println();

            // Mostrar información de la tabla "orders"
            System.out.println("Tabla: orders");
            ResultSet ordersResult = statement.executeQuery("SELECT * FROM orders");
            while (ordersResult.next()) {
                int orderId = ordersResult.getInt("orderId");
                int userId = ordersResult.getInt("userId");
                double totalPrice = ordersResult.getDouble("totalPrice");
                String orderDate = ordersResult.getDate("orderDate").toString();
                System.out.printf("Order ID: %d, User ID: %d, Total Price: %.2f, Order Date: %s%n",
                        orderId, userId, totalPrice, orderDate);
            }
            System.out.println();

            // Mostrar información de la tabla "order_products"
            System.out.println("Tabla: order_products");
            ResultSet orderProductsResult = statement.executeQuery("SELECT * FROM order_products");
            while (orderProductsResult.next()) {
                int orderId = orderProductsResult.getInt("orderId");
                int productId = orderProductsResult.getInt("productId");
                int quantity = orderProductsResult.getInt("quantity");
                System.out.printf("Order ID: %d, Product ID: %d, Quantity: %d%n",
                        orderId, productId, quantity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
