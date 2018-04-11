package io.stfn.statistics.services;

import io.stfn.statistics.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final TransactionsRepository transactionsRepository;

    @Autowired
    public StatisticsServiceImpl(TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    @Override
    public Statistics calculateStatistics() {
        Collection<TransactionsRepository.Bucket> buckets = transactionsRepository.viewAsAggregatedBuckets();
        if (buckets.isEmpty()) {
            return Statistics.EMPTY;
        }

        long count = 0L;
        double sum = 0d, min = Double.MAX_VALUE, max = Double.MIN_VALUE;

        for (TransactionsRepository.Bucket bucket : buckets) {
            count += bucket.getCount();
            sum += bucket.getSum();
            if (bucket.getMin() < min) min = bucket.getMin();
            if (bucket.getMax() > max) max = bucket.getMax();
        }
        return Statistics.builder()
                .sum(sum)
                .avg(sum / (double) count)
                .min(min)
                .max(max)
                .count(count)
                .build();
    }




}
