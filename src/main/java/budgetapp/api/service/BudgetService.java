package budgetapp.api.service;


import budgetapp.api.model.Account;
import budgetapp.api.model.Transaction;
import budgetapp.api.model.Type;
import budgetapp.api.repository.AccountRepository;
import budgetapp.api.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    @Transactional
    public Account createAccount(Account account){
        if(account.getBalance()==null){
            account.setBalance(BigDecimal.ZERO);
        }
        if(account.getBalance().compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Saldo nie moze byc ujemne");
        }
        return accountRepository.save(account);
    }

    public Account findAccountById(Long id){
        return accountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono konta o id: " + id));
    }
    @Transactional
    public void deleteAccount(Long id){
        Account account = findAccountById(id);
        if(!account.getTransactions().isEmpty()){
            throw new IllegalStateException("Nie mozna usunac konta z przypisanymi transakcjami");
        }
        accountRepository.delete(account);
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction){
        Account account=accountRepository.findById(transaction.getAccount().getId()).orElseThrow(()-> new IllegalArgumentException("Nie znaleziono konta o id: " + transaction.getAccount().getId()));
        if(transaction.getType()== Type.EXPENSE){
            if(account.getBalance().compareTo(transaction.getAmount()) < 0 ){
                throw new IllegalStateException("Za malo srodkow na koncie");
            }
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            
        } else if (transaction.getType()== Type.INCOME){
            account.setBalance(account.getBalance().add(transaction.getAmount()));
            
        }
        return transactionRepository.save(transaction);
    }
    public List<Transaction> getFilteredTransactions(LocalDateTime from, LocalDateTime to, String category) {
        Specification<Transaction> spec = (root, query, cb) -> cb.conjunction();

        if (from != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("transactionDate"), from));
        }

        if (to != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("transactionDate"), to));
        }

        if (category != null && !category.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }

        return transactionRepository.findAll(spec);
    }
    @Transactional
    public void deleteTransaction(Long id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("Nie znaleziono transakcji o id: " + id));
        Account account = accountRepository.findById(transaction.getAccount().getId()).orElseThrow(()-> new IllegalArgumentException("Nie znaleziono konta o id: " + transaction.getAccount().getId()));
        if(transaction.getType()== Type.EXPENSE){

            account.setBalance(account.getBalance().add(transaction.getAmount()));

        } else if (transaction.getType()== Type.INCOME){

            if(account.getBalance().compareTo(transaction.getAmount()) < 0){
                throw new IllegalStateException("Nie mozna usunac tej transakcji, bo saldo konta jest za niskie");
            }
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));

        }
        transactionRepository.delete(transaction);
    }
}
