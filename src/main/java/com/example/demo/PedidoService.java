package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class PedidoService {

    private final Datos datos;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final ZonaRepository zonaRepository;
    private final RepartidorRepository repartidorRepository;

    public PedidoService(Datos datos, PedidoRepository pedidoRepository, ProductoRepository productoRepository,
                         ClienteRepository clienteRepository, ZonaRepository zonaRepository,
                         RepartidorRepository repartidorRepository) {
        this.datos = datos;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.zonaRepository = zonaRepository;
        this.repartidorRepository = repartidorRepository;
    }

    public Pedido crear(String nombre, String correo, String telefono, String direccion,
                        String fechaNacimiento, int productoId, int cantidad, int zonaId, String mensaje) {
        Producto producto = productoRepository.findById(productoId).orElse(null);
        Zona zona = zonaRepository.findById(zonaId).orElse(null);
        if (producto == null || zona == null) {
            throw new IllegalArgumentException("Producto o zona no válidos");
        }
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }
        if (cantidad > producto.getStock()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + producto.getStock());
        }

        Cliente cliente = clienteRepository.findByCorreoIgnoreCase(correo).orElse(null);
        long pedidosTotales = 0;   // todos los pedidos del cliente
        long compras = 0;          // pedidos no cancelados
        long pendientes = 0;       // pedidos pendientes
        if (cliente != null) {
            int id = cliente.getId();
            pedidosTotales = pedidoRepository.countByClienteId(id);
            compras = pedidoRepository.countByClienteIdAndEstadoNot(id, "Cancelado");
            pendientes = pedidoRepository.countByClienteIdAndEstado(id, "Pendiente");
        }

        if (pendientes >= 3) {
            throw new IllegalArgumentException("Ya tienes 3 pedidos pendientes. Espera a que se entreguen para pedir más");
        }

        List<String> beneficios = new ArrayList<>();
        double subtotal = Datos.redondear(producto.getPrecio() * cantidad);

        double descuentoPromo = 0;
        Promocion promo = datos.promoActiva(producto.getId());
        if (promo != null) {
            descuentoPromo = Datos.redondear(subtotal * promo.getPorcentaje() / 100.0);
            beneficios.add("Promoción del producto (" + promo.getPorcentaje() + "%)");
        }

        String cumple = (fechaNacimiento == null || fechaNacimiento.isBlank())
                ? (cliente == null ? "" : cliente.getFechaNacimiento())
                : fechaNacimiento;

        int extra = 0;
        String motivo = "";

        if (compras > 5) {
            extra = 10;
            motivo = "Cliente frecuente (10%)";
        }
        if (esCumpleanos(cumple) && 15 > extra) {
            extra = 15;
            motivo = "Feliz cumpleaños (15%)";
        }
        if (cantidad >= 10 && 12 > extra) {
            extra = 12;
            motivo = "Compra por volumen (12%)";
        }

        double descuentoExtra = Datos.redondear((subtotal - descuentoPromo) * extra / 100.0);
        if (extra > 0) {
            beneficios.add(motivo);
        }

        double envio = zona.getCosto();
        if (pedidosTotales == 0) {
            envio = 0;
            beneficios.add("Delivery gratis por primera compra");
        }

        if (cliente == null) {
            cliente = clienteRepository.save(new Cliente(nombre, correo, telefono, direccion, cumple));
        } else if (cumple != null && !cumple.isBlank()) {
            cliente.setFechaNacimiento(cumple);
            cliente = clienteRepository.save(cliente);
        }

        double descuento = Datos.redondear(descuentoPromo + descuentoExtra);
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setProducto(producto);
        pedido.setZona(zona);
        pedido.setCantidad(cantidad);
        pedido.setMensaje(mensaje);
        pedido.setSubtotal(subtotal);
        pedido.setDescuento(descuento);
        pedido.setEnvio(envio);
        pedido.setTotal(Datos.redondear(subtotal - descuento + envio));
        pedido.setEstado("Pendiente");
        pedido.setBeneficios(beneficios.isEmpty() ? "-" : String.join("; ", beneficios));
        pedido.setRepartidor(repartidorRepository.findAll().stream()
                .min(Comparator.comparingLong(r -> pedidoRepository.countByRepartidorIdAndEstadoIn(
                        r.getId(), List.of("Pendiente", "En camino"))))
                .orElse(null));

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
        return pedidoRepository.save(pedido);
    }

    private boolean esCumpleanos(String fecha) {
        if (fecha == null || fecha.length() != 10) return false;
        String hoy = LocalDate.now().toString();
        return fecha.substring(5).equals(hoy.substring(5));
    }

    public void avanzar(int id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) return;
        if (pedido.getEstado().equals("Pendiente")) {
            pedido.setEstado("En camino");
        } else if (pedido.getEstado().equals("En camino")) {
            pedido.setEstado("Entregado");
        } else {
            throw new IllegalArgumentException("El pedido ya está " + pedido.getEstado());
        }
        pedidoRepository.save(pedido);
    }

    public void cancelar(int id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) return;
        if (!pedido.getEstado().equals("Pendiente")) {
            throw new IllegalArgumentException("Solo se pueden cancelar pedidos pendientes");
        }
        pedido.setEstado("Cancelado");
        Producto producto = pedido.getProducto();
        producto.setStock(producto.getStock() + pedido.getCantidad());
        productoRepository.save(producto);
        pedidoRepository.save(pedido);
    }
}
