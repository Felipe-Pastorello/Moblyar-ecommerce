package ecommerce.service;
import ecommerce.entity.Produto;
import ecommerce.repository.ProdutoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class ProdutoService {
    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Page<Produto> listAll(int pagina) {

        Pageable pageable = PageRequest.of(pagina, 9);

        return repository.findAll(pageable);
    }

    public Produto save(Produto produto){
        Produto existente =
                repository.findByNome(
                        produto.getNome()
                );

        if(existente != null &&
                !existente.getId()
                        .equals(produto.getId())){

            throw new RuntimeException(
                    "Já existe um produto com esse nome."
            );
        }

        return repository.save(produto);
    }

    public Produto getById(Long id){
        return repository.findById(id).orElse(null);
    }

    public void delete(Long id){
        repository.deleteById(id);
    }

    public void toggleStatus(Long id){

        Produto produto = repository.findById(id).orElseThrow();

        if(produto.getStatus().equals("ATIVO")){
            produto.setStatus("INATIVO");
        }
        else{
            produto.setStatus("ATIVO");
        }

        repository.save(produto);
    }

    public List<Produto> buscarSemCategoria() {
        return repository.buscarSemCategoria();
    }

    public boolean existeNome(String nome){
        return repository.existsByNome(nome);
    }

    public List<Produto> listarProdutosHome() {

        List<Produto> produtos = repository.findAll();

        return produtos.stream()
                .filter(p -> "ATIVO".equals(p.getStatus()))
                .limit(4)
                .toList();
    }

    public Page<Produto> buscarProdutos(String termo, Long categoriaId, Double precoMin, Double precoMax, Boolean disponivel, int pagina, String ordenar) {

        Sort sort = switch (ordenar) {
            case "precoMenor" ->
                    Sort.by("preco").ascending();

            case "precoMaior" ->
                    Sort.by("preco").descending();

            case "nome" ->
                    Sort.by("nome").ascending();

            default ->
                    Sort.by("id").descending();
        };

        Pageable pageable = PageRequest.of(
                pagina,
                9,
                sort
        );

        return repository.buscarProdutos(
                termo,
                categoriaId,
                precoMin,
                precoMax,
                disponivel,
                pageable
        );
    }
}
