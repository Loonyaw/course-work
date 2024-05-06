package ua.opnu.bankist.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CardUserDTO {
    private Long id;
    private String cardNumber;
    private Date expirationDate;
    private int cvv;
    private Long userId;

    public CardUserDTO(Long id, String cardNumber, Date expirationDate, int cvv, Long userId) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
        this.userId = userId;
    }

}
