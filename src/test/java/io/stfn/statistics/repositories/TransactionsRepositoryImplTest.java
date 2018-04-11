package io.stfn.statistics.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TransactionsRepositoryImplTest {
    @Autowired
    private TransactionsRepository transactionsRepository;

    @Test
    public void transaction_stores() {
        transactionsRepository.storeTransaction(System.currentTimeMillis() / 1000L, 100d);
        Collection<TransactionsRepository.Bucket> buckets = transactionsRepository.viewAsAggregatedBuckets();

        assertThat(buckets.size()).isEqualTo(1);
        assertThat(buckets.iterator().next()).isEqualTo(new TransactionsRepository.Bucket(100d));
    }

    @Test
    @SuppressWarnings("AccessStaticViaInstance")
    public void transaction_evicts() throws InterruptedException {
        transactionsRepository.storeTransaction((System.currentTimeMillis() - 60_000L) / 1000L, 100d);
        Collection<TransactionsRepository.Bucket> buckets = transactionsRepository.viewAsAggregatedBuckets();

        assertThat(buckets.size()).isEqualTo(1);
        assertThat(buckets.iterator().next()).isEqualTo(new TransactionsRepository.Bucket(100d));

        Thread.currentThread().sleep(2000L);

        assertThat(buckets.size()).isEqualTo(0);
    }

    @Test
    public void transactions_are_merged_correctly() {
        long currentSeconds = System.currentTimeMillis() / 1000L;
        transactionsRepository.storeTransaction(currentSeconds, 100d);
        transactionsRepository.storeTransaction(currentSeconds, 200d);

        Collection<TransactionsRepository.Bucket> buckets = transactionsRepository.viewAsAggregatedBuckets();
        assertThat(buckets.size()).isEqualTo(1);
        assertThat(buckets.iterator().next()).isEqualTo(new TransactionsRepository.Bucket(2, 300, 100, 200));
    }

}