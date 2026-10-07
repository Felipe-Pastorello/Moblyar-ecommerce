package ecommerce.repository;
import ecommerce.entity.Pedidos;
import ecommerce.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedidos, Long> {
    List<Pedidos> findByUsuarioOrderByDataDesc(Usuario usuario);
}
