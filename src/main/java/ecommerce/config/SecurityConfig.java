package ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                .authorizeHttpRequests(auth -> auth

                        // =====================================
                        // PÚBLICO
                        // =====================================

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


                        // =====================================
                        // ADMIN
                        // =====================================

                        .requestMatchers(
                                "/admin/**"
                        ).hasRole("ADMIN")


                        // =====================================
                        // USUÁRIO LOGADO
                        // =====================================

                        .requestMatchers(
                                "/carrinho/**",
                                "/comprar/**",
                                "/pedido/**",
                                "/perfil"
                        ).authenticated()


                        // =====================================
                        // RESTANTE
                        // =====================================

                        .anyRequest().permitAll()
                )


                // =====================================
                // LOGIN DO USUÁRIO
                // =====================================

                .formLogin(form -> form

                        .loginPage("/login")

                        .loginProcessingUrl("/login")

                        .defaultSuccessUrl("/", true)

                        .failureUrl("/login?erro=true")

                        .permitAll()
                )


                // =====================================
                // LOGOUT
                // =====================================

                .logout(logout -> logout

                        .logoutSuccessUrl("/")

                        .permitAll()
                );


        return http.build();
    }
}