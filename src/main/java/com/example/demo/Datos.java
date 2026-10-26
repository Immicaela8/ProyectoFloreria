package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Datos {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final PromocionRepository promocionRepository;
    private final PedidoRepository pedidoRepository;

    public Datos(CategoriaRepository categoriaRepository, ProductoRepository productoRepository,
                 ClienteRepository clienteRepository, PromocionRepository promocionRepository,
                 PedidoRepository pedidoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.promocionRepository = promocionRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public Promocion promoActiva(int productoId) {
        return promocionRepository.findFirstByProductoIdAndActivaTrue(productoId).orElse(null);
    }

    public double precioFinal(Producto p) {
        Promocion promo = promoActiva(p.getId());
        if (promo == null) return p.getPrecio();
        return redondear(p.getPrecio() * (100 - promo.getPorcentaje()) / 100.0);
    }

    public String nombreCategoria(int id) {
        return categoriaRepository.findById(id).map(Categoria::getNombre).orElse("-");
    }

    public String nombreProducto(int id) {
        return productoRepository.findById(id).map(Producto::getNombre).orElse("-");
    }

    public String nombreCliente(int id) {
        return clienteRepository.findById(id).map(Cliente::getNombre).orElse("-");
    }

    public long compras(int clienteId) {
        return pedidoRepository.countByClienteIdAndEstadoNot(clienteId, "Cancelado");
    }

    public String soles(double valor) {
        return String.format(Locale.US, "S/ %.2f", valor);
    }

    public static double redondear(double valor) {
        return Math.round(valor * 100) / 100.0;
    }
}
