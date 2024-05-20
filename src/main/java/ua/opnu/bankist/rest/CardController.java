package ua.opnu.bankist.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.opnu.bankist.annotations.LogController;
import ua.opnu.bankist.dto.CardUserDTO;
import ua.opnu.bankist.model.Card;
import ua.opnu.bankist.service.CardService;

@RestController
@LogController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping("/{cardNumber}")
    public ResponseEntity<CardUserDTO> getCardByNumber(@PathVariable String cardNumber) {
        Card card = cardService.getCardByNumber(cardNumber);
        if (card != null) {
            CardUserDTO cardUserDTO = new CardUserDTO(card.getId(), card.getCardNumber(), card.getExpirationDate(), card.getCvv(), card.getUser().getId());
            return ResponseEntity.ok(cardUserDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
