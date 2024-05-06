package ua.opnu.bankist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.opnu.bankist.model.Card;
import ua.opnu.bankist.model.User;
import ua.opnu.bankist.repo.CardRepository;

import java.util.Date;
import java.util.Random;
import java.util.Calendar;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public Card createCardForUser(User user) {
        Card card = new Card();
        card.setUser(user);
        card.setCardNumber(generateCardNumber());
        card.setCvv(generateCvv());
        card.setExpirationDate(generateExpirationDate());
        return cardRepository.save(card);
    }


    private String generateCardNumber() {
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
        return new Random().nextInt(900) + 100;
    }

    private Date generateExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 5);
        return calendar.getTime();
    }

    public void saveCard(Card card) { // ?
        cardRepository.save(card);
    }

    public Card getCardByNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }
}
