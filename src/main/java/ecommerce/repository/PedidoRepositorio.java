package ecommerce.repository;
import ecommerce.model.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepositorio extends JpaRepository<Pedidos, Long> {
}
