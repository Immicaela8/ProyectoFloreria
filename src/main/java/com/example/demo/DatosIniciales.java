package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ZonaRepository zonaRepository;
    private final RepartidorRepository repartidorRepository;
    private final PromocionRepository promocionRepository;
    private final PedidoService pedidoService;

    public DatosIniciales(CategoriaRepository categoriaRepository, ProductoRepository productoRepository,
                          ZonaRepository zonaRepository, RepartidorRepository repartidorRepository,
                          PromocionRepository promocionRepository, PedidoService pedidoService) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.zonaRepository = zonaRepository;
        this.repartidorRepository = repartidorRepository;
        this.promocionRepository = promocionRepository;
        this.pedidoService = pedidoService;
    }

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() > 0) return;

        Categoria flores = categoriaRepository.save(new Categoria("Flores"));
        Categoria arreglos = categoriaRepository.save(new Categoria("Arreglos por ocasión"));
        Categoria complementos = categoriaRepository.save(new Categoria("Complementos"));

        List<Producto> p = productoRepository.saveAll(List.of(
                new Producto("Rosa Roja", 15, 50, flores, "rosasRojas.jpg"),
                new Producto("Girasoles", 20, 40, flores, "girasoles.jpg"),
                new Producto("Tulipanes", 25, 30, flores, "tulipanes.jpg"),
                new Producto("Lirios", 30, 25, flores, "Lirios.jpg"),
                new Producto("Orquídeas", 25, 20, flores, "orquideas.jpg"),
                new Producto("Claveles", 15, 60, flores, "claveles.jpg"),
                new Producto("Arreglo de Boda", 120, 8, arreglos, "arregloBoda.jpg"),
                new Producto("Arreglo de Cumpleaños", 80, 10, arreglos, "arregloCumpleanos.jpg"),
                new Producto("Arreglo de Aniversario", 100, 10, arreglos, "arregloAniversario.jpeg"),
                new Producto("Arreglo de San Valentín", 115, 10, arreglos, "arregloSanValentin.jpg"),
                new Producto("Arreglo Día de la Madre", 120, 10, arreglos, "arregloDiaMadre.jpeg"),
                new Producto("Arreglo Día del Padre", 120, 10, arreglos, "arregloDiaPadre.jpg"),
                new Producto("Caja de Chocolates", 20, 30, complementos, "cajaChocolate.jpg"),
                new Producto("Peluche", 35, 4, complementos, "peluche.jpg")));

        List<Zona> z = zonaRepository.saveAll(List.of(
                new Zona("Miraflores", 8),
                new Zona("San Isidro", 8),
                new Zona("Surco", 10),
                new Zona("La Molina", 12)));

        repartidorRepository.saveAll(List.of(
                new Repartidor("Carlos Ruiz", "999111222"),
                new Repartidor("Lucía Vega", "999333444"),
                new Repartidor("Pedro Soto", "999555666")));

        promocionRepository.saveAll(List.of(
                new Promocion(p.get(0), 25, true),
                new Promocion(p.get(1), 28, true),
                new Promocion(p.get(2), 27, true),
                new Promocion(p.get(3), 10, false)));

        for (int i = 0; i < 6; i++) {
            Pedido pedido = pedidoService.crear("María Torres", "maria@correo.com", "987654321",
                    "Av. Larco 123", "", p.get(0).getId(), 1, z.get(0).getId(), "Feliz día");
            pedidoService.avanzar(pedido.getId());
            pedidoService.avanzar(pedido.getId());
        }
        pedidoService.crear("Luis Gómez", "luis@correo.com", "987111222", "Jr. Lima 456", "",
                p.get(6).getId(), 1, z.get(2).getId(), "");
        Pedido p3 = pedidoService.crear("Ana Ruiz", "ana@correo.com", "955444333", "Calle Sol 78", "",
                p.get(1).getId(), 2, z.get(1).getId(), "Gracias");
        pedidoService.avanzar(p3.getId());
    }
}
