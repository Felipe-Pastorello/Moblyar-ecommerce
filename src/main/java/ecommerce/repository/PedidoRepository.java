package ecommerce.repository;

import ecommerce.entity.Pedidos;
import ecommerce.entity.StatusPedido;
import ecommerce.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedidos, Long> {
    List<Pedidos> findByUsuarioOrderByDataDesc(Usuario usuario);
    @Query("""
    SELECT p
    FROM Pedidos p
    JOIN p.usuario u
    WHERE
        (:busca IS NULL
            OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :busca, '%'))
            OR (:idPedido IS NOT NULL AND p.id = :idPedido))
        AND (:status IS NULL OR p.status = :status)
        AND (:dataInicio IS NULL OR p.data >= :dataInicio)
        AND (:dataFim IS NULL OR p.data <= :dataFim)
        AND (:precoMin IS NULL OR p.total >= :precoMin)
        AND (:precoMax IS NULL OR p.total <= :precoMax)
    """)
    Page<Pedidos> buscarPedidos(
            @Param("busca") String busca,
            @Param("idPedido") Long idPedido,
            @Param("status") StatusPedido status,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("precoMin") Double precoMin,
            @Param("precoMax") Double precoMax,
            Pageable pageable
    );
}
