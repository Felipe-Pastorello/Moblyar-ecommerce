package ecommerce.controller;

import ecommerce.entity.Endereco;
import ecommerce.entity.Usuario;
import ecommerce.service.EnderecoService;
import ecommerce.service.PedidoService;
import ecommerce.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    private final UsuarioService service;
    private final PedidoService pedidoService;
    private final EnderecoService enderecoService;

    public UsuarioController(
            UsuarioService service,
            PedidoService pedidoService,
            EnderecoService enderecoService) {
        this.service = service;
        this.pedidoService = pedidoService;
        this.enderecoService = enderecoService;
    }

    @GetMapping("/login")
    public String login() {
        return "usuario/login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @ModelAttribute Usuario usuario,
            @RequestParam String confirmarSenha,
            Model model) {

        if (usuario.getSenha() == null
                || !usuario.getSenha().equals(confirmarSenha)) {
            model.addAttribute("erro", "As senhas não coincidem.");
            return "usuario/cadastro";
        }

        try {
            service.cadastrar(usuario);
            return "redirect:/login?cadastro=sucesso";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "usuario/cadastro";
        }
    }

    @GetMapping("/perfil")
    public String perfil(Authentication authentication, Model model) {
        Usuario usuario = usuarioAutenticado(authentication);

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute(
                "pedidos",
                pedidoService.buscarPedidosPorUsuario(usuario.getEmail())
        );
        model.addAttribute(
                "enderecos",
                enderecoService.listar(usuario.getEmail())
        );

        return "usuario/perfil";
    }

    @PostMapping("/perfil/editar")
    public String editarPerfil(
            Authentication authentication,
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam(required = false, defaultValue = "") String senhaAtual,
            @RequestParam(required = false, defaultValue = "") String novaSenha,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioAutenticado(authentication);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            boolean emailAlterado = service.atualizarDados(
                    usuario.getEmail(),
                    nome,
                    email,
                    senhaAtual,
                    novaSenha
            );

            if (emailAlterado) {
                HttpSession session = request.getSession(false);

                SecurityContextHolder.clearContext();

                if (session != null) {
                    session.invalidate();
                }

                return "redirect:/login?emailAlterado=true";
            }

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Dados atualizados com sucesso."
            );
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/perfil";
    }

    @GetMapping("/perfil/enderecos/novo")
    public String novoEndereco(
            Authentication authentication,
            Model model) {

        if (usuarioAutenticado(authentication) == null) {
            return "redirect:/login";
        }

        model.addAttribute("endereco", new Endereco());
        model.addAttribute("modoEdicao", false);

        return "usuario/endereco-form";
    }

    @PostMapping("/perfil/enderecos")
    public String criarEndereco(
            Authentication authentication,
            @ModelAttribute Endereco endereco,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioAutenticado(authentication);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            enderecoService.criar(usuario.getEmail(), endereco);
            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Endereço cadastrado com sucesso."
            );
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/perfil";
    }

    @GetMapping("/perfil/enderecos/{id}/editar")
    public String editarEndereco(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioAutenticado(authentication);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            model.addAttribute(
                    "endereco",
                    enderecoService.buscarDaConta(id, usuario.getEmail())
            );
            model.addAttribute("modoEdicao", true);

            return "usuario/endereco-form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/perfil";
        }
    }

    @PostMapping("/perfil/enderecos/{id}")
    public String atualizarEndereco(
            @PathVariable Long id,
            Authentication authentication,
            @ModelAttribute Endereco endereco,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioAutenticado(authentication);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            enderecoService.atualizar(id, usuario.getEmail(), endereco);
            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Endereço atualizado com sucesso."
            );
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/perfil";
    }

    @PostMapping("/perfil/enderecos/{id}/excluir")
    public String excluirEndereco(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioAutenticado(authentication);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            enderecoService.excluir(id, usuario.getEmail());
            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Endereço removido com sucesso."
            );
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/perfil";
    }

    private Usuario usuarioAutenticado(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        return service.buscarPorEmail(authentication.getName());
    }
}