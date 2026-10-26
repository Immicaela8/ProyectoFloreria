package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "zona")
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "costo", nullable = false)
    private double costo;

    public Zona() {}

    public Zona(String nombre, double costo) {
        this.nombre = nombre;
        this.costo = costo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }
}
