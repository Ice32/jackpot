package com.sporty.jackpot_service.controller;

import com.sporty.jackpot_service.IntegrationTest;
import com.sporty.jackpot_service.dto.EvaluationResult;
import com.sporty.jackpot_service.dto.request.EvaluateBetRequestTestBuilder;
import com.sporty.jackpot_service.dto.request.SubmitBetRequestTestBuilder;
import com.sporty.jackpot_service.model.JackpotTestBuilder;
import com.sporty.jackpot_service.repository.JackpotContributionRepository;
import com.sporty.jackpot_service.repository.JackpotRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class BetControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JsonMapper mapper;

    @Autowired
    private JackpotRepository jackpotRepository;

    @Autowired
    private JackpotContributionRepository jackpotContributionRepository;

    @Value("${jackpot.strategies.contribution.fixed.contribution-rate}")
    private BigDecimal fixedContributionRate;

    @Value("${jackpot.strategies.contribution.variable.initial-rate}")
    private BigDecimal variableContributionInitialRate;

    @Value("${jackpot.strategies.contribution.variable.floor-rate}")
    private BigDecimal variableContributionFloorRate;

    @Test
    void submitBet_FixedContributionJackpot_ContributionPersisted() throws Exception {
        var initialJackpotBalance = new BigDecimal("1100");
        var jackpot = jackpotRepository.save(JackpotTestBuilder.fixedContributionRate()
                .currentBalance(initialJackpotBalance)
                .build());
        var betPayload = new SubmitBetRequestTestBuilder(jackpot.getJackpotId()).build();

        mvc.perform(post("/api/v1/bets/submit")
                        .content(mapper.writeValueAsString(betPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        await().until(() -> jackpotContributionRepository.count() == 1);
        var jackpotContribution = jackpotContributionRepository.findByJackpotId(betPayload.jackpotId()).orElseThrow();
        assertThat(jackpotContribution.getBetId()).isEqualTo(betPayload.betId());
        assertThat(jackpotContribution.getUserId()).isEqualTo(betPayload.userId());
        assertThat(jackpotContribution.getJackpotId()).isEqualTo(betPayload.jackpotId());
        assertThat(jackpotContribution.getStakeAmount()).isEqualByComparingTo(betPayload.betAmount());
        assertThat(jackpotContribution.getCurrentJackpotAmount()).isEqualByComparingTo(initialJackpotBalance.add(betPayload.betAmount().multiply(fixedContributionRate)));
        assertThat(jackpotContribution.getContributionAmount()).isEqualByComparingTo(betPayload.betAmount().multiply(fixedContributionRate));
    }

    @Test
    void submitBet_FixedContributionJackpot_JackpotBalanceUpdated() throws Exception {
        var initialBalance = new BigDecimal("1100");
        var jackpot = jackpotRepository.save(JackpotTestBuilder.fixedContributionRate()
                .currentBalance(initialBalance)
                .build());
        var betPayload = new SubmitBetRequestTestBuilder(jackpot.getJackpotId()).build();
        var expectedContribution = betPayload.betAmount().multiply(fixedContributionRate);

        mvc.perform(post("/api/v1/bets/submit")
                        .content(mapper.writeValueAsString(betPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        await().until(() -> jackpotContributionRepository.count() > 0);
        var updatedJackpot = jackpotRepository.findAll().getFirst();
        assertThat(updatedJackpot.getJackpotId()).isEqualTo(jackpot.getJackpotId());
        assertThat(updatedJackpot.getCurrentBalance()).isEqualByComparingTo(initialBalance.add(expectedContribution));
    }

    @Test
    void submitBet_VariableContributionJackpotAndInitialRate_ContributionPersisted() throws Exception {
        var jackpot = jackpotRepository.save(JackpotTestBuilder.variableContributionRate()
                .currentBalance(BigDecimal.ZERO)
                .build());
        var betPayload = new SubmitBetRequestTestBuilder(jackpot.getJackpotId()).build();

        mvc.perform(post("/api/v1/bets/submit")
                        .content(mapper.writeValueAsString(betPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        await().until(() -> jackpotContributionRepository.count() == 1);
        var jackpotContribution = jackpotContributionRepository.findByJackpotId(betPayload.jackpotId()).orElseThrow();
        assertThat(jackpotContribution.getCurrentJackpotAmount()).isEqualByComparingTo(betPayload.betAmount().multiply(variableContributionInitialRate));
        assertThat(jackpotContribution.getContributionAmount()).isEqualByComparingTo(betPayload.betAmount().multiply(variableContributionInitialRate));
    }

    @Test
    void submitBet_VariableContributionJackpotAndInitialRate_JackpotBalanceUpdated() throws Exception {
        var jackpot = jackpotRepository.save(JackpotTestBuilder.variableContributionRate()
                .currentBalance(BigDecimal.ZERO)
                .build());
        var betPayload = new SubmitBetRequestTestBuilder(jackpot.getJackpotId()).build();
        var expectedContribution = betPayload.betAmount().multiply(variableContributionInitialRate);

        mvc.perform(post("/api/v1/bets/submit")
                        .content(mapper.writeValueAsString(betPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        await().until(() -> jackpotContributionRepository.count() > 0);
        var updatedJackpot = jackpotRepository.findAll().getFirst();
        assertThat(updatedJackpot.getJackpotId()).isEqualTo(jackpot.getJackpotId());
        assertThat(updatedJackpot.getCurrentBalance()).isEqualByComparingTo(expectedContribution);
    }

    @Test
    void submitBet_VariableContributionJackpotAndFloorRate_ContributionPersisted() throws Exception {
        var initialJackpotBalance = new BigDecimal("999999999999");
        var jackpot = jackpotRepository.save(JackpotTestBuilder.variableContributionRate()
                .currentBalance(initialJackpotBalance)
                .build());
        var betPayload = new SubmitBetRequestTestBuilder(jackpot.getJackpotId()).build();

        mvc.perform(post("/api/v1/bets/submit")
                        .content(mapper.writeValueAsString(betPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        await().until(() -> jackpotContributionRepository.count() == 1);
        var jackpotContribution = jackpotContributionRepository.findByJackpotId(betPayload.jackpotId()).orElseThrow();
        assertThat(jackpotContribution.getCurrentJackpotAmount()).isEqualByComparingTo(initialJackpotBalance.add(betPayload.betAmount().multiply(variableContributionFloorRate)));
        assertThat(jackpotContribution.getContributionAmount()).isEqualByComparingTo(betPayload.betAmount().multiply(variableContributionFloorRate));
    }

    @Test
    void submitBet_VariableContributionJackpotAndFloorRate_JackpotBalanceUpdated() throws Exception {
        var initialBalance = new BigDecimal("999999999999");
        var jackpot = jackpotRepository.save(JackpotTestBuilder.variableContributionRate()
                .currentBalance(initialBalance)
                .build());
        var betPayload = new SubmitBetRequestTestBuilder(jackpot.getJackpotId()).build();
        var expectedContribution = betPayload.betAmount().multiply(variableContributionFloorRate);

        mvc.perform(post("/api/v1/bets/submit")
                        .content(mapper.writeValueAsString(betPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        await().until(() -> jackpotContributionRepository.count() > 0);
        var updatedJackpot = jackpotRepository.findAll().getFirst();
        assertThat(updatedJackpot.getJackpotId()).isEqualTo(jackpot.getJackpotId());
        assertThat(updatedJackpot.getCurrentBalance()).isEqualByComparingTo(initialBalance.add(expectedContribution));
    }

    @Nested
    @IntegrationTest
    @SpringBootTest(properties = {
            "jackpot.strategies.reward.fixed.win-chance=100",
            "jackpot.strategies.reward.variable.tier1-chance=100"
    })
    class SureWinTests {

        @Value("${jackpot.strategies.reward.variable.tier1-threshold}")
        private BigDecimal variableRewardThreshold;

        @Test
        void evaluateBet_FixedRewardChanceJackpotAndChance100_Won() throws Exception {
            var jackpotBaseBalance = new BigDecimal("100");
            var initialJackpotBalance = new BigDecimal("100");
            var jackpot = jackpotRepository.save(JackpotTestBuilder.fixedRewardChance()
                    .baseAmount(jackpotBaseBalance)
                    .currentBalance(initialJackpotBalance)
                    .build());
            var betPayload = new EvaluateBetRequestTestBuilder(jackpot.getJackpotId()).build();

            var responseAsString = mvc.perform(post("/api/v1/bets/evaluate")
                            .content(mapper.writeValueAsString(betPayload))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            EvaluationResult result = mapper.readValue(responseAsString, EvaluationResult.class);
            assertThat(result.won()).isTrue();
            assertThat(result.betId()).isEqualTo(betPayload.betId());
            assertThat(result.remainingPoolBalance()).isEqualByComparingTo(jackpotBaseBalance);
            assertThat(result.payoutAmount()).isEqualByComparingTo(initialJackpotBalance);
        }

        @Test
        void evaluateBet_VariableRewardChanceJackpotAndChanceForThreshold100_Won() throws Exception {
            var jackpotBaseBalance = new BigDecimal("100");
            var initialJackpotBalance = variableRewardThreshold.add(BigDecimal.ONE);
            var jackpot = jackpotRepository.save(JackpotTestBuilder.variableRewardChance()
                    .baseAmount(jackpotBaseBalance)
                    .currentBalance(initialJackpotBalance)
                    .build());
            var betPayload = new EvaluateBetRequestTestBuilder(jackpot.getJackpotId()).build();

            var responseAsString = mvc.perform(post("/api/v1/bets/evaluate")
                            .content(mapper.writeValueAsString(betPayload))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            EvaluationResult result = mapper.readValue(responseAsString, EvaluationResult.class);
            assertThat(result.won()).isTrue();
            assertThat(result.betId()).isEqualTo(betPayload.betId());
            assertThat(result.remainingPoolBalance()).isEqualByComparingTo(jackpotBaseBalance);
            assertThat(result.payoutAmount()).isEqualByComparingTo(initialJackpotBalance);
        }

        @Test
        void evaluateBet_VariableRewardChanceJackpotAndNotOverSureWinThreshold_Lost() throws Exception {
            var jackpotBaseBalance = new BigDecimal("100");
            var initialJackpotBalance = variableRewardThreshold.subtract(BigDecimal.ONE);
            var jackpot = jackpotRepository.save(JackpotTestBuilder.variableRewardChance()
                    .baseAmount(jackpotBaseBalance)
                    .currentBalance(initialJackpotBalance)
                    .build());
            var betPayload = new EvaluateBetRequestTestBuilder(jackpot.getJackpotId()).build();

            var responseAsString = mvc.perform(post("/api/v1/bets/evaluate")
                            .content(mapper.writeValueAsString(betPayload))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            EvaluationResult result = mapper.readValue(responseAsString, EvaluationResult.class);
            assertThat(result.won()).isFalse();
            assertThat(result.betId()).isEqualTo(betPayload.betId());
            assertThat(result.remainingPoolBalance()).isEqualByComparingTo(initialJackpotBalance);
            assertThat(result.payoutAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @IntegrationTest
    @SpringBootTest(properties = {
            "jackpot.strategies.reward.fixed.win-chance=0",
            "jackpot.strategies.reward.variable.tier1-chance=0"
    })
    class SureLossTests {

        @Value("${jackpot.strategies.reward.variable.tier1-threshold}")
        private BigDecimal variableRewardThreshold;

        @Test
        void evaluateBet_FixedRewardChanceJackpotAndChance0_Lost() throws Exception {
            var initialJackpotBalance = new BigDecimal("100");
            var jackpot = jackpotRepository.save(new JackpotTestBuilder()
                    .currentBalance(initialJackpotBalance)
                    .build());
            var betPayload = new EvaluateBetRequestTestBuilder(jackpot.getJackpotId()).build();

            var responseAsString = mvc.perform(post("/api/v1/bets/evaluate")
                            .content(mapper.writeValueAsString(betPayload))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            EvaluationResult result = mapper.readValue(responseAsString, EvaluationResult.class);
            assertThat(result.won()).isFalse();
            assertThat(result.betId()).isEqualTo(betPayload.betId());
            assertThat(result.remainingPoolBalance()).isEqualByComparingTo(initialJackpotBalance);
            assertThat(result.payoutAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void evaluateBet_VariableRewardChanceJackpotAndSureLossThreshold_Loss() throws Exception {
            var jackpotBaseBalance = new BigDecimal("100");
            var initialJackpotBalance = variableRewardThreshold.add(BigDecimal.ONE);
            var jackpot = jackpotRepository.save(JackpotTestBuilder.variableRewardChance()
                    .baseAmount(jackpotBaseBalance)
                    .currentBalance(initialJackpotBalance)
                    .build());
            var betPayload = new EvaluateBetRequestTestBuilder(jackpot.getJackpotId()).build();

            var responseAsString = mvc.perform(post("/api/v1/bets/evaluate")
                            .content(mapper.writeValueAsString(betPayload))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            EvaluationResult result = mapper.readValue(responseAsString, EvaluationResult.class);
            assertThat(result.won()).isFalse();
            assertThat(result.betId()).isEqualTo(betPayload.betId());
            assertThat(result.remainingPoolBalance()).isEqualByComparingTo(initialJackpotBalance);
            assertThat(result.payoutAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

}
