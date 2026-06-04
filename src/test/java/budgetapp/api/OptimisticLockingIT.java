package budgetapp.api;

import budgetapp.api.model.Account;
import budgetapp.api.repository.AccountRepository;
import budgetapp.api.service.BudgetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OptimisticLockingIT {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void shouldThrowOptimisticLockingExceptionWhenConcurrentUpdatesOccur() throws Exception {
        Account account = new Account();
        account.setName("Konto");
        account.setBalance(new BigDecimal("1000.00"));
        account = accountRepository.save(account);

        Long accountId = account.getId();

        Account accountUser1 = accountRepository.findById(accountId).orElseThrow();
        Account accountUser2 = accountRepository.findById(accountId).orElseThrow();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<?> future1 = executor.submit(() -> {
            accountUser1.setBalance(new BigDecimal("1100.00"));
            accountRepository.saveAndFlush(accountUser1);
        });

        future1.get();

        Future<?> future2 = executor.submit(() -> {
            accountUser2.setBalance(new BigDecimal("1200.00"));
            accountRepository.saveAndFlush(accountUser2);
        });

        Exception exception = assertThrows(Exception.class, future2::get);

        assertTrue(exception.getCause() instanceof ObjectOptimisticLockingFailureException);

        accountRepository.deleteById(accountId);
        executor.shutdown();
    }
}