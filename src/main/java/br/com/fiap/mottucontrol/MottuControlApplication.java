package br.com.fiap.mottucontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Spring Boot.
 * * A anotação @SpringBootApplication já inclui:
 * 1. @Configuration: Marca a classe como fonte de configuração.
 * 2. @EnableAutoConfiguration: Tenta configurar automaticamente o Spring.
 * 3. @ComponentScan: Varre este pacote (br.com.fiap.mottucontrol) e 
 * todos os seus sub-pacotes (como .controller, .repository, .model)
 * em busca de componentes.
 * * As anotações @EnableJpaRepositories, @EntityScan e @ComponentScan explícitas
 * foram removidas por serem redundantes e causarem conflitos com os testes "fatiados" (@WebMvcTest).
 */
@SpringBootApplication
public class MottuControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(MottuControlApplication.class, args);
    }

}
