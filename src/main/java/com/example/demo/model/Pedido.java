package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_zona", nullable = false)
    private Zona zona;

    @ManyToOne
    @JoinColumn(name = "id_repartidor")
    private Repartidor repartidor;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "mensaje", length = 500)
    private String mensaje;

    @Column(name = "subtotal")
    private double subtotal;

    @Column(name = "descuento")
    private double descuento;

    @Column(name = "envio")
    private double envio;

    @Column(name = "total")
    private double total;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "beneficios", length = 500)
    private String beneficios;

    public Pedido() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Zona getZona() { return zona; }
    public void setZona(Zona zona) { this.zona = zona; }

    public Repartidor getRepartidor() { return repartidor; }
    public void setRepartidor(Repartidor repartidor) { this.repartidor = repartidor; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }

    public double getEnvio() { return envio; }
    public void setEnvio(double envio) { this.envio = envio; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getBeneficios() { return beneficios; }
    public void setBeneficios(String beneficios) { this.beneficios = beneficios; }
}
