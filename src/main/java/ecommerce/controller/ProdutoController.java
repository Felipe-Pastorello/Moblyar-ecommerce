package ecommerce.controller;

import ecommerce.model.Produto;
import ecommerce.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/products")
public class ProdutoController {
    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public String listProducts(Model model){
        model.addAttribute("products", service.listAll());
        return "products/list";
    }

    @GetMapping("/new")
    public String newProduct(Model model){
        model.addAttribute("product", new Produto());
        return "products/form";
    }

    @PostMapping("/save")
    public String saveProduct(Produto product){
        service.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model){
        model.addAttribute("product", service.getById(id));
        return "products/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id){
        service.delete(id);
        return "redirect:/products";
    }
}
