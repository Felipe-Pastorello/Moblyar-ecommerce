package ecommerce.controller;

import ecommerce.model.Produto;
import ecommerce.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public String listProdutos(Model model){
        model.addAttribute("produtos", service.listAll());
        return "produtos/list";
    }

    @GetMapping("/new")
    public String newProduto(Model model){
        model.addAttribute("produto", new Produto());
        return "produtos/form";
    }

    @PostMapping("/save")
    public String saveProduto(Produto produto){
        service.save(produto);
        return "redirect:/produtos";
    }

    /*@GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model){
        model.addAttribute("produto", service.getById(id));
        return "produto/form";
    }*/

    @GetMapping("/delete/{id}")
    public String deleteProduto(@PathVariable Long id){
        service.delete(id);
        return "redirect:/produtos";
    }
}
