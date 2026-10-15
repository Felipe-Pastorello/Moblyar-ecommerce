package ecommerce.controller;

import ecommerce.entity.Carrinho;
import ecommerce.entity.Pedidos;
import ecommerce.entity.Usuario;
import ecommerce.service.CarrinhoService;
import ecommerce.service.PedidoService;
import ecommerce.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ecommerce.entity.Pedidos;
import ecommerce.repository.PedidoRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CheckoutController {

    private final CarrinhoService carrinhoService;
    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    public CheckoutController(CarrinhoService carrinhoService, UsuarioService usuarioService, PedidoService pedidoService, PedidoRepository pedidoRepository) {

        this.carrinhoService = carrinhoService;
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/checkout")
    public String checkout(Authentication authentication, Model model) {

        String email = authentication.getName();

        Carrinho carrinho = carrinhoService.buscarCarrinho(email);

        if (carrinho.getItens() == null ||
                carrinho.getItens().isEmpty()) {
            return "redirect:/";
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);

        double total = carrinho.getItens()
                .stream()
                .mapToDouble(item ->
                        item.getProduto().getPreco()
                                * item.getQuantidade())
                .sum();

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("usuario", usuario);
        model.addAttribute("total", total);

        return "checkout/checkout";
    }

    @PostMapping("/checkout/finalizar")
    public String finalizar(@RequestParam Long enderecoId, Authentication authentication, RedirectAttributes redirectAttributes) {

        try {
            Pedidos pedido = pedidoService.criarPedido(
                    authentication.getName(),
                    enderecoId
            );

            return "redirect:/checkout/pagamento/" + pedido.getId();

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(
                    "erroCheckout",
                    e.getMessage()
            );

            return "redirect:/checkout";
        }
    }

    @GetMapping("/checkout/pagamento/{id}")
    public String pagamento(@PathVariable Long id, Authentication authentication, Model model) {

        Pedidos pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "Pedido não encontrado."
                        )
                );

        // Impede que um usuário consulte o pedido de outra pessoa.
        if (!pedido.getUsuario().getEmail()
                .equals(authentication.getName())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN
            );
        }

        model.addAttribute("pedido", pedido);

        return "checkout/pagamento";
    }

    @PostMapping("/checkout/pagamento/{id}/iniciar")
    public String iniciarPagamento(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {

        try {
            String link = pedidoService.obterLinkPagamento(
                    id, authentication.getName());

            return "redirect:" + link;

        } catch (RuntimeException e) {
            logger.error("Erro ao iniciar pagamento do pedido {}", id, e);

            redirectAttributes.addFlashAttribute(
                    "erroCheckout",
                    "Não foi possível iniciar o pagamento: " + e.getMessage());

            return "redirect:/checkout/pagamento/" + id;
        }
    }
}