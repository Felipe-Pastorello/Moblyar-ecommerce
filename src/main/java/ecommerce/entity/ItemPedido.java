package ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.Entity;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantidade;
    private Double preco;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedidos pedidos;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;
}
