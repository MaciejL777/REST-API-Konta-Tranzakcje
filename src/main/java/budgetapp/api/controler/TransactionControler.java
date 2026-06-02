package budgetapp.api.controler;


import budgetapp.api.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionControler {

    private final BudgetService budgetService;
}
