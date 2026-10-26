package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public ClienteController(ClienteRepository clienteRepository, PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer editar, Model model) {
        Cliente c = editar == null ? null : clienteRepository.findById(editar).orElse(null);
        model.addAttribute("cliente", c != null ? c : new Cliente());
        model.addAttribute("clientes", clienteRepository.findAll());
        return "admin/clientes";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente, RedirectAttributes ra) {
        if (clienteRepository.existsByCorreoIgnoreCaseAndIdNot(cliente.getCorreo(), cliente.getId())) {
            ra.addFlashAttribute("error", "Ya existe un cliente con ese correo");
            return "redirect:/admin/clientes";
        }
        clienteRepository.save(cliente);
        ra.addFlashAttribute("ok", "Cliente guardado");
        return "redirect:/admin/clientes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes ra) {
        if (pedidoRepository.existsByClienteId(id)) {
            ra.addFlashAttribute("error", "No se puede eliminar: el cliente tiene pedidos");
        } else {
            clienteRepository.deleteById(id);
            ra.addFlashAttribute("ok", "Cliente eliminado");
        }
        return "redirect:/admin/clientes";
    }
}
