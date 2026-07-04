package com.sporty.jackpot_service.controller;

import com.sporty.jackpot_service.dto.request.BetPayload;
import com.sporty.jackpot_service.producer.JackpotBetProducer;
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

    @PostMapping("/submit")
    public void submitBet(@Valid @RequestBody BetPayload request) {
        producer.publishBet(request);
    }

    @PostMapping("/evaluate")
    public void evaluateBet() {
    }
}
