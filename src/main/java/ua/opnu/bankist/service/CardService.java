package ua.opnu.bankist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.opnu.bankist.annotations.LogService;
import ua.opnu.bankist.model.Card;
import ua.opnu.bankist.model.User;
import ua.opnu.bankist.repo.CardRepository;

import java.util.Date;
import java.util.Random;
import java.util.Calendar;

@Service
@LogService // Custom annotation to enable logging for this service
public class CardService {

    @Autowired
    private CardRepository cardRepository; // Injecting the CardRepository dependency

    public Card createCardForUser(User user, String currency) {
        // Create a new card for a user with the specified currency
        Card card = new Card();
        card.setUser(user);
        card.setCardNumber(generateCardNumber());
        card.setCvv(generateCvv());
        card.setExpirationDate(generateExpirationDate());
        card.setCurrency(currency);
        return cardRepository.save(card);
    }

    private String generateCardNumber() {
        // Generate a random card number
        Random rnd = new Random();
        StringBuilder cardNumber = new StringBuilder("4000 ");
        for (int i = 0; i < 12; i++) {
            if (i % 4 == 0 && i != 0) {
                cardNumber.append(" ");
            }
            cardNumber.append(rnd.nextInt(10));
        }
        return cardNumber.toString();
    }

    private int generateCvv() {
        // Generate a random CVV code
        return new Random().nextInt(900) + 100;
    }

    private Date generateExpirationDate() {
        // Generate an expiration date 5 years from now
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 5);
        return calendar.getTime();
    }

    public Card getCardByNumber(String cardNumber) {
        // Retrieve a card by its number
        return cardRepository.findByCardNumber(cardNumber);
    }
}
