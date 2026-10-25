
package ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import ecommerce.service.UsuarioService;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Conta administrativa independente da tabela de clientes
    @Bean
    public UserDetailsService adminUserDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${admin.username}") String username,
            @Value("${admin.password}") String password) {

        return new InMemoryUserDetailsManager(
                User.withUsername(username)
                        .password(passwordEncoder.encode(password))
                        .roles("ADMIN")
                        .build()
        );
    }

    // Contexto de sessão exclusivo do administrador
    @Bean
    public HttpSessionSecurityContextRepository adminSecurityContextRepository() {
        HttpSessionSecurityContextRepository repository =
                new HttpSessionSecurityContextRepository();

        repository.setSpringSecurityContextKey("ADMIN_SECURITY_CONTEXT");

        return repository;
    }

    // SEGURANÇA DO ADMINISTRADOR
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(
            HttpSecurity http,
            HttpSessionSecurityContextRepository adminSecurityContextRepository,
            @Qualifier("adminUserDetailsService") UserDetailsService adminUserDetailsService)
            throws Exception {

        http
                .securityMatcher("/admin/**")

                .securityContext(context -> context
                        .securityContextRepository(adminSecurityContextRepository)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login", "/admin/css/**").permitAll()
                        .anyRequest().hasRole("ADMIN")
                )

                .userDetailsService(adminUserDetailsService)

                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin", true)
                        .failureUrl("/admin/login?erro=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    // SEGURANÇA DOS CLIENTES
    @Bean
    @Order(2)
    public SecurityFilterChain userSecurityFilterChain(
            HttpSecurity http,
            UsuarioService usuarioService) throws Exception {
        http.userDetailsService(usuarioService);

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**",
                                "/buscar/**",
                                "/produtos/**",
                                "/login",
                                "/cadastro"
                        ).permitAll()

                        .requestMatchers(
                                "/carrinho/**",
                                "/comprar/**",
                                "/pedido/**",
                                "/checkout/**",
                                "/perfil",
                                "/perfil/**"
                        ).authenticated()

                        .anyRequest().permitAll()
                )

                .exceptionHandling(exception -> exception
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                request -> request.getRequestURI()
                                        .startsWith("/carrinho")
                        )
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?erro=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}
