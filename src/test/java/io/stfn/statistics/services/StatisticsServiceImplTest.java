package io.stfn.statistics.services;

import io.stfn.statistics.repositories.TransactionsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StatisticsServiceImplTest {
    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private StatisticsService statisticsService;

    @Test
    public void statistics_is_calculated_correctly() {
        long currentSeconds = System.currentTimeMillis() / 1000L;
        transactionsRepository.storeTransaction(currentSeconds, 100d);
        transactionsRepository.storeTransaction(currentSeconds, 200d);

        long someTimeInThePastSeconds = (System.currentTimeMillis() - 29_000L) / 1000L;
        transactionsRepository.storeTransaction(someTimeInThePastSeconds, 50d);

        long minuteAgoSeconds = (System.currentTimeMillis() - 60_000L) / 1000L;
        transactionsRepository.storeTransaction(minuteAgoSeconds, 50d);

        StatisticsService.Statistics expected = StatisticsService.Statistics.builder()
                .count(4)
                .max(200)
                .min(50)
                .sum(400)
                .avg(100)
                .build();

        StatisticsService.Statistics statistics = statisticsService.calculateStatistics();
        assertThat(statistics).isEqualTo(expected);
    }
}