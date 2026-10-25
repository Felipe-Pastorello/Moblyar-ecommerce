package ecommerce.service;

import ecommerce.entity.Usuario;
import ecommerce.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void cadastrar(Usuario usuario) {
        usuario.setEmail(usuario.getEmail().trim());

        if (repository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException(
                    "Já existe um usuário cadastrado com esse e-mail."
            );
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        repository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    @Transactional
    public boolean atualizarDados(
            String emailAtual,
            String nome,
            String novoEmail,
            String senhaAtual,
            String novaSenha) {

        Usuario usuario = repository.findByEmail(emailAtual)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );

        if (nome == null || nome.isBlank()) {
            throw new RuntimeException("Informe seu nome.");
        }

        if (novoEmail == null || novoEmail.isBlank()) {
            throw new RuntimeException("Informe seu e-mail.");
        }

        novoEmail = novoEmail.trim();

        boolean emailAlterado = !usuario.getEmail().equalsIgnoreCase(novoEmail);
        boolean querAlterarSenha = novaSenha != null && !novaSenha.isBlank();

        if (emailAlterado || querAlterarSenha) {
            if (senhaAtual == null
                    || senhaAtual.isBlank()
                    || !passwordEncoder.matches(
                    senhaAtual,
                    usuario.getSenha())) {
                throw new RuntimeException(
                        "A senha atual está incorreta."
                );
            }
        }

        if (emailAlterado && repository.existsByEmail(novoEmail)) {
            throw new RuntimeException(
                    "Já existe uma conta com esse e-mail."
            );
        }

        /*if (querAlterarSenha && novaSenha.length() < 8) {
            throw new RuntimeException(
                    "A nova senha deve ter pelo menos 8 caracteres."
            );
        }*/

        usuario.setNome(nome.trim());
        usuario.setEmail(novoEmail);

        if (querAlterarSenha) {
            usuario.setSenha(passwordEncoder.encode(novaSenha));
        }

        repository.save(usuario);

        return emailAlterado;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuário não encontrado."
                        )
                );

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles("USER")
                .build();
    }
}