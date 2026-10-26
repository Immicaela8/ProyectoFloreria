package com.example.demo.controller;

import com.example.demo.model.Producto;
import com.example.demo.model.Promocion;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.PromocionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/promociones")
public class PromocionController {

    private final PromocionRepository promocionRepository;
    private final ProductoRepository productoRepository;

    public PromocionController(PromocionRepository promocionRepository, ProductoRepository productoRepository) {
        this.promocionRepository = promocionRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer editar, Model model) {
        Promocion p = editar == null ? null : promocionRepository.findById(editar).orElse(null);
        model.addAttribute("promocion", p != null ? p : new Promocion());
        model.addAttribute("promociones", promocionRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        return "admin/promociones";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Promocion promocion, @RequestParam int productoId, RedirectAttributes ra) {
        if (promocion.getPorcentaje() < 1 || promocion.getPorcentaje() > 90) {
            ra.addFlashAttribute("error", "El descuento debe estar entre 1% y 90%");
            return "redirect:/admin/promociones";
        }
        Producto producto = productoRepository.findById(productoId).orElse(null);
        if (producto == null) {
            ra.addFlashAttribute("error", "El producto no existe");
            return "redirect:/admin/promociones";
        }
        promocion.setProducto(producto);
        promocionRepository.save(promocion);
        ra.addFlashAttribute("ok", "Promoción guardada");
        return "redirect:/admin/promociones";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes ra) {
        promocionRepository.deleteById(id);
        ra.addFlashAttribute("ok", "Promoción eliminada");
        return "redirect:/admin/promociones";
    }
}
