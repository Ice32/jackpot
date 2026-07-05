package com.sporty.jackpot_service.controller;

import com.sporty.jackpot_service.dto.JackpotResponse;
import com.sporty.jackpot_service.repository.JackpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jackpots")
@RequiredArgsConstructor
public class JackpotController {     // only for the UI demo

    private final JackpotRepository jackpotRepository;

    @GetMapping
    public List<JackpotResponse> getAllJackpots() {
        return jackpotRepository.findAll().stream()
                .map(jackpot -> new JackpotResponse(
                        jackpot.getJackpotId(),
                        jackpot.getName(),
                        jackpot.getCurrentBalance()
                ))
                .toList();
    }
}
