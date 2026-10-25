package ecommerce.repository;

import ecommerce.entity.Endereco;
import ecommerce.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    List<Endereco> findByUsuariosContaining(Usuario usuario);

    Optional<Endereco> findByIdAndUsuariosContaining(
            Long id,
            Usuario usuario
    );
}