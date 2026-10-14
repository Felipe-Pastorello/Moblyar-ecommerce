
package ecommerce.controller;

import ecommerce.entity.Produto;
import ecommerce.service.CategoriaService;
import ecommerce.service.ProdutoService;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BuscaProdutoController {

    private final ProdutoService service;
    private final CategoriaService categoriaService;

    public BuscaProdutoController(
            ProdutoService service,
            CategoriaService categoriaService) {
        this.service = service;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/produtos/busca")
    public String buscarProdutos(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Double precoMin,
            @RequestParam(required = false) Double precoMax,
            @RequestParam(required = false) Boolean disponivel,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "relevancia") String ordenar,
            Model model) {

        Page<Produto> produtos = service.buscarProdutos(
                termo,
                categoriaId,
                precoMin,
                precoMax,
                disponivel,
                pagina,
                ordenar
        );

        model.addAttribute("produtos", produtos);
        model.addAttribute("termo", termo);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("precoMin", precoMin);
        model.addAttribute("precoMax", precoMax);
        model.addAttribute("disponivel", disponivel);
        model.addAttribute("ordenar", ordenar);
        model.addAttribute(
                "categorias",
                categoriaService.listarAtivas()
        );

        return "produtos/busca";
    }
}
