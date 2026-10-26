package com.example.demo.controller;

import com.example.demo.model.Categoria;
import com.example.demo.model.Producto;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.PromocionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final PedidoRepository pedidoRepository;
    private final PromocionRepository promocionRepository;

    public ProductoController(ProductoRepository productoRepository, CategoriaRepository categoriaRepository,
                              PedidoRepository pedidoRepository, PromocionRepository promocionRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.pedidoRepository = pedidoRepository;
        this.promocionRepository = promocionRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer editar, Model model) {
        Producto p = editar == null ? null : productoRepository.findById(editar).orElse(null);
        model.addAttribute("producto", p != null ? p : new Producto());
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "admin/productos";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto, @RequestParam int categoriaId, RedirectAttributes ra) {
        if (producto.getPrecio() <= 0 || producto.getStock() < 0) {
            ra.addFlashAttribute("error", "El precio debe ser mayor a 0 y el stock no puede ser negativo");
            return "redirect:/admin/productos";
        }
        Categoria categoria = categoriaRepository.findById(categoriaId).orElse(null);
        if (categoria == null) {
            ra.addFlashAttribute("error", "La categoría no existe");
            return "redirect:/admin/productos";
        }
        producto.setCategoria(categoria);
        productoRepository.save(producto);
        ra.addFlashAttribute("ok", "Producto guardado");
        return "redirect:/admin/productos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes ra) {
        if (pedidoRepository.existsByProductoId(id)) {
            ra.addFlashAttribute("error", "No se puede eliminar: el producto tiene pedidos");
        } else {
            promocionRepository.deleteAll(promocionRepository.findByProductoId(id));
            productoRepository.deleteById(id);
            ra.addFlashAttribute("ok", "Producto eliminado");
        }
        return "redirect:/admin/productos";
    }
}
