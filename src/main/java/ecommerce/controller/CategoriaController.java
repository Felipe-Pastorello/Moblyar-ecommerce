
package ecommerce.controller;

import ecommerce.entity.Categorias;
import ecommerce.service.CategoriaService;
import ecommerce.service.ProdutoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaController {

    private final CategoriaService service;
    private final ProdutoService produtoService;

    public CategoriaController(
            CategoriaService service,
            ProdutoService produtoService) {

        this.service = service;
        this.produtoService = produtoService;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "categorias",
                service.listAll()
        );

        model.addAttribute(
                "produtosSemCategoria",
                produtoService.buscarSemCategoria()
        );

        return "admin/categorias/list";
    }

    @PostMapping("/save")
    public String salvar(Categorias categoria) {

        boolean novaCategoria = categoria.getId() == null;

        service.save(categoria);

        if (novaCategoria) {
            return "redirect:/admin/categorias?sucesso=cadastrada";
        }

        return "redirect:/admin/categorias?sucesso=editada";
    }

    @GetMapping("/status/{id}")
    public String alterarStatus(@PathVariable Long id) {

        service.alterarStatus(id);

        return "redirect:/admin/categorias";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        service.delete(id);

        return "redirect:/admin/categorias";
    }

    @GetMapping("/existe")
    @ResponseBody
    public boolean existeNome(
            @RequestParam String nome,
            @RequestParam(required = false) Long id) {

        return service.existeNome(nome, id);
    }
}
