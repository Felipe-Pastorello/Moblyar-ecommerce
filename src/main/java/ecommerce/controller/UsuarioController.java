package ecommerce.controller;

import ecommerce.entity.Usuario;
import ecommerce.service.UsuarioService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@Controller
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }


    // =========================
    // TELA DE LOGIN
    // =========================

    @GetMapping("/login")
    public String login() {

        return "usuario/login";
    }


    // =========================
    // TELA DE CADASTRO
    // =========================

    @GetMapping("/cadastro")
    public String cadastro(Model model) {

        model.addAttribute("usuario", new Usuario());

        return "usuario/cadastro";
    }


    // =========================
    // REALIZAR CADASTRO
    // =========================

    @PostMapping("/cadastro")
    public String cadastrar(
            @ModelAttribute Usuario usuario,
            @RequestParam String confirmarSenha,
            Model model) {

        if (!usuario.getSenha().equals(confirmarSenha)) {

            model.addAttribute(
                    "erro",
                    "As senhas não coincidem."
            );

            return "usuario/cadastro";
        }

        try {

            service.cadastrar(usuario);

            return "redirect:/login?cadastro=sucesso";

        } catch (RuntimeException e) {

            model.addAttribute(
                    "erro",
                    e.getMessage()
            );

            return "usuario/cadastro";
        }
    }

    @GetMapping("/perfil")
    public String perfil(
            Authentication authentication,
            Model model) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {

            return "redirect:/login";
        }

        Usuario usuario = service.buscarPorEmail(
                authentication.getName()
        );

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);

        return "usuario/perfil";
    }
}