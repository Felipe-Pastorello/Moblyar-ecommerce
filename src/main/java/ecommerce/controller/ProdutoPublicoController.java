package ecommerce.controller;

import ecommerce.entity.Produto;
import ecommerce.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProdutoPublicoController {

    private final ProdutoService service;

    public ProdutoPublicoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping("/produto/{id}")
    public String visualizarProduto(
            @PathVariable Long id,
            Model model) {

        Produto produto = service.getById(id);

        if (produto == null) {
            return "redirect:/";
        }

        model.addAttribute("produto", produto);

        return "produtos/produto";
    }
}