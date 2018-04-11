package io.stfn.statistics.services;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.NonNull;

public interface StatisticsService {
    @NonNull Statistics calculateStatistics();

    @Getter
    @Builder
    @EqualsAndHashCode
    class Statistics {
        private final double sum;
        private final double avg;
        private final double min;
        private final double max;
        private final long count;

        public static final Statistics EMPTY = Statistics.builder().sum(0).avg(0).min(0).max(0).count(0).build();
    }
}
