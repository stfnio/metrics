package io.stfn.statistics.repositories;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransactionsRepositoryImpl implements TransactionsRepository {
    private static final long MILLIS_IN_ONE_SECOND = 1_000L;
    private static final long EVICT_SECONDS_AMOUNT = 61;

    /**
     * At any moment of time storage will have 60 elements at maximum, therefore
     * we can surely state that statistics calculation will execute in O(60) -> O(1).
     */
    private final Map<Long, Bucket> storage = new ConcurrentHashMap<>(60);

    @Override
    public void storeTransaction(long transactionSeconds, double amount) {
        storage.merge(transactionSeconds,
                new Bucket(amount),
                (oldBucket, newBucket) -> new Bucket(
                        oldBucket.getCount() + 1,
                        oldBucket.getSum() + amount,
                        amount < oldBucket.getMin() ? amount : oldBucket.getMin(),
                        amount > oldBucket.getMax() ? amount : oldBucket.getMax())
        );
    }

    @Override
    public Collection<Bucket> viewAsAggregatedBuckets() {
        return storage.values();
    }

    @Scheduled(fixedRate = MILLIS_IN_ONE_SECOND)
    private void cleaner() {
        Long nowSeconds = System.currentTimeMillis() / MILLIS_IN_ONE_SECOND;
        storage.remove(nowSeconds - EVICT_SECONDS_AMOUNT);
    }
}
