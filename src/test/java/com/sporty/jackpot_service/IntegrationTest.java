package com.sporty.jackpot_service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners(listeners = TestExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        topics = {"jackpot-bets"}, // automatically handles creating the topic for tests
        brokerProperties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"}
)
public @interface IntegrationTest {
}
