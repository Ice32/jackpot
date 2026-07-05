package com.sporty.jackpot_service.controller;

import com.sporty.jackpot_service.IntegrationTest;
import com.sporty.jackpot_service.dto.EvaluationResult;
import com.sporty.jackpot_service.dto.request.EvaluateBetRequestTestBuilder;
import com.sporty.jackpot_service.dto.request.SubmitBetRequestTestBuilder;
import com.sporty.jackpot_service.model.JackpotContributionTestBuilder;
import com.sporty.jackpot_service.model.JackpotTestBuilder;
import com.sporty.jackpot_service.repository.JackpotContributionRepository;
import com.sporty.jackpot_service.repository.JackpotRepository;
import com.sporty.jackpot_service.repository.JackpotRewardRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class BetControllerIntegrationTest {

    private static final BigDecimal FIXED_CONTRIBUTION_RATE = new BigDecimal("0.10");
    private static final BigDecimal VARIABLE_CONTRIBUTION_INITIAL_RATE = new BigDecimal("0.30");
    private static final BigDecimal VARIABLE_CONTRIBUTION_FLOOR_RATE = new BigDecimal("0.05");
    private static final BigDecimal VARIABLE_REWARD_POOL_LIMIT = new BigDecimal("25000.00");
    private static final BigDecimal VARIABLE_REWARD_TIER1_THRESHOLD = new BigDecimal("5000.00");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JsonMapper mapper;

    @Autowired
    private JackpotRepository jackpotRepository;

    @Autowired
    private JackpotContributionRepository jackpotContributionRepository;

    @Autowired
    private JackpotRewardRepository rewardRepository;

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
                .andExpect(status().isAccepted());

        await().until(() -> jackpotContributionRepository.count() == 1);
        var jackpotContribution = jackpotContributionRepository.findByJackpotId(betPayload.jackpotId()).orElseThrow();
        assertThat(jackpotContribution.getBetId()).isEqualTo(betPayload.betId());
        assertThat(jackpotContribution.getUserId()).isEqualTo(betPayload.userId());
        assertThat(jackpotContribution.getJackpotId()).isEqualTo(betPayload.jackpotId());
        assertThat(jackpotContribution.getStakeAmount()).isEqualByComparingTo(betPayload.betAmount());
        assertThat(jackpotContribution.getCurrentJackpotAmount()).isEqualByComparingTo(initialJackpotBalance.add(betPayload.betAmount().multiply(FIXED_CONTRIBUTION_RATE)));
        assertThat(jackpotContribution.getContributionAmount()).isEqualByComparingTo(betPayload.betAmount().multiply(FIXED_CONTRIBUTION_RATE));
    }

    @Test
    void submitBet_FixedContributionJackpot_JackpotBalanceUpdated() throws Exception {
        var initialBalance = new BigDecimal("1100");
        var jackpot = jackpotRepository.save(JackpotTestBuilder.fixedContributionRate()
                .currentBalance(initialBalance)
                .build());
        var betPayload = new SubmitBetRequestTestBuilder(jackpot.getJackpotId()).build();
        var expectedContribution = betPayload.betAmount().multiply(FIXED_CONTRIBUTION_RATE);

        mvc.perform(post("/api/v1/bets/submit")
                        .content(mapper.writeValueAsString(betPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        await().until(() -> jackpotContributionRepository.count() > 0);
        var updatedJackpot = jackpotRepository.findAll().getFirst();
        assertThat(updatedJackpot.getJackpotId()).isEqualTo(jackpot.getJackpotId());
        assertThat(updatedJackpot.getCurrentBalance()).isEqualByComparingTo(initialBalance.add(expectedContribution));
    }

    @Test
    void submitBet_FixedContributionJackpot_UsesJackpotSpecificRate() throws Exception {
        var jackpotSpecificRate = new BigDecimal("0.20");
        var jackpot = jackpotRepository.save(JackpotTestBuilder.fixedContributionRate()
                .fixedContributionRate(jackpotSpecificRate)
                .build());
        var betPayload = new SubmitBetRequestTestBuilder(jackpot.getJackpotId()).build();

        mvc.perform(post("/api/v1/bets/submit")
                        .content(mapper.writeValueAsString(betPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        await().until(() -> jackpotContributionRepository.count() == 1);
        var jackpotContribution = jackpotContributionRepository.findByJackpotId(betPayload.jackpotId()).orElseThrow();
        assertThat(jackpotContribution.getContributionAmount()).isEqualByComparingTo(betPayload.betAmount().multiply(jackpotSpecificRate));
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
                .andExpect(status().isAccepted());

        await().until(() -> jackpotContributionRepository.count() == 1);
        var jackpotContribution = jackpotContributionRepository.findByJackpotId(betPayload.jackpotId()).orElseThrow();
        assertThat(jackpotContribution.getCurrentJackpotAmount()).isEqualByComparingTo(betPayload.betAmount().multiply(VARIABLE_CONTRIBUTION_INITIAL_RATE));
        assertThat(jackpotContribution.getContributionAmount()).isEqualByComparingTo(betPayload.betAmount().multiply(VARIABLE_CONTRIBUTION_INITIAL_RATE));
    }

    @Test
    void submitBet_VariableContributionJackpotAndInitialRate_JackpotBalanceUpdated() throws Exception {
        var jackpot = jackpotRepository.save(JackpotTestBuilder.variableContributionRate()
                .currentBalance(BigDecimal.ZERO)
                .build());
        var betPayload = new SubmitBetRequestTestBuilder(jackpot.getJackpotId()).build();
        var expectedContribution = betPayload.betAmount().multiply(VARIABLE_CONTRIBUTION_INITIAL_RATE);

        mvc.perform(post("/api/v1/bets/submit")
                        .content(mapper.writeValueAsString(betPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

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
                .andExpect(status().isAccepted());

        await().until(() -> jackpotContributionRepository.count() == 1);
        var jackpotContribution = jackpotContributionRepository.findByJackpotId(betPayload.jackpotId()).orElseThrow();
        assertThat(jackpotContribution.getCurrentJackpotAmount()).isEqualByComparingTo(initialJackpotBalance.add(betPayload.betAmount().multiply(VARIABLE_CONTRIBUTION_FLOOR_RATE)));
        assertThat(jackpotContribution.getContributionAmount()).isEqualByComparingTo(betPayload.betAmount().multiply(VARIABLE_CONTRIBUTION_FLOOR_RATE));
    }

    @Test
    void submitBet_VariableContributionJackpotAndFloorRate_JackpotBalanceUpdated() throws Exception {
        var initialBalance = new BigDecimal("999999999999");
        var jackpot = jackpotRepository.save(JackpotTestBuilder.variableContributionRate()
                .currentBalance(initialBalance)
                .build());
        var betPayload = new SubmitBetRequestTestBuilder(jackpot.getJackpotId()).build();
        var expectedContribution = betPayload.betAmount().multiply(VARIABLE_CONTRIBUTION_FLOOR_RATE);

        mvc.perform(post("/api/v1/bets/submit")
                        .content(mapper.writeValueAsString(betPayload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        await().until(() -> jackpotContributionRepository.count() > 0);
        var updatedJackpot = jackpotRepository.findAll().getFirst();
        assertThat(updatedJackpot.getJackpotId()).isEqualTo(jackpot.getJackpotId());
        assertThat(updatedJackpot.getCurrentBalance()).isEqualByComparingTo(initialBalance.add(expectedContribution));
    }

    @Test
    void evaluateBet_NonExistentBet_400() throws Exception {
        var evaluationRequest = new EvaluateBetRequestTestBuilder(UUID.randomUUID().toString()).build();

        mvc.perform(post("/api/v1/bets/evaluate")
                        .content(mapper.writeValueAsString(evaluationRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void evaluateBet_VariableRewardChanceJackpotAndPoolLimitHit_Won() throws Exception {
        var jackpotBaseBalance = new BigDecimal("100");
        var initialJackpotBalance = VARIABLE_REWARD_POOL_LIMIT.add(BigDecimal.ONE);
        var jackpot = jackpotRepository.save(JackpotTestBuilder.variableRewardChance()
                .baseAmount(jackpotBaseBalance)
                .currentBalance(initialJackpotBalance)
                .build());
        var jackpotContribution = jackpotContributionRepository.save(
                new JackpotContributionTestBuilder(jackpot.getJackpotId()).build()
        );
        var evaluationRequest = new EvaluateBetRequestTestBuilder(jackpotContribution).build();

        var responseAsString = mvc.perform(post("/api/v1/bets/evaluate")
                        .content(mapper.writeValueAsString(evaluationRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        EvaluationResult result = mapper.readValue(responseAsString, EvaluationResult.class);
        assertThat(result.won()).isTrue();
        assertThat(result.betId()).isEqualTo(evaluationRequest.betId());
        assertThat(result.remainingPoolBalance()).isEqualByComparingTo(jackpotBaseBalance);
        assertThat(result.payoutAmount()).isEqualByComparingTo(initialJackpotBalance);
        var rewardRecord = rewardRepository.findAll().getFirst();
        assertThat(rewardRecord.getBetId()).isEqualTo(evaluationRequest.betId());
        assertThat(rewardRecord.getUserId()).isEqualTo(jackpotContribution.getUserId());
        assertThat(rewardRecord.getJackpotId()).isEqualTo(jackpot.getJackpotId());
        assertThat(rewardRecord.getJackpotRewardAmount()).isEqualByComparingTo(initialJackpotBalance);
    }

    @Test
    void evaluateBet_AlreadyEvaluatedWinningBet_409AndNoDuplicateReward() throws Exception {
        var jackpot = jackpotRepository.save(JackpotTestBuilder.variableRewardChance()
                .currentBalance(VARIABLE_REWARD_POOL_LIMIT.add(BigDecimal.ONE))
                .build());
        var jackpotContribution = jackpotContributionRepository.save(
                new JackpotContributionTestBuilder(jackpot.getJackpotId()).build()
        );
        var evaluationRequest = new EvaluateBetRequestTestBuilder(jackpotContribution).build();

        mvc.perform(post("/api/v1/bets/evaluate")
                        .content(mapper.writeValueAsString(evaluationRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(post("/api/v1/bets/evaluate")
                        .content(mapper.writeValueAsString(evaluationRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        assertThat(rewardRepository.count()).isEqualTo(1);
    }

        @Test
        void evaluateBet_FixedRewardChanceJackpotAndChance100_Won() throws Exception {
            var jackpotBaseBalance = new BigDecimal("100");
            var initialJackpotBalance = new BigDecimal("100");
            var jackpot = jackpotRepository.save(JackpotTestBuilder.fixedRewardChance()
                    .baseAmount(jackpotBaseBalance)
                    .currentBalance(initialJackpotBalance)
                    .fixedRewardWinChance(new BigDecimal("100"))
                    .build());
            var jackpotContribution = jackpotContributionRepository.save(
                    new JackpotContributionTestBuilder(jackpot.getJackpotId()).build()
            );
            var evaluationRequest = new EvaluateBetRequestTestBuilder(jackpotContribution).build();

            var responseAsString = mvc.perform(post("/api/v1/bets/evaluate")
                            .content(mapper.writeValueAsString(evaluationRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            EvaluationResult result = mapper.readValue(responseAsString, EvaluationResult.class);
            assertThat(result.won()).isTrue();
            assertThat(result.betId()).isEqualTo(evaluationRequest.betId());
            assertThat(result.remainingPoolBalance()).isEqualByComparingTo(jackpotBaseBalance);
            assertThat(result.payoutAmount()).isEqualByComparingTo(initialJackpotBalance);
            assertThat(rewardRepository.count()).isEqualTo(1);
        }

        @Test
        void evaluateBet_VariableRewardChanceJackpotAndChanceForThreshold100_Won() throws Exception {
            var jackpotBaseBalance = new BigDecimal("100");
            var initialJackpotBalance = VARIABLE_REWARD_TIER1_THRESHOLD.add(BigDecimal.ONE);
            var jackpot = jackpotRepository.save(JackpotTestBuilder.variableRewardChance()
                    .baseAmount(jackpotBaseBalance)
                    .currentBalance(initialJackpotBalance)
                    .variableRewardBaseChance(BigDecimal.ZERO)
                    .variableRewardTier1Chance(new BigDecimal("100"))
                    .build());
            var jackpotContribution = jackpotContributionRepository.save(
                    new JackpotContributionTestBuilder(jackpot.getJackpotId()).build()
            );
            var evaluationRequest = new EvaluateBetRequestTestBuilder(jackpotContribution).build();

            var responseAsString = mvc.perform(post("/api/v1/bets/evaluate")
                            .content(mapper.writeValueAsString(evaluationRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            EvaluationResult result = mapper.readValue(responseAsString, EvaluationResult.class);
            assertThat(result.won()).isTrue();
            assertThat(result.betId()).isEqualTo(evaluationRequest.betId());
            assertThat(result.remainingPoolBalance()).isEqualByComparingTo(jackpotBaseBalance);
            assertThat(result.payoutAmount()).isEqualByComparingTo(initialJackpotBalance);
            assertThat(rewardRepository.count()).isEqualTo(1);
        }

        @Test
        void evaluateBet_VariableRewardChanceJackpotAndNotOverSureWinThreshold_Lost() throws Exception {
            var jackpotBaseBalance = new BigDecimal("100");
            var initialJackpotBalance = VARIABLE_REWARD_TIER1_THRESHOLD.subtract(BigDecimal.ONE);
            var jackpot = jackpotRepository.save(JackpotTestBuilder.variableRewardChance()
                    .baseAmount(jackpotBaseBalance)
                    .currentBalance(initialJackpotBalance)
                    .variableRewardBaseChance(BigDecimal.ZERO)
                    .variableRewardTier1Chance(new BigDecimal("100"))
                    .build());
            var jackpotContribution = jackpotContributionRepository.save(
                    new JackpotContributionTestBuilder(jackpot.getJackpotId()).build()
            );
            var evaluationRequest = new EvaluateBetRequestTestBuilder(jackpotContribution).build();

            var responseAsString = mvc.perform(post("/api/v1/bets/evaluate")
                            .content(mapper.writeValueAsString(evaluationRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            EvaluationResult result = mapper.readValue(responseAsString, EvaluationResult.class);
            assertThat(result.won()).isFalse();
            assertThat(result.betId()).isEqualTo(evaluationRequest.betId());
            assertThat(result.remainingPoolBalance()).isEqualByComparingTo(initialJackpotBalance);
            assertThat(result.payoutAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(rewardRepository.count()).isZero();
        }

        @Test
        void evaluateBet_FixedRewardChanceJackpotAndChance0_Lost() throws Exception {
            var initialJackpotBalance = new BigDecimal("100");
            var jackpot = jackpotRepository.save(new JackpotTestBuilder()
                    .currentBalance(initialJackpotBalance)
                    .fixedRewardWinChance(BigDecimal.ZERO)
                    .build());
            var jackpotContribution = jackpotContributionRepository.save(
                    new JackpotContributionTestBuilder(jackpot.getJackpotId()).build()
            );
            var evaluationRequest = new EvaluateBetRequestTestBuilder(jackpotContribution).build();

            var responseAsString = mvc.perform(post("/api/v1/bets/evaluate")
                            .content(mapper.writeValueAsString(evaluationRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            EvaluationResult result = mapper.readValue(responseAsString, EvaluationResult.class);
            assertThat(result.won()).isFalse();
            assertThat(result.betId()).isEqualTo(evaluationRequest.betId());
            assertThat(result.remainingPoolBalance()).isEqualByComparingTo(initialJackpotBalance);
            assertThat(result.payoutAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(rewardRepository.count()).isZero();
        }

        @Test
        void evaluateBet_AlreadyEvaluatedLosingBet_409() throws Exception {
            var jackpot = jackpotRepository.save(new JackpotTestBuilder()
                    .currentBalance(new BigDecimal("100"))
                    .fixedRewardWinChance(BigDecimal.ZERO)
                    .build());
            var jackpotContribution = jackpotContributionRepository.save(
                    new JackpotContributionTestBuilder(jackpot.getJackpotId()).build()
            );
            var evaluationRequest = new EvaluateBetRequestTestBuilder(jackpotContribution).build();

            mvc.perform(post("/api/v1/bets/evaluate")
                            .content(mapper.writeValueAsString(evaluationRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            mvc.perform(post("/api/v1/bets/evaluate")
                            .content(mapper.writeValueAsString(evaluationRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict());

            assertThat(rewardRepository.count()).isZero();
        }

        @Test
        void evaluateBet_VariableRewardChanceJackpotAndSureLossThreshold_Loss() throws Exception {
            var jackpotBaseBalance = new BigDecimal("100");
            var initialJackpotBalance = VARIABLE_REWARD_TIER1_THRESHOLD.add(BigDecimal.ONE);
            var jackpot = jackpotRepository.save(JackpotTestBuilder.variableRewardChance()
                    .baseAmount(jackpotBaseBalance)
                    .currentBalance(initialJackpotBalance)
                    .variableRewardTier1Chance(BigDecimal.ZERO)
                    .build());
            var jackpotContribution = jackpotContributionRepository.save(
                    new JackpotContributionTestBuilder(jackpot.getJackpotId()).build()
            );
            var evaluationRequest = new EvaluateBetRequestTestBuilder(jackpotContribution).build();

            var responseAsString = mvc.perform(post("/api/v1/bets/evaluate")
                            .content(mapper.writeValueAsString(evaluationRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            EvaluationResult result = mapper.readValue(responseAsString, EvaluationResult.class);
            assertThat(result.won()).isFalse();
            assertThat(result.betId()).isEqualTo(evaluationRequest.betId());
            assertThat(result.remainingPoolBalance()).isEqualByComparingTo(initialJackpotBalance);
            assertThat(result.payoutAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(rewardRepository.count()).isZero();
        }

}
