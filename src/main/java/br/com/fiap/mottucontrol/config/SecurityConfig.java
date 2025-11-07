package br.com.fiap.mottucontrol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desabilita a proteção CSRF
            .csrf(csrf -> csrf.disable()) 
            
            // 2. Autoriza todas as requisições (mas ainda exige Basic Auth)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated() 
            )
            
            // 3. Habilita o Basic Auth
            .httpBasic(org.springframework.security.config.Customizer.withDefaults()); 

        return http.build();
    }
}