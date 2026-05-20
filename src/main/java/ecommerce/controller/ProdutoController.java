package ecommerce.controller;

import ecommerce.entity.ImagemProduto;
import ecommerce.entity.Produto;
import ecommerce.service.ProdutoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

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

    @PostMapping("/save")
    public String saveProduto(
            Produto produto,
            @RequestParam(value = "arquivos", required = false)
            MultipartFile[] arquivos) throws Exception {
        if (arquivos != null) {
            String pastaUploads = "C:/uploads/";
            for (MultipartFile arquivo : arquivos) {
                if (!arquivo.isEmpty()) {
                    String nomeArquivo =
                            UUID.randomUUID()
                                    + "_"
                                    + arquivo.getOriginalFilename();
                    arquivo.transferTo(
                            new File(
                                    pastaUploads
                                            + nomeArquivo
                            )
                    );
                    ImagemProduto img =
                            new ImagemProduto();
                    img.setNomeArquivo(
                            nomeArquivo
                    );

                    produto.adicionarImagem(img);
                }
            }
        }
        service.save(produto);
        return "redirect:/produtos";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduto(
            @PathVariable Long id){

        service.delete(id);

        return "redirect:/produtos";
    }
}