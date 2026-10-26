package com.example.demo.controller;

import com.example.demo.Datos;
import com.example.demo.model.Pedido;
import com.example.demo.model.Producto;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.PromocionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final Datos datos;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final PromocionRepository promocionRepository;

    public DashboardController(Datos datos, ProductoRepository productoRepository, ClienteRepository clienteRepository,
                               PedidoRepository pedidoRepository, PromocionRepository promocionRepository) {
        this.datos = datos;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.promocionRepository = promocionRepository;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        List<Producto> productos = productoRepository.findAll();
        List<Pedido> pedidos = pedidoRepository.findAll();
        List<Pedido> validos = pedidos.stream()
                .filter(p -> !p.getEstado().equals("Cancelado")).toList();
        double ingresos = validos.stream().mapToDouble(Pedido::getTotal).sum();

        Map<Integer, Integer> ventas = validos.stream().collect(
                Collectors.groupingBy(p -> p.getProducto().getId(), Collectors.summingInt(Pedido::getCantidad)));
        String masVendido = ventas.entrySet().stream().max(Map.Entry.comparingByValue())
                .map(e -> datos.nombreProducto(e.getKey()) + " (" + e.getValue() + " uds)").orElse("-");

        Map<Integer, Double> gasto = validos.stream().collect(
                Collectors.groupingBy(p -> p.getCliente().getId(), Collectors.summingDouble(Pedido::getTotal)));
        String mejorCliente = gasto.entrySet().stream().max(Map.Entry.comparingByValue())
                .map(e -> datos.nombreCliente(e.getKey()) + " (" + datos.soles(e.getValue()) + ")").orElse("-");

        Map<Integer, Long> porCategoria = productos.stream().collect(
                Collectors.groupingBy(p -> p.getCategoria().getId(), Collectors.counting()));
        String categoriaTop = porCategoria.entrySet().stream().max(Map.Entry.comparingByValue())
                .map(e -> datos.nombreCategoria(e.getKey()) + " (" + e.getValue() + ")").orElse("-");

        String stockBajo = productos.stream().filter(p -> p.getStock() < 5)
                .map(p -> p.getNombre() + " (" + p.getStock() + ")").collect(Collectors.joining(", "));

        double inventario = productos.stream().mapToDouble(p -> p.getPrecio() * p.getStock()).sum();

        Map<String, String> m = new LinkedHashMap<>();
        m.put("Total de productos", String.valueOf(productos.size()));
        m.put("Total de clientes", String.valueOf(clienteRepository.count()));
        m.put("Total de pedidos", String.valueOf(pedidos.size()));
        m.put("Ingresos totales", datos.soles(ingresos));
        m.put("Ticket promedio", datos.soles(validos.isEmpty() ? 0 : ingresos / validos.size()));
        m.put("Pedidos pendientes", String.valueOf(pedidoRepository.countByEstado("Pendiente")));
        m.put("Pedidos en camino", String.valueOf(pedidoRepository.countByEstado("En camino")));
        m.put("Pedidos entregados", String.valueOf(pedidoRepository.countByEstado("Entregado")));
        m.put("Pedidos cancelados", String.valueOf(pedidoRepository.countByEstado("Cancelado")));
        m.put("Producto más vendido", masVendido);
        m.put("Cliente que más compró", mejorCliente);
        m.put("Categoría con más productos", categoriaTop);
        m.put("Productos con stock bajo (menos de 5)", stockBajo.isEmpty() ? "Ninguno" : stockBajo);
        m.put("Clientes frecuentes (más de 5 compras)", String.valueOf(clienteRepository.findAll().stream()
                .filter(c -> datos.compras(c.getId()) > 5).count()));
        m.put("Promociones activas", String.valueOf(promocionRepository.findByActivaTrue().size()));
        m.put("Valor del inventario", datos.soles(inventario));

        model.addAttribute("metricas", m);
        return "admin/dashboard";
    }
}
