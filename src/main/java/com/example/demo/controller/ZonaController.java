package com.example.demo.controller;

import com.example.demo.model.Zona;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ZonaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/zonas")
public class ZonaController {

    private final ZonaRepository zonaRepository;
    private final PedidoRepository pedidoRepository;

    public ZonaController(ZonaRepository zonaRepository, PedidoRepository pedidoRepository) {
        this.zonaRepository = zonaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer editar, Model model) {
        Zona z = editar == null ? null : zonaRepository.findById(editar).orElse(null);
        model.addAttribute("zona", z != null ? z : new Zona());
        model.addAttribute("zonas", zonaRepository.findAll());
        return "admin/zonas";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Zona zona, RedirectAttributes ra) {
        if (zona.getCosto() < 0) {
            ra.addFlashAttribute("error", "El costo de delivery no puede ser negativo");
            return "redirect:/admin/zonas";
        }
        zonaRepository.save(zona);
        ra.addFlashAttribute("ok", "Zona guardada");
        return "redirect:/admin/zonas";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes ra) {
        if (pedidoRepository.existsByZonaId(id)) {
            ra.addFlashAttribute("error", "No se puede eliminar: la zona tiene pedidos");
        } else {
            zonaRepository.deleteById(id);
            ra.addFlashAttribute("ok", "Zona eliminada");
        }
        return "redirect:/admin/zonas";
    }
}
