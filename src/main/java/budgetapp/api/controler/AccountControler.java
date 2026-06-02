package budgetapp.api.controler;


import budgetapp.api.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountControler {

    private final BudgetService budgetService;
}
