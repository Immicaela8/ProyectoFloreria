package com.example.demo.controller;

import com.example.demo.PedidoService;
import com.example.demo.model.Pedido;
import com.example.demo.repository.PedidoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;

    public PedidoController(PedidoRepository pedidoRepository, PedidoService pedidoService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAll());
        return "admin/pedidos";
    }

    @PostMapping("/avanzar/{id}")
    public String avanzar(@PathVariable int id, RedirectAttributes ra) {
        try {
            pedidoService.avanzar(id);
            ra.addFlashAttribute("ok", "Estado del pedido actualizado");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable int id, RedirectAttributes ra) {
        try {
            pedidoService.cancelar(id);
            ra.addFlashAttribute("ok", "Pedido cancelado y stock devuelto");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes ra) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null && pedido.getEstado().equals("Cancelado")) {
            pedidoRepository.deleteById(id);
            ra.addFlashAttribute("ok", "Pedido eliminado");
        } else {
            ra.addFlashAttribute("error", "Solo se pueden eliminar pedidos cancelados");
        }
        return "redirect:/admin/pedidos";
    }
}
