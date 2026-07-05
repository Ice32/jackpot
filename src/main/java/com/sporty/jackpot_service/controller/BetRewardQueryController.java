package com.sporty.jackpot_service.controller;

import com.sporty.jackpot_service.model.JackpotReward;
import com.sporty.jackpot_service.repository.JackpotRewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bets")
@RequiredArgsConstructor
public class BetRewardQueryController {

    private final JackpotRewardRepository rewardRepository;

    @GetMapping("/rewards")
    public List<JackpotReward> getRecentRewards() {
        return rewardRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
