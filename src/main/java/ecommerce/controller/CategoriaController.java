package ecommerce.controller;

import ecommerce.entity.Categorias;
import ecommerce.service.CategoriaService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(
            CategoriaService service){

        this.service=service;
    }

    @GetMapping
    public String listar(Model model){

        model.addAttribute(
                "categorias",
                service.listAll());

        return "categorias/list";
    }

    @PostMapping("/save")
    public String salvar(Categorias categoria){

        service.save(categoria);

        return "redirect:/categorias";
    }

    @GetMapping("/status/{id}")
    public String alterarStatus(
            @PathVariable Long id){

        service.alterarStatus(id);

        return "redirect:/categorias";
    }

    @GetMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id){

        service.delete(id);

        return "redirect:/categorias";
    }
}
