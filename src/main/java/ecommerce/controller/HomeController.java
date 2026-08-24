package ecommerce.controller;

import ecommerce.service.CategoriaService;
import ecommerce.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {

        model.addAttribute("produtos", produtoService.listarProdutosHome());
        model.addAttribute("categorias", categoriaService.listarAtivas());

        return "home/home";
    }
}
