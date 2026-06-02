package budgetapp.api.service;


import budgetapp.api.model.Transaction;
import budgetapp.api.repository.AccountRepository;
import budgetapp.api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    final private AccountRepository accountRepository;
    final private TransactionRepository transactionRepository;

    public List<Transaction> getFilteredTransactions(LocalDateTime from, LocalDateTime to, String category) {
        Specification<Transaction> spec = Specification.where((Specification<Transaction>) null);

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
}
