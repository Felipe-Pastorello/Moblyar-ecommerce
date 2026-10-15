package ecommerce.service;

import ecommerce.entity.Carrinho;
import ecommerce.entity.Endereco;
import ecommerce.entity.ItemCarrinho;
import ecommerce.entity.ItemPedido;
import ecommerce.entity.Pedidos;
import ecommerce.entity.StatusPedido;
import ecommerce.entity.Usuario;
import ecommerce.repository.PedidoRepository;
import ecommerce.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarrinhoService carrinhoService;
    private final AsaasService asaasService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            CarrinhoService carrinhoService,
            AsaasService asaasService) {

        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.carrinhoService = carrinhoService;
        this.asaasService = asaasService;
    }

    @Transactional
    public Pedidos criarPedido(String email, Long enderecoId) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );

        Carrinho carrinho =
                carrinhoService.buscarCarrinho(email);

        if (carrinho.getItens() == null ||
                carrinho.getItens().isEmpty()) {

            throw new RuntimeException(
                    "Não é possível criar um pedido com o carrinho vazio."
            );
        }

        Endereco endereco = usuario.getEnderecos()
                .stream()
                .filter(e -> e.getId().equals(enderecoId))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Endereço não pertence ao usuário."
                        )
                );

        Pedidos pedido = new Pedidos();

        pedido.setUsuario(usuario);
        pedido.setEndereco(endereco);
        pedido.setData(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setItems(new ArrayList<>());

        double total = 0.0;

        for (ItemCarrinho itemCarrinho : carrinho.getItens()) {

            ItemPedido itemPedido = new ItemPedido();

            itemPedido.setPedidos(pedido);
            itemPedido.setProduto(itemCarrinho.getProduto());
            itemPedido.setQuantidade(itemCarrinho.getQuantidade());

            double preco = itemCarrinho.getProduto().getPreco();

            itemPedido.setPreco(preco);

            total += preco * itemCarrinho.getQuantidade();

            pedido.getItems().add(itemPedido);
        }

        pedido.setTotal(total);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public String obterLinkPagamento(Long pedidoId, String email) {

        Pedidos pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pedido não encontrado."
                ));

        if (!pedido.getUsuario().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (pedido.getAsaasCheckoutId() != null
                && !pedido.getAsaasCheckoutId().isBlank()) {
            return "https://sandbox.asaas.com/checkoutSession/show?id="
                    + pedido.getAsaasCheckoutId();
        }

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalStateException(
                    "Não é possível iniciar o pagamento deste pedido."
            );
        }

        String checkoutId = asaasService.criarCheckout(pedido);

        pedido.setAsaasCheckoutId(checkoutId);
        pedidoRepository.save(pedido);

        return "https://sandbox.asaas.com/checkoutSession/show?id=" + checkoutId;
    }

    @Transactional(readOnly = true)
    public List<Pedidos> buscarPedidosPorUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );

        return pedidoRepository.findByUsuarioOrderByDataDesc(usuario);
    }
}