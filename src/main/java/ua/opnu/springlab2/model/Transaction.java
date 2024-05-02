package ua.opnu.springlab2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private Date transactionDate;

    @Column(nullable = false)
    private String transactionType; // "DEPOSIT", "WITHDRAWAL", "LOAN"
}
