package ecommerce.repository;
import ecommerce.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    @Query("""
        SELECT DISTINCT p
        FROM Produto p
        LEFT JOIN p.categorias c
        WHERE p.status = 'ATIVO'
        AND (
            :termo IS NULL
            OR :termo = ''
            OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
        )
        AND (
            :categoriaId IS NULL
            OR c.id = :categoriaId
        )
        AND (
            :precoMin IS NULL
            OR p.preco >= :precoMin
        )
        AND (
            :precoMax IS NULL
            OR p.preco <= :precoMax
        )
        AND (
            :disponivel IS NULL
            OR :disponivel = false
            OR p.estoque > 0
        )
        """)
    Page<Produto> buscarProdutos(
            @Param("termo") String termo,
            @Param("categoriaId") Long categoriaId,
            @Param("precoMin") Double precoMin,
            @Param("precoMax") Double precoMax,
            @Param("disponivel") Boolean disponivel,
            Pageable pageable
    );
    Page<Produto> findByNomeContainingIgnoreCase(
            String nome,
            Pageable pageable
    );
}
