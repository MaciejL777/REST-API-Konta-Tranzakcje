package budgetapp.api.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="transactions")
@NoArgsConstructor
@Getter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Kwota tranzakcji musi byc wieksza od zera")
    @DecimalMin(value = "0.01", message = "Kwota tranzakcji musi byc wieksza od zera")
    @Column(nullable = false,precision=12,scale=2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 10)
    private Type type;

    @Column(nullable = false,length = 20)
    private String category;

    @Column(length = 100)
    private String description;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id", nullable = false)
    private Account account;


    @PrePersist
    protected void onCreate() {
        this.transactionDate = LocalDateTime.now();
    }

    public Transaction(Account account, BigDecimal amount, Type type, String category, String description) {
        this.account = account;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.description = description;
    }
}
