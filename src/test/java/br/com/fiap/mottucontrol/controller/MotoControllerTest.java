package br.com.fiap.mottucontrol.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Collections;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.mottucontrol.model.Moto;
import br.com.fiap.mottucontrol.repository.MotoRepository;

@SpringBootTest(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    JpaRepositoriesAutoConfiguration.class
})
@AutoConfigureMockMvc
public class MotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean 
    private MotoRepository motoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void deveListarMotosComSucesso() throws Exception {
        when(motoRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/motos"))
               .andExpect(status().isOk());
    }

    @Test
    public void deveCriarMotoComSucesso() throws Exception {
        Moto moto = new Moto();
        moto.setModelo("Honda Biz");
        moto.setPlaca("TEST123");
        moto.setAno(2023);

        when(motoRepository.save(any(Moto.class))).thenReturn(moto);

        mockMvc.perform(post("/api/motos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moto)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.placa").value("TEST123"));
    }
}
