package io.stfn.statistics.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.stfn.statistics.repositories.TransactionsRepository;
import io.stfn.statistics.services.StatisticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StatisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionsRepository transactionsRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void valid_statistics_is_produced_from_recent_transactions() throws Exception {
        long currentSeconds = System.currentTimeMillis() / 1_000L;
        transactionsRepository.storeTransaction(currentSeconds, 50d);
        transactionsRepository.storeTransaction(currentSeconds, 200d);
        transactionsRepository.storeTransaction(currentSeconds, 50d);

        StatisticsService.Statistics expected = StatisticsService.Statistics.builder()
                .count(3)
                .max(200)
                .min(50)
                .sum(300)
                .avg(100)
                .build();

        MockHttpServletResponse response = mockMvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    public void valid_statistics_is_produced_from_past_transactions() throws Exception {
        long halfAMinuteAgoSeconds = (System.currentTimeMillis() - 30_000L) / 1_000L;
        transactionsRepository.storeTransaction(halfAMinuteAgoSeconds, 100d);
        transactionsRepository.storeTransaction(halfAMinuteAgoSeconds, 200d);

        StatisticsService.Statistics expected = StatisticsService.Statistics.builder()
                .count(2)
                .max(200)
                .min(100)
                .sum(300)
                .avg(150)
                .build();

        MockHttpServletResponse response = mockMvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expected));
    }
}