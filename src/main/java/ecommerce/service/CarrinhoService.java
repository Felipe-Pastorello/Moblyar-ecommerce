package ecommerce.service;

import ecommerce.entity.Carrinho;
import ecommerce.entity.ItemCarrinho;
import ecommerce.entity.Produto;
import ecommerce.entity.Usuario;
import ecommerce.repository.CarrinhoRepository;
import ecommerce.repository.ItemCarrinhoRepository;
import ecommerce.repository.ProdutoRepository;
import ecommerce.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, ItemCarrinhoRepository itemCarrinhoRepository, ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {

        this.carrinhoRepository = carrinhoRepository;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Carrinho buscarOuCriarCarrinho(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );

        return carrinhoRepository.findByUsuario(usuario)
                .orElseGet(() -> {

                    Carrinho carrinho = new Carrinho();
                    carrinho.setUsuario(usuario);

                    return carrinhoRepository.save(carrinho);
                });
    }

    @Transactional
    public void adicionarProduto(String email, Long produtoId, Integer quantidade) {

        Carrinho carrinho = buscarOuCriarCarrinho(email);

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() ->
                        new RuntimeException("Produto não encontrado.")
                );

        ItemCarrinho item = itemCarrinhoRepository
                .findByCarrinhoAndProduto(carrinho, produto)
                .orElse(null);

        if (item == null) {

            item = new ItemCarrinho();
            item.setCarrinho(carrinho);
            item.setProduto(produto);
            item.setQuantidade(quantidade);

        } else {

            item.setQuantidade(
                    item.getQuantidade() + quantidade
            );
        }

        itemCarrinhoRepository.save(item);
    }

    @Transactional(readOnly = true)
    public Carrinho buscarCarrinho(String email) {
        return buscarOuCriarCarrinho(email);
    }

    @Transactional
    public void alterarQuantidade(String email, Long itemId, Integer quantidade) {

        if (quantidade == null || quantidade < 1) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        Carrinho carrinho = buscarOuCriarCarrinho(email);

        ItemCarrinho item = itemCarrinhoRepository
                .findByIdAndCarrinho(itemId, carrinho)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item não encontrado no carrinho."
                        )
                );

        item.setQuantidade(quantidade);

        itemCarrinhoRepository.save(item);
    }

    @Transactional
    public void removerItem(String email, Long itemId) {

        Carrinho carrinho = buscarOuCriarCarrinho(email);

        ItemCarrinho item = itemCarrinhoRepository
                .findByIdAndCarrinho(itemId, carrinho)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item não encontrado no carrinho."
                        )
                );

        itemCarrinhoRepository.delete(item);
    }

    @Transactional
    public void esvaziarCarrinho(String email) {

        Carrinho carrinho = buscarOuCriarCarrinho(email);

        itemCarrinhoRepository.deleteAll(carrinho.getItens());
    }
}