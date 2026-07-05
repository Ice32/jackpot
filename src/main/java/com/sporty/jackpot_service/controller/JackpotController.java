package com.sporty.jackpot_service.controller;

import com.sporty.jackpot_service.model.Jackpot;
import com.sporty.jackpot_service.repository.JackpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bets")
@RequiredArgsConstructor
public class JackpotController {     // only for the UI demo

    private final JackpotRepository jackpotRepository;

    @GetMapping("/pools")
    public List<Jackpot> getAllJackpotPools() {
        return jackpotRepository.findAll();
    }
}
