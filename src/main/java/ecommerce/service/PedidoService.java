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

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarrinhoService carrinhoService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            CarrinhoService carrinhoService) {

        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.carrinhoService = carrinhoService;
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
}