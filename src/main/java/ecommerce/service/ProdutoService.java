package ecommerce.service;
import ecommerce.entity.Produto;
import ecommerce.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public List<Produto> listAll(){
        return repository.findAll();
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

        Produto produto =
                repository.findById(id)
                        .orElseThrow();

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
}
