package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.repository.ClienteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final ClienteRepository clienteRepository;

    public LoginController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String ingresar(@RequestParam String correo, @RequestParam String password) {
        if (correo.equalsIgnoreCase("admin@floreria.com")) {
            return "redirect:/admin";
        }
        return "redirect:/inicio";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Cliente cliente, RedirectAttributes ra) {
        boolean correoRepetido = cliente.getCorreo().equalsIgnoreCase("admin@floreria.com")
                || clienteRepository.existsByCorreoIgnoreCase(cliente.getCorreo());
        if (correoRepetido) {
            ra.addFlashAttribute("error", "Ya existe una cuenta con ese correo");
            return "redirect:/";
        }
        clienteRepository.save(cliente);
        ra.addFlashAttribute("ok", "Registro exitoso. Ya puedes ingresar con tu correo");
        return "redirect:/";
    }
}
