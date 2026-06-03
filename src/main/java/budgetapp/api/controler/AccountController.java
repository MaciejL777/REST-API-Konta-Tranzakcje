package budgetapp.api.controler;


import budgetapp.api.model.Account;
import budgetapp.api.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = budgetService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Account account=budgetService.findAccountById(id);
        return ResponseEntity.ok(account);
    }
    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account) {
        Account createdAccount=budgetService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        budgetService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
