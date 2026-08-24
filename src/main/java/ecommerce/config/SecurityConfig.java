package ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService users() {

        UserDetails admin =
                User.withDefaultPasswordEncoder().username("admin").password("1234").roles("ADMIN").build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/", "/css/**", "/uploads/**", "/buscar/**").permitAll()

                        .requestMatchers("/produtos/**", "/categorias/**").authenticated()

                        .anyRequest().permitAll()
                )

                .formLogin(Customizer.withDefaults())

                .logout(Customizer.withDefaults());

        return http.build();
    }
}