package ecommerce.model;
import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagemProduto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;
}
