package ecommerce.repository;
import ecommerce.entity.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categorias, Long> {
    Optional<Categorias> findByNome(String nome);
    boolean existsByNome(String nome);
    boolean existsByNomeAndIdNot(
            String nome,
            Long id
    );
}
