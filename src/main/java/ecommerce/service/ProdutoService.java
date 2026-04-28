package ecommerce.service;
import ecommerce.model.Produto;
import ecommerce.repository.ProdutoRepositorio;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepositorio repository;

    public ProdutoService(ProdutoRepositorio repository) {
        this.repository = repository;
    }

    public List<Produto> listAll(){
        return repository.findAll();
    }

    public Produto save(Produto produto){
        return repository.save(produto);
    }

    public Produto getById(Long id){
        return repository.findById(id).orElse(null);
    }

    public void delete(Long id){
        repository.deleteById(id);
    }
}
