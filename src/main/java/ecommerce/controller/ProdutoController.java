package ecommerce.controller;

import ecommerce.entity.ImagemProduto;
import ecommerce.entity.Produto;
import ecommerce.repository.ImagemProdutoRepository;
import ecommerce.service.ProdutoService;
import ecommerce.service.CategoriaService;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;
    private final CategoriaService categoriaService;
    private final ImagemProdutoRepository imagemRepository;


    public ProdutoController(ProdutoService service, CategoriaService categoriaService, ImagemProdutoRepository imagemRepository) {
        this.service = service;
        this.categoriaService = categoriaService;
        this.imagemRepository = imagemRepository;

    }

    @GetMapping
    public String listProdutos(
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {

        var paginaProdutos = service.listAll(pagina);

        model.addAttribute("paginaProdutos", paginaProdutos);
        model.addAttribute("produtos", paginaProdutos.getContent());
        model.addAttribute("categorias", categoriaService.listAll());

        return "produtos/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduto(
            @PathVariable Long id){

        service.delete(id);

        return "redirect:/produtos";
    }

    @PostMapping("/save")
    public String saveProduto(
            Produto produto,

            @RequestParam(value="categoriaIds", required=false)
            List<Long> categoriaIds,

            @RequestParam(value="arquivos", required=false)
            MultipartFile[] arquivos)

            throws Exception {

        boolean novoProduto = produto.getId() == null;


        if(produto.getId() != null){
            Produto existente = service.getById(produto.getId());
            produto.setImagens(existente.getImagens());
        }

        // Categorias
        if(categoriaIds != null){

            produto.setCategorias(categoriaService.buscarPorIds(categoriaIds));

        }else{

            produto.setCategorias(List.of());
        }

        // Novas imagens
        if (arquivos != null) {

            String pastaUploads = "C:/uploads/";

            for (MultipartFile arquivo : arquivos) {

                if (!arquivo.isEmpty()) {

                    String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();

                    arquivo.transferTo(
                            new File(pastaUploads + nomeArquivo));

                    ImagemProduto img = new ImagemProduto();

                    img.setNomeArquivo(nomeArquivo);

                    produto.adicionarImagem(img);
                }
            }
        }

        service.save(produto);

        if(novoProduto){
            return "redirect:/produtos?sucesso=cadastrado";
        }

        return "redirect:/produtos?sucesso=editado";
    }

    @GetMapping("/status/{id}")
    public String alterarStatus(
            @PathVariable Long id){

        service.toggleStatus(id);

        return "redirect:/produtos";
    }

    @GetMapping("/removerCategoria")
    public String removerCategoria(
            Long produtoId,
            Long categoriaId){

        Produto produto =
                service.getById(produtoId);

        produto.getCategorias()
                .removeIf(c->

                        c.getId()
                                .equals(categoriaId));

        service.save(produto);

        return "redirect:/categorias";
    }

    @GetMapping("/existe")
    @ResponseBody
    public boolean existeNome(String nome){

        return service.existeNome(nome);
    }

    @GetMapping("/{id}/imagens")
    @ResponseBody
    public List<ImagemProduto> listarImagens(
            @PathVariable Long id){

        Produto produto =
                service.getById(id);

        return produto.getImagens();
    }

    @GetMapping("/imagem/delete/{id}")
    @ResponseBody
    public void excluirImagem(
            @PathVariable Long id){

        imagemRepository.deleteById(id);
    }

    @GetMapping("/busca")
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

        model.addAttribute("categorias",
                categoriaService.listarAtivas());

        return "produtos/busca";
    }

    @GetMapping("/{id}")
    public String visualizarProduto(@PathVariable Long id, Model model) {

        Produto produto = service.getById(id);

        if (produto == null) {
            return "redirect:/";
        }

        model.addAttribute("produto", produto);

        return "produtos/produto";
    }

}