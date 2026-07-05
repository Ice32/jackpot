package com.sporty.jackpot_service.controller;

import com.sporty.jackpot_service.IntegrationTest;
import com.sporty.jackpot_service.model.JackpotReward;
import com.sporty.jackpot_service.model.JackpotTestBuilder;
import com.sporty.jackpot_service.repository.JackpotRepository;
import com.sporty.jackpot_service.repository.JackpotRewardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class BetRewardControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JackpotRepository jackpotRepository;

    @Autowired
    private JackpotRewardRepository rewardRepository;

    @Test
    void getRewards() throws Exception {
        var jackpot = jackpotRepository.save(new JackpotTestBuilder().build());
        var reward = rewardRepository.save(new JackpotReward(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                jackpot.getJackpotId(),
                new BigDecimal("123.45")
        ));

        mvc.perform(get("/api/v1/bets/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].betId").value(reward.getBetId()))
                .andExpect(jsonPath("$[0].userId").value(reward.getUserId()))
                .andExpect(jsonPath("$[0].jackpotId").value(reward.getJackpotId()))
                .andExpect(jsonPath("$[0].jackpotRewardAmount").value(reward.getJackpotRewardAmount().doubleValue()))
                .andExpect(jsonPath("$[0].jackpot").doesNotExist())
                .andExpect(jsonPath("$[0]", not(org.hamcrest.Matchers.hasKey("id"))));
    }
}
