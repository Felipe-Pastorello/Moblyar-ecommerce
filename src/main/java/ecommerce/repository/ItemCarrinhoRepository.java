package ecommerce.repository;

import ecommerce.entity.Carrinho;
import ecommerce.entity.ItemCarrinho;
import ecommerce.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {

    Optional<ItemCarrinho> findByCarrinhoAndProduto(
            Carrinho carrinho,
            Produto produto
    );

    Optional<ItemCarrinho> findByIdAndCarrinho(
            Long id,
            Carrinho carrinho
    );
}