package ecommerce.repository;
import ecommerce.entity.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedidos, Long> {
}
