package ecommerce.service;

import ecommerce.entity.Categorias;
import ecommerce.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository repository;

    public CategoriaService(
            CategoriaRepository repository){

        this.repository=repository;
    }

    public List<Categorias> listAll(){
        return repository.findAll();
    }

    public void save(Categorias categoria){
        repository.save(categoria);
    }

    public Categorias getById(Long id){

        return repository.findById(id)
                .orElseThrow();
    }

    public void delete(Long id){

        repository.deleteById(id);
    }

    public void alterarStatus(Long id){

        Categorias categoria = getById(id);

        if("ATIVO".equals(categoria.getStatus())){
            categoria.setStatus("INATIVO");
        }else{
            categoria.setStatus("ATIVO");
        }
        save(categoria);
    }

    public List<Categorias> buscarPorIds(List<Long> ids){
        return repository.findAllById(ids);
    }
}
