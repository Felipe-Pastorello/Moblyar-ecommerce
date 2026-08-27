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
public class BuscaController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    public BuscaController(
            ProdutoService produtoService,
            CategoriaService categoriaService) {

        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/buscar")
    public String buscar(

            @RequestParam(required = false) String nome,

            @RequestParam(required = false) Long categoriaId,

            @RequestParam(required = false) Double precoMin,

            @RequestParam(required = false) Double precoMax,

            @RequestParam(required = false) Boolean disponivel,

            @RequestParam(defaultValue = "0") int pagina,

            @RequestParam(defaultValue = "relevancia") String ordenar,

            Model model) {

        Page<Produto> produtos =
                produtoService.buscarProdutos(
                        nome,
                        categoriaId,
                        precoMin,
                        precoMax,
                        disponivel,
                        pagina,
                        ordenar
                );

        model.addAttribute("produtos", produtos);

        model.addAttribute("nome", nome);
        model.addAttribute("termo", nome);

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