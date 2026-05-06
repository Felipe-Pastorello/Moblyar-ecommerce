package ecommerce.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders") // "order" é palavra reservada
public class Pedidos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime data;
    private Double total;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario user;

    @OneToMany(mappedBy = "pedidos")
    private List<ItemPedido> items;
}
