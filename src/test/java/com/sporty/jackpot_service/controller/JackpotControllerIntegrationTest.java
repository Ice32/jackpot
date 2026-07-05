package com.sporty.jackpot_service.controller;

import com.sporty.jackpot_service.IntegrationTest;
import com.sporty.jackpot_service.model.JackpotTestBuilder;
import com.sporty.jackpot_service.repository.JackpotRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class JackpotControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JackpotRepository jackpotRepository;

    @Test
    void getJackpots() throws Exception {
        var jackpot = jackpotRepository.save(new JackpotTestBuilder().build());

        mvc.perform(get("/api/v1/jackpots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jackpotId").value(jackpot.getJackpotId()))
                .andExpect(jsonPath("$[0].name").value(jackpot.getName()))
                .andExpect(jsonPath("$[0].currentBalance").value(jackpot.getCurrentBalance().doubleValue()))
                .andExpect(jsonPath("$[0].baseAmount").doesNotExist())
                .andExpect(jsonPath("$[0].contributionConfiguration").doesNotExist())
                .andExpect(jsonPath("$[0].rewardConfiguration").doesNotExist());
    }
}
