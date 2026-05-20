package ecommerce.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String descricao;

    private Double preco;

    private Integer estoque;

    @OneToMany(mappedBy = "produto",
            cascade = CascadeType.ALL,
            orphanRemoval = true)

    @JsonIgnore
    private List<ImagemProduto> imagens =
            new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "produto_categoria",
            joinColumns = @JoinColumn(name = "produto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categorias> categorias;

    public void adicionarImagem(ImagemProduto img){
        imagens.add(img);
        img.setProduto(this);
    }
}

