package ecommerce.controller;

import ecommerce.model.ImagemProduto;
import ecommerce.model.Produto;
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

        if(arquivos != null){

            String pastaUploads =
                    System.getProperty("user.dir")
                            + "/src/main/resources/static/uploads/";

            File pasta = new File(pastaUploads);

            if(!pasta.exists()){
                pasta.mkdirs();
            }

            for(MultipartFile arquivo : arquivos){

                if(!arquivo.isEmpty()){

                    String nomeArquivo =
                            UUID.randomUUID() + "_"
                                    + arquivo.getOriginalFilename();

                    arquivo.transferTo(
                            new File(pastaUploads + nomeArquivo)
                    );

                    ImagemProduto img =
                            new ImagemProduto();

                    img.setNomeArquivo(nomeArquivo);

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