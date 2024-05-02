package ua.opnu.springlab2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.opnu.springlab2.model.Card;
import ua.opnu.springlab2.model.User;
import ua.opnu.springlab2.repo.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CardService cardService;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        try {
            Card newCard = cardService.createCard();
            newCard.setUser(user);
            user.setCard(newCard);
            User savedUser = userRepository.save(user);
            cardService.saveCard(newCard);
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with provided username or email already exists.", e);
        } catch (Exception e) {
            throw e;
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean authenticate(String username, String password, String pin) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(u -> u.getPassword().equals(password) && u.getPin().equals(pin)).orElse(false);
    }

    public boolean validateCredentials(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(u -> u.getPassword().equals(password)).isPresent();
    }
}
