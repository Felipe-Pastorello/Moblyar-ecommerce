package ecommerce.repository;
import ecommerce.model.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepositorio extends JpaRepository<Categorias, Long> {
}
