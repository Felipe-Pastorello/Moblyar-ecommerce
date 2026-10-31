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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import ecommerce.repository.ProdutoRepository;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarrinhoService carrinhoService;
    private final AsaasService asaasService;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository, CarrinhoService carrinhoService, AsaasService asaasService, ProdutoRepository produtoRepository) {

        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.carrinhoService = carrinhoService;
        this.asaasService = asaasService;
        this.produtoRepository = produtoRepository;
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

            Long produtoId = itemCarrinho.getProduto().getId();
            int quantidade = itemCarrinho.getQuantidade();

            if (quantidade <= 0) {
                throw new IllegalArgumentException(
                        "A quantidade do produto deve ser maior que zero."
                );
            }

            int atualizado = produtoRepository.reservarEstoque(
                    produtoId,
                    quantidade
            );

            if (atualizado == 0) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "O produto \"" + itemCarrinho.getProduto().getNome()
                                + "\" não possui estoque suficiente ou está inativo."
                );
            }

            ItemPedido itemPedido = new ItemPedido();

            itemPedido.setPedidos(pedido);
            itemPedido.setProduto(itemCarrinho.getProduto());
            itemPedido.setQuantidade(quantidade);

            double preco = itemCarrinho.getProduto().getPreco();

            itemPedido.setPreco(preco);

            total += preco * quantidade;

            pedido.getItems().add(itemPedido);
        }

        pedido.setTotal(total);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public String obterLinkPagamento(
            Long pedidoId,
            String email,
            BigDecimal freteValor,
            String freteServico) {

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

        String checkoutId = asaasService.criarCheckout(
                pedido,
                freteValor,
                freteServico
        );

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

    @Transactional(readOnly = true)
    public Page<Pedidos> buscarPedidosAdmin(String busca, StatusPedido status, LocalDateTime dataInicio, LocalDateTime dataFim, Double precoMin, Double precoMax, int pagina, int tamanho) {

        String buscaLimpa = StringUtils.hasText(busca) ? busca.trim() : null;

        Long idPedido = null;

        if (buscaLimpa != null && buscaLimpa.matches("\\d+")) {
            try {
                idPedido = Long.valueOf(buscaLimpa);
            } catch (NumberFormatException ignored) {
                // A busca continua pelo nome ou e-mail.
            }
        }

        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException(
                    "A data inicial não pode ser posterior à data final."
            );
        }

        if (precoMin != null && precoMin < 0 || precoMax != null && precoMax < 0) {
            throw new IllegalArgumentException(
                    "Os preços não podem ser negativos."
            );
        }

        if (precoMin != null && precoMax != null && precoMin > precoMax) {
            throw new IllegalArgumentException(
                    "O preço mínimo não pode ser maior que o máximo."
            );
        }

        Pageable pageable = PageRequest.of(Math.max(pagina, 0), Math.max(tamanho, 1), Sort.by(Sort.Direction.DESC, "data")
        );

        if (idPedido != null) {
            Page<Pedidos> resultadoId = pedidoRepository.findAll(
                    PageRequest.of(
                            0,
                            1,
                            Sort.by(Sort.Direction.DESC, "data")
                    )
            );

            // A busca geral continua aplicando os filtros.
            // O ID será priorizado na consulta abaixo.
        }

        return pedidoRepository.buscarPedidos(buscaLimpa, idPedido, status, dataInicio, dataFim, precoMin, precoMax, pageable
        );
    }

    @Transactional
    public void atualizarStatus(Long pedidoId, StatusPedido novoStatus) {

        if (novoStatus == null) {
            throw new IllegalArgumentException(
                    "Selecione um status válido."
            );
        }

        Pedidos pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pedido não encontrado."
                ));

        StatusPedido statusAtual = pedido.getStatus();

        if (statusAtual == novoStatus) {
            return;
        }

        if (statusAtual != StatusPedido.PENDENTE) {
            throw new IllegalStateException(
                    "Não é possível alterar um pedido que já está "
                            + statusAtual + "."
            );
        }

        if (novoStatus == StatusPedido.CANCELADO
                || novoStatus == StatusPedido.EXPIRADO) {

            devolverEstoqueDoPedido(pedido);
        }

        pedido.setStatus(novoStatus);

        pedidoRepository.save(pedido);
    }

    private void devolverEstoqueDoPedido(Pedidos pedido) {

        if (pedido.isEstoqueDevolvido()) {
            return;
        }

        for (ItemPedido item : pedido.getItems()) {

            int atualizado = produtoRepository.devolverEstoque(
                    item.getProduto().getId(),
                    item.getQuantidade()
            );

            if (atualizado == 0) {
                throw new IllegalStateException(
                        "Não foi possível devolver o estoque do produto "
                                + item.getProduto().getId()
                );
            }
        }

        pedido.setEstoqueDevolvido(true);
    }
}