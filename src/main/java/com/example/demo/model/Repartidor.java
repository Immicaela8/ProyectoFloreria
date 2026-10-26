package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "repartidor")
public class Repartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "telefono", length = 30)
    private String telefono;

    public Repartidor() {}

    public Repartidor(String nombre, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
