package ecommerce.repository;
import ecommerce.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByStatus(String status);
    boolean existsByNome(String nome);
    @Query("""
SELECT p
FROM Produto p
WHERE p.categorias IS EMPTY""")
    List<Produto> buscarSemCategoria();
    Produto findByNome(String nome);
}
