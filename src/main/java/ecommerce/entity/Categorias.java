package ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categorias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String status = "ATIVO";

    @ManyToMany(mappedBy = "categorias")
    private List<Produto> produtos;
}
