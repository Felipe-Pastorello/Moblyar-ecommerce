package ecommerce.repository;

import ecommerce.entity.ImagemProduto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagemProdutoRepository
        extends JpaRepository<ImagemProduto,Long> {

}