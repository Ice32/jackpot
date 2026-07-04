package com.sporty.jackpot_service.controller;

import com.sporty.jackpot_service.dto.EvaluateBetRequest;
import com.sporty.jackpot_service.dto.SubmitBetRequest;
import com.sporty.jackpot_service.dto.EvaluationResult;
import com.sporty.jackpot_service.producer.JackpotBetProducer;
import com.sporty.jackpot_service.service.BetEvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bets")
@RequiredArgsConstructor
public class BetController {

    private final JackpotBetProducer producer;
    private final BetEvaluationService evaluationService;

    @PostMapping("/submit")
    public void submitBet(@Valid @RequestBody SubmitBetRequest request) {
        producer.publishBet(request);
    }

    @PostMapping("/evaluate")
    public EvaluationResult evaluateBet(@Valid @RequestBody EvaluateBetRequest request) {
        return evaluationService.evaluateBet(request);
    }
}
