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
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        testAccount.setTransactions(new ArrayList<>());
        ReflectionTestUtils.setField(testAccount, "id", 1L);
    }

    // TEST 1: DODAWANIE WYDATKU
    @Test
    void shouldDecreaseBalanceWhenAddingExpense() {
        Transaction expense = new Transaction(testAccount, new BigDecimal("100.00"), Type.EXPENSE, "Jedzenie", "Zakupy");

        // FIX: Dodano brakujący mock dla weryfikacji konta w createTransaction
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = budgetService.createTransaction(expense);

        assertNotNull(result);
        assertEquals(0, new BigDecimal("400.00").compareTo(testAccount.getBalance()));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // Test 2: BLOKADA DODANIA WYDATKU PRZEKRACZAJĄCEGO SALDO
    @Test
    void shouldThrowExceptionWhenExpenseIsGreaterThanBalance() {
        Transaction expensiveTransaction = new Transaction(testAccount, new BigDecimal("600.00"), Type.EXPENSE, "Elektronika", "Nowy telefon");

        // FIX: Dodano brakujący mock dla weryfikacji konta w createTransaction
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            budgetService.createTransaction(expensiveTransaction);
        });

        assertEquals("Za malo srodkow na koncie", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    // TEST 3: DODAWANIE PRZYCHODU
    @Test
    void shouldIncreaseBalanceWhenAddingIncome() {
        Transaction income = new Transaction(testAccount, new BigDecimal("250.00"), Type.INCOME, "Pensja", "Wypłata");

        // FIX: Dodano brakujący mock dla weryfikacji konta w createTransaction
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = budgetService.createTransaction(income);

        assertNotNull(result);
        assertEquals(0, new BigDecimal("750.00").compareTo(testAccount.getBalance()));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // TEST 4: BLOKADA USUNIĘCIA KONTA Z TRANSAKCJAMI
    @Test
    void shouldThrowExceptionWhenDeletingAccountWithTransactions() {
        Transaction fakeTransaction = new Transaction();
        testAccount.getTransactions().add(fakeTransaction);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            budgetService.deleteAccount(1L);
        });

        assertEquals("Nie mozna usunac konta z przypisanymi transakcjami", exception.getMessage());
        verify(accountRepository, never()).delete(any(Account.class));
    }

    // TEST 5: ZWROT ŚRODKÓW PO USUNIĘCIU WYDATKU
    @Test
    void shouldRestoreBalanceWhenDeletingExpenseTransaction() {
        Transaction expenseToDelete = new Transaction(testAccount, new BigDecimal("100.00"), Type.EXPENSE, "Rozrywka", "Kino");

        when(transactionRepository.findById(10L)).thenReturn(Optional.of(expenseToDelete));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        budgetService.deleteTransaction(10L);

        assertEquals(0, new BigDecimal("600.00").compareTo(testAccount.getBalance()));
        verify(transactionRepository, times(1)).delete(expenseToDelete);
    }
}