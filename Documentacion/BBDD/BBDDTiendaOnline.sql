CREATE DATABASE IF NOT EXISTS TiendaOnline;
USE TiendaOnline;
drop database TiendaOnline;

-- Tabla de tienda

CREATE TABLE IF NOT EXISTS tienda (
    idTienda INT AUTO_INCREMENT PRIMARY KEY,
    nombreTienda VARCHAR(255) NOT NULL
);

drop table tienda;

-- Tabla de categorías

CREATE TABLE IF NOT EXISTS categorias (
    idCategoria INT PRIMARY KEY,
    nombreCategoria VARCHAR(255) NOT NULL
);

drop table categorias;

-- Tabla de productos

CREATE TABLE IF NOT EXISTS productos (
    idProducto INT PRIMARY KEY,
    nombreProducto VARCHAR(255) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    descripcion VARCHAR (255) NOT NULL,
    caracteristicas VARCHAR (255) NOT NULL, 
    inventario INT NOT NULL,
    idCategoria INT NOT NULL,
    FOREIGN KEY (idCategoria) REFERENCES categorias(idCategoria)
);

drop table productos;

-- Tabla de características de productos (opcional)

/*CREATE TABLE IF NOT EXISTS caracteristicas_producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    producto_id INT,
    clave VARCHAR(255),
    valor VARCHAR(255),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);*/ 

-- Tabla de imágenes de productos

CREATE TABLE IF NOT EXISTS imagenesProducto (
    idImagen INT AUTO_INCREMENT PRIMARY KEY,
    idProducto INT,
    imagenUrl VARCHAR(255),
    FOREIGN KEY (idProducto) REFERENCES productos(idProducto)
);

drop table imagenesProducto;

-- Tabla de usuarios

CREATE TABLE IF NOT EXISTS usuarios (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    nombreUsuario VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

drop table usuarios;

-- Tabla de direcciones de usuarios

CREATE TABLE IF NOT EXISTS direcciones (
    idDireccion INT AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT,
    calle VARCHAR(255),
    numero INT,
    ciudad VARCHAR(255),
    pais VARCHAR(255),
    FOREIGN KEY (idUsuario) REFERENCES usuarios(idUsuario)
);

drop table direccions;

-- Tabla de historial de compras

CREATE TABLE IF NOT EXISTS historialCompras (
    idHistorial INT AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT,
    idProducto INT,
    cantidad INT,
    fecha DATE,
    FOREIGN KEY (idUsuario) REFERENCES usuarios(idUsuario),
    FOREIGN KEY (idProducto) REFERENCES productos(idProducto)
);

drop table historialCompras;