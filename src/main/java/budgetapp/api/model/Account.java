package budgetapp.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="accounts")
@NoArgsConstructor
@Getter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa konta jest wymagana")
    @Column(nullable = false,unique = true,length = 100)
    private String name;

    @Column(nullable = false, precision = 12,scale = 2)
    private BigDecimal balance;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions= new ArrayList<>();

    public Account(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
    }

}
