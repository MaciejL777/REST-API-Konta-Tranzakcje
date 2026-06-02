package budgetapp.api.service;


import budgetapp.api.model.Account;
import budgetapp.api.model.Transaction;
import budgetapp.api.model.Type;
import budgetapp.api.repository.AccountRepository;
import budgetapp.api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceTest {
    @Mock
    AccountRepository accountRepository;
    @Mock
    TransactionRepository transactionRepository;
    @InjectMocks
    BudgetService budgetService;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account("Test", BigDecimal.valueOf(500.00));
        ReflectionTestUtils.setField(testAccount, "id", 1L);
    }

    @Test
    void shouldDecreaseBalanceWhenAddingExpense() {
        Transaction expense = new Transaction(testAccount, new BigDecimal("100.00"), Type.EXPENSE, "Jedzenie", "Zakupy");

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = budgetService.createTransaction(expense);

        assertNotNull(result);
        // Sprawdzamy czy z 500 zł odjęło 100 zł i zostało 400 zł
        assertEquals(0, new BigDecimal("400.00").compareTo(testAccount.getBalance()));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}