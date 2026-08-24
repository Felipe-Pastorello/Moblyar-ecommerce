package ecommerce.controller;

import ecommerce.entity.Produto;
import ecommerce.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BuscaController {

    private final ProdutoRepository produtoRepository;

    public BuscaController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/buscar")
    public String buscar(
            @RequestParam(required = false) String nome,
            Pageable pageable,
            Model model) {

        Page<Produto> produtos;

        if (nome == null || nome.isBlank()) {
            produtos = produtoRepository.findAll(pageable);
        } else {
            produtos = produtoRepository.findByNomeContainingIgnoreCase(
                    nome,
                    pageable
            );
        }

        model.addAttribute("produtos", produtos);
        model.addAttribute("nome", nome);

        return "produtos/busca";
    }
}