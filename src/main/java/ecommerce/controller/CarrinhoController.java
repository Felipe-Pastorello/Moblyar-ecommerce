package ecommerce.controller;

import ecommerce.dto.CarrinhoItemResponse;
import ecommerce.entity.Carrinho;
import ecommerce.service.CarrinhoService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrinho")
public class CarrinhoController {

    private final CarrinhoService service;

    public CarrinhoController(CarrinhoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CarrinhoItemResponse>> visualizarCarrinho(Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return ResponseEntity.status(401).build();
        }

        Carrinho carrinho =
                service.buscarCarrinho(authentication.getName());

        List<CarrinhoItemResponse> resposta =
                carrinho.getItens()
                        .stream()
                        .map(item -> new CarrinhoItemResponse(
                                item.getId(),
                                item.getProduto().getId(),
                                item.getProduto().getNome(),
                                item.getQuantidade(),
                                item.getProduto().getPreco()
                        ))
                        .toList();

        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/adicionar")
    public ResponseEntity<Void> adicionarProduto(Authentication authentication, @RequestParam Long produtoId, @RequestParam(defaultValue = "1") Integer quantidade) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return ResponseEntity.status(401).build();
        }

        service.adicionarProduto(
                authentication.getName(),
                produtoId,
                quantidade
        );

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/item/{itemId}")
    public ResponseEntity<Void> alterarQuantidade(Authentication authentication, @PathVariable Long itemId, @RequestParam Integer quantidade) {

        service.alterarQuantidade(
                authentication.getName(),
                itemId,
                quantidade
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> removerItem(Authentication authentication, @PathVariable Long itemId) {

        service.removerItem(
                authentication.getName(),
                itemId
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> esvaziarCarrinho(Authentication authentication) {

        service.esvaziarCarrinho(
                authentication.getName()
        );

        return ResponseEntity.ok().build();
    }
}