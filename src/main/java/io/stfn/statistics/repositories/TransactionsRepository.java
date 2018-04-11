package io.stfn.statistics.repositories;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.util.Collection;

public interface TransactionsRepository {
    void storeTransaction(long transactionSeconds, double amount);

    @NonNull Collection<Bucket> viewAsAggregatedBuckets();


    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    class Bucket {
        private final long count;
        private final double sum;
        private final double min;
        private final double max;

        /**
         * Constructs a single value bucket
         * @param amount transaction amount
         */
        Bucket(double amount) {
            this.count = 1;
            this.sum = amount;
            this.min = amount;
            this.max = amount;
        }
    }
}
