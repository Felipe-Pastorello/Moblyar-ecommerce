package ecommerce.controller;

import ecommerce.entity.Carrinho;
import ecommerce.entity.Usuario;
import ecommerce.service.CarrinhoService;
import ecommerce.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CheckoutController {

    private final CarrinhoService carrinhoService;
    private final UsuarioService usuarioService;

    public CheckoutController(
            CarrinhoService carrinhoService,
            UsuarioService usuarioService) {

        this.carrinhoService = carrinhoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/checkout")
    public String checkout(Authentication authentication, Model model) {

        String email = authentication.getName();

        Carrinho carrinho =
                carrinhoService.buscarCarrinho(email);

        if (carrinho.getItens() == null ||
                carrinho.getItens().isEmpty()) {

            return "redirect:/";
        }

        Usuario usuario =
                usuarioService.buscarPorEmail(email);

        double total = carrinho.getItens()
                .stream()
                .mapToDouble(item ->
                        item.getProduto().getPreco()
                                * item.getQuantidade()
                )
                .sum();

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("usuario", usuario);
        model.addAttribute("total", total);

        return "checkout/checkout";
    }
}