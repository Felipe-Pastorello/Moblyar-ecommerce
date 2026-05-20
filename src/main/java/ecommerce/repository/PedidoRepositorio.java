package ecommerce.repository;
import ecommerce.entity.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepositorio extends JpaRepository<Pedidos, Long> {
}
