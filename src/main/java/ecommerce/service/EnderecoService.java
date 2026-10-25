package ecommerce.service;

import ecommerce.entity.Endereco;
import ecommerce.entity.Usuario;
import ecommerce.repository.EnderecoRepository;
import ecommerce.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final UsuarioRepository usuarioRepository;

    public EnderecoService(EnderecoRepository enderecoRepository, UsuarioRepository usuarioRepository) {
        this.enderecoRepository = enderecoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Endereco> listar(String email) {
        Usuario usuario = buscarUsuario(email);
        return enderecoRepository.findByUsuariosContaining(usuario);
    }

    @Transactional(readOnly = true)
    public Endereco buscarDaConta(Long id, String email) {
        Usuario usuario = buscarUsuario(email);

        return enderecoRepository.findByIdAndUsuariosContaining(id, usuario).orElseThrow(() -> new RuntimeException(
                "Endereço não encontrado nesta conta."
                )
        );
    }

    @Transactional
    public void criar(String email, Endereco dados) {
        validar(dados);

        Usuario usuario = buscarUsuario(email);

        Endereco endereco = new Endereco();
        preencher(endereco, dados);
        endereco.setUsuarios(new ArrayList<>(List.of(usuario)));

        enderecoRepository.save(endereco);

        if (usuario.getEnderecos() == null) {
            usuario.setEnderecos(new ArrayList<>());
        }

        usuario.getEnderecos().add(endereco);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void atualizar(Long id, String email, Endereco dados) {
        validar(dados);

        Endereco endereco = buscarDaConta(id, email);
        preencher(endereco, dados);

        enderecoRepository.save(endereco);
    }

    @Transactional
    public void excluir(Long id, String email) {
        Usuario usuario = buscarUsuario(email);
        Endereco endereco = buscarDaConta(id, email);

        if (usuario.getEnderecos() != null) {
            usuario.getEnderecos().removeIf(
                    e -> e.getId().equals(endereco.getId())
            );
        }

        usuarioRepository.save(usuario);

        if (endereco.getUsuarios() != null) {
            endereco.getUsuarios().removeIf(
                    u -> u.getId().equals(usuario.getId())
            );

            if (endereco.getUsuarios().isEmpty()) {
                enderecoRepository.delete(endereco);
            } else {
                enderecoRepository.save(endereco);
            }
        } else {
            enderecoRepository.delete(endereco);
        }
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado.")
        );
    }

    private void validar(Endereco endereco) {
        if (endereco.getRua() == null || endereco.getRua().isBlank()) {
            throw new RuntimeException("Informe a rua.");
        }

        if (endereco.getCidade() == null || endereco.getCidade().isBlank()) {
            throw new RuntimeException("Informe a cidade.");
        }

        if (endereco.getEstado() == null || endereco.getEstado().isBlank()) {
            throw new RuntimeException("Informe o estado.");
        }

        if (endereco.getCep() == null || endereco.getCep().isBlank()) {
            throw new RuntimeException("Informe o CEP.");
        }
    }

    private void preencher(Endereco destino, Endereco origem) {
        destino.setRua(origem.getRua().trim());
        destino.setCidade(origem.getCidade().trim());
        destino.setEstado(origem.getEstado().trim());
        destino.setCep(origem.getCep().trim());
    }
}