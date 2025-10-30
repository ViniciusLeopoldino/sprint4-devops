package br.com.fiap.mottucontrol.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// ======================================================
// ===== IMPORTAÇÃO NECESSÁRIA PARA O CSRF =====
// ======================================================
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// ======================================================

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Importação da segurança de teste
import org.springframework.security.test.context.support.WithMockUser; 

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.mottucontrol.model.Moto;
import br.com.fiap.mottucontrol.repository.MotoRepository;

import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(MotoController.class)
public class MotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private MotoRepository motoRepository;

    @MockBean
    private UserDetailsService userDetailsService; 

    @Test
    @WithMockUser // Anotação para simular usuário logado
    public void deveListarMotosComSucesso() throws Exception {
        when(motoRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/motos"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser // Anotação para simular usuário logado
    public void deveCriarMotoComSucesso() throws Exception {
        Moto moto = new Moto();
        moto.setModelo("Honda Biz");
        moto.setPlaca("TEST123");
        moto.setAno(2023);

        when(motoRepository.save(any(Moto.class))).thenReturn(moto);

        mockMvc.perform(post("/api/motos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moto))
                // ======================================================
                // ===== CORREÇÃO: ADICIONA O TOKEN CSRF FALSO =====
                // ======================================================
                .with(csrf()) 
               )
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.placa").value("TEST123"));
    }
}
