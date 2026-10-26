package com.example.demo.controller;

import com.example.demo.model.Categoria;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaController(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer editar, Model model) {
        Categoria c = editar == null ? null : categoriaRepository.findById(editar).orElse(null);
        model.addAttribute("categoria", c != null ? c : new Categoria());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "admin/categorias";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria, RedirectAttributes ra) {
        Categoria destino = categoria.getId() == 0
                ? categoria
                : categoriaRepository.findById(categoria.getId()).orElse(categoria);
        destino.setNombre(categoria.getNombre());
        categoriaRepository.save(destino);
        ra.addFlashAttribute("ok", "Categoría guardada");
        return "redirect:/admin/categorias";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes ra) {
        if (productoRepository.existsByCategoriaId(id)) {
            ra.addFlashAttribute("error", "No se puede eliminar: la categoría tiene productos");
        } else {
            categoriaRepository.deleteById(id);
            ra.addFlashAttribute("ok", "Categoría eliminada");
        }
        return "redirect:/admin/categorias";
    }
}
