package ecommerce.dto;

import java.math.BigDecimal;

public record CarrinhoItemResponse(
        Long itemId,
        Long produtoId,
        String nome,
        Integer quantidade,
        Double preco
) {
}