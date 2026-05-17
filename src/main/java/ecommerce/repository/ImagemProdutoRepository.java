package ecommerce.repository;

import ecommerce.model.ImagemProduto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagemProdutoRepository
        extends JpaRepository<ImagemProduto,Long> {

}