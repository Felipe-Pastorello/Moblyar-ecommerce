package ecommerce.repository;

import ecommerce.entity.Carrinho;
import ecommerce.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    Optional<Carrinho> findByUsuario(Usuario usuario);
}