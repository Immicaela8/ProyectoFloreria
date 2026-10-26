package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "promocion")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "porcentaje", nullable = false)
    private int porcentaje;

    @Column(name = "activa", nullable = false)
    private boolean activa;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    public Promocion() {}

    public Promocion(Producto producto, int porcentaje, boolean activa) {
        this.producto = producto;
        this.porcentaje = porcentaje;
        this.activa = activa;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPorcentaje() { return porcentaje; }
    public void setPorcentaje(int porcentaje) { this.porcentaje = porcentaje; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}
