package budgetapp.api;

import budgetapp.api.model.Account;
import budgetapp.api.model.Transaction;
import budgetapp.api.model.Type;
import budgetapp.api.repository.AccountRepository;
import budgetapp.api.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BudgetAppBaseFunctionsIT {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    jakarta.persistence.EntityManager entityManager;

    @Test
    @Transactional
    void shouldPerformFullCrudAndRelationalWorkflowInPostgres() {

        Account newAccount = new Account();
        newAccount.setName("Konto Testowe");
        newAccount.setBalance(new BigDecimal("500.00"));

        Account savedAccount = accountRepository.saveAndFlush(newAccount);

        assertNotNull(savedAccount.getId(), "Postgres powinien automatycznie nadać ID z sekwencji");
        assertEquals("Konto Testowe", savedAccount.getName());
        assertEquals(0, savedAccount.getVersion(), "Nowa encja powinna mieć wersję 0");


        Optional<Account> fetchedAccountOpt = accountRepository.findById(savedAccount.getId());
        assertTrue(fetchedAccountOpt.isPresent());
        Account fetchedAccount = fetchedAccountOpt.get();

        Transaction expense = new Transaction();
        expense.setAccount(fetchedAccount);
        expense.setAmount(new BigDecimal("50.00"));
        expense.setType(Type.EXPENSE);
        expense.setCategory("Rozrywka");
        expense.setDescription("Kino ze znajomymi");

        Transaction savedTransaction = transactionRepository.saveAndFlush(expense);
        assertNotNull(savedTransaction.getId(), "Transakcja powinna otrzymać własne ID z bazy");

        fetchedAccount.setBalance(fetchedAccount.getBalance().subtract(savedTransaction.getAmount()));
        accountRepository.saveAndFlush(fetchedAccount);


        Account updatedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();

        assertEquals(new BigDecimal("450.00"), updatedAccount.getBalance());
        assertEquals(1, updatedAccount.getVersion(), "Wersja konta powinna wzrosnąć do 1 po aktualizacji balansu");


        transactionRepository.saveAndFlush(expense);

        entityManager.clear();
        List<Transaction> accountTransactions = accountRepository.findById(updatedAccount.getId()).get().getTransactions();
        assertFalse(accountTransactions.isEmpty(), "Lista transakcji dla tego konta nie powinna być pusta");
        assertEquals(1, accountTransactions.size());
        assertEquals("Kino ze znajomymi", accountTransactions.get(0).getDescription());


        transactionRepository.delete(savedTransaction);
        accountRepository.delete(updatedAccount);

        assertFalse(transactionRepository.findById(savedTransaction.getId()).isPresent());
        assertFalse(accountRepository.findById(savedAccount.getId()).isPresent());
    }
}

