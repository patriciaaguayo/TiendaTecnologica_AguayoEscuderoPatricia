/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendatecnologica;

/**
 *
 * @author Patricia Aguayo
 */

public class Producto {
    
    private int id;
    private String nombre;
    private double precio;
    private String descripcion;
    private String caracteristicas;
    private String imagenUrl1; 
    private String imagenUrl2; 

    // Constructor
    
    public Producto(int id, String nombre, double precio, String descripcion, 
                    String caracteristicas, String imagenUrl1, String imagenUrl2) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.caracteristicas = caracteristicas;
        this.imagenUrl1 = imagenUrl1;
        this.imagenUrl2 = imagenUrl2;
    }

    
    public int getId() { 
        return id; 
    }
    
    public String getNombre() { 
        return nombre; 
    }
    
    public double getPrecio() { 
        return precio; 
    }
    
    public String getDescripcion() { 
        return descripcion; 
    }
    
    public String getCaracteristicas() { 
        return caracteristicas; 
    }
    
    public String getImagenUrl1() { 
        return imagenUrl1; 
    }
    
    public String getImagenUrl2() { 
        return imagenUrl2; 
    }
}
