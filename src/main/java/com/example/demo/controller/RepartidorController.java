package com.example.demo.controller;

import com.example.demo.model.Pedido;
import com.example.demo.model.Repartidor;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.RepartidorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/repartidores")
public class RepartidorController {

    private final RepartidorRepository repartidorRepository;
    private final PedidoRepository pedidoRepository;

    public RepartidorController(RepartidorRepository repartidorRepository, PedidoRepository pedidoRepository) {
        this.repartidorRepository = repartidorRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer editar, Model model) {
        Repartidor r = editar == null ? null : repartidorRepository.findById(editar).orElse(null);
        model.addAttribute("repartidor", r != null ? r : new Repartidor());
        model.addAttribute("repartidores", repartidorRepository.findAll());
        return "admin/repartidores";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Repartidor repartidor, RedirectAttributes ra) {
        repartidorRepository.save(repartidor);
        ra.addFlashAttribute("ok", "Repartidor guardado");
        return "redirect:/admin/repartidores";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes ra) {
        List<Pedido> pedidos = pedidoRepository.findByRepartidorId(id);
        pedidos.forEach(p -> p.setRepartidor(null));
        pedidoRepository.saveAll(pedidos);
        repartidorRepository.deleteById(id);
        ra.addFlashAttribute("ok", "Repartidor eliminado");
        return "redirect:/admin/repartidores";
    }
}
