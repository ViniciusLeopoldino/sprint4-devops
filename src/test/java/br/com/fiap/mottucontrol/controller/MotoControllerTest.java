package br.com.fiap.mottucontrol.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.mottucontrol.controller.MotoController;
import br.com.fiap.mottucontrol.model.Moto;
import br.com.fiap.mottucontrol.repository.MotoRepository;

@WebMvcTest(value = MotoController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class MotoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MotoRepository motoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void deveListarMotosComSucesso() throws Exception {
        // Mock do comportamento do repositório
        when(motoRepository.findAll()).thenReturn(Collections.emptyList());

        // Execução da requisição
        mockMvc.perform(get("/api/motos"))
               .andExpect(status().isOk());
    }

    @Test
    public void deveCriarMotoComSucesso() throws Exception {
        // Criação de uma moto de exemplo
        Moto moto = new Moto();
        moto.setModelo("Honda Biz");
        moto.setPlaca("TEST123");
        moto.setAno(2023);

        // Mock do comportamento de salvar
        when(motoRepository.save(any(Moto.class))).thenReturn(moto);

        // Execução da requisição POST
        mockMvc.perform(post("/api/motos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moto)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.placa").value("TEST123"));
    }
}