package com.example.demo.controller;

import com.example.demo.Datos;
import com.example.demo.PedidoService;
import com.example.demo.model.Pedido;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.PromocionRepository;
import com.example.demo.repository.ZonaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TiendaController {

    private final Datos datos;
    private final PedidoService pedidoService;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final PromocionRepository promocionRepository;
    private final ZonaRepository zonaRepository;

    public TiendaController(Datos datos, PedidoService pedidoService, CategoriaRepository categoriaRepository,
                            ProductoRepository productoRepository, PromocionRepository promocionRepository,
                            ZonaRepository zonaRepository) {
        this.datos = datos;
        this.pedidoService = pedidoService;
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.promocionRepository = promocionRepository;
        this.zonaRepository = zonaRepository;
    }

    @GetMapping("/inicio")
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/productos")
    public String productos(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        return "productos";
    }

    @GetMapping("/promociones")
    public String promociones(Model model) {
        model.addAttribute("promos", promocionRepository.findByActivaTrue());
        return "promociones";
    }

    @GetMapping("/comprar")
    public String comprarForm(@RequestParam(defaultValue = "0") int producto, Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("zonas", zonaRepository.findAll());
        model.addAttribute("seleccionado", producto);
        return "comprar";
    }

    @PostMapping("/comprar")
    public String comprar(@RequestParam String nombre, @RequestParam String correo,
                          @RequestParam String telefono, @RequestParam String direccion,
                          @RequestParam(defaultValue = "") String fechaNacimiento,
                          @RequestParam int productoId, @RequestParam int cantidad,
                          @RequestParam int zonaId, @RequestParam(defaultValue = "") String mensaje,
                          RedirectAttributes ra) {
        try {
            Pedido pedido = pedidoService.crear(nombre, correo, telefono, direccion, fechaNacimiento,
                    productoId, cantidad, zonaId, mensaje);
            ra.addFlashAttribute("ok", "Pedido #" + pedido.getId() + " confirmado. Total a pagar: "
                    + datos.soles(pedido.getTotal())
                    + (pedido.getBeneficios().equals("-") ? "" : ". Beneficios: " + pedido.getBeneficios()));
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/comprar";
    }
}
