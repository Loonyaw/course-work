package ua.opnu.bankist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false, unique = true)
    private String cardNumber;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private Date expirationDate;

    @Column(nullable = false)
    private int cvv;

    @Column(nullable = false)
    private double balance;

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardNumber='" + cardNumber + '\'' +
                ", expirationDate=" + expirationDate +
                ", cvv=" + cvv +
                ", currency='" + currency + '\'' +
                ", balance=" + balance +
                '}';
    }
}
