package ecommerce.service;

import ecommerce.entity.Usuario;
import ecommerce.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // CADASTRO
    // =========================

    public void cadastrar(Usuario usuario) {

        if (repository.existsByEmail(usuario.getEmail())) {

            throw new RuntimeException(
                    "Já existe um usuário cadastrado com esse e-mail."
            );
        }

        usuario.setSenha(
                passwordEncoder.encode(usuario.getSenha())
        );

        repository.save(usuario);
    }


    // =========================
    // BUSCAR USUÁRIO
    // =========================

    public Usuario buscarPorEmail(String email) {

        return repository
                .findByEmail(email)
                .orElse(null);
    }


    // =========================
    // SPRING SECURITY
    // =========================

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario usuario = repository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuário não encontrado."
                        )
                );

        return User
                .withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles("USER")
                .build();
    }
}