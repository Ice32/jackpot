-- @formatter:off
INSERT INTO contribution_configuration (
    id, strategy_type, rate, initial_rate, decay_step, decay_rate, floor_rate
)
VALUES
    (1, 'VARIABLE', NULL, 0.30, 1000.00, 0.005, 0.05),
    (2, 'FIXED', 0.10, NULL, NULL, NULL, NULL),
    (3, 'VARIABLE', NULL, 0.25, 2500.00, 0.002, 0.04);

INSERT INTO reward_configuration (
    id, strategy_type, win_chance, base_chance, tier1_threshold, tier1_chance,
    tier2_threshold, tier2_chance, pool_limit
)
VALUES
    (1, 'VARIABLE', NULL, 5.00, 5000.00, 10.00, 10000.00, 20.00, 25000.00),
    (2, 'FIXED', 5.00, NULL, NULL, NULL, NULL, NULL, NULL),
    (3, 'FIXED', 8.00, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO jackpot (
    jackpot_id, name, current_balance, base_amount,
    contribution_configuration_id, reward_configuration_id, version
)
VALUES
    ('JACKPOT-123', '💥 Mega Progressive', 5000.00, 1000.00, 1, 1, 0),
    ('JACKPOT-456', '⚡ Lightning Strike', 250.00, 250.00, 2, 2, 0),
    ('JACKPOT-789', '🔮 High Roller Club', 25000.00, 10000.00, 3, 3, 0);
-- @formatter:on