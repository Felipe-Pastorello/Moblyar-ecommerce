package ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pedidos")
public class Pedidos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime data;

    private Double total;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @OneToMany(
            mappedBy = "pedidos",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemPedido> items;

    private String asaasCheckoutId;

    private String asaasPaymentId;

    @Column(nullable = false)
    private boolean estoqueDevolvido = false;
}