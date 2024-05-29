package ua.opnu.bankist.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.opnu.bankist.annotations.LogController;
import ua.opnu.bankist.dto.CardUserDTO;
import ua.opnu.bankist.model.Card;
import ua.opnu.bankist.service.CardService;

@RestController
@LogController // Custom annotation to enable logging for this controller
@RequestMapping("/api/cards") // Base URL for all endpoints in this controller
public class CardController {

    @Autowired
    private CardService cardService; // Injecting the CardService dependency

    @GetMapping("/{cardNumber}")
    public ResponseEntity<CardUserDTO> getCardByNumber(@PathVariable String cardNumber) {
        // Fetch the card details using the card number
        Card card = cardService.getCardByNumber(cardNumber);
        if (card != null) {
            // Convert Card entity to CardUserDTO and return it in the response
            CardUserDTO cardUserDTO = new CardUserDTO(card.getId(), card.getCardNumber(), card.getExpirationDate(), card.getCvv(), card.getUser().getId());
            return ResponseEntity.ok(cardUserDTO);
        } else {
            // Return 404 Not Found if the card does not exist
            return ResponseEntity.notFound().build();
        }
    }
}
