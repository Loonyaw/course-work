package ua.opnu.bankist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.opnu.bankist.annotations.LogService;
import ua.opnu.bankist.model.User;
import ua.opnu.bankist.repo.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@LogService // Custom annotation to enable logging for this service
public class UserService {

    @Autowired
    private UserRepository userRepository; // Injecting the UserRepository dependency
    @Autowired
    private CardService cardService; // Injecting the CardService dependency
    @Autowired
    private PasswordEncoder passwordEncoder; // Injecting the PasswordEncoder dependency

    public List<User> findAllUsers() {
        // Retrieve all users
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        // Find a user by their ID
        return userRepository.findById(id);
    }

    public User saveUser(User user, String currency) {
        // Save a new user and create a card for them
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            User savedUser = userRepository.save(user);
            cardService.createCardForUser(savedUser, currency);
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with provided username or email already exists.", e);
        }
    }

    public boolean existsByUsername(String username) {
        // Check if a user with the specified username exists
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        // Check if a user with the specified email exists
        return userRepository.existsByEmail(email);
    }

    public boolean authenticate(String username, String password, String pin) {
        // Authenticate a user by their username, password, and PIN
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(u -> passwordEncoder.matches(password, u.getPassword()) && u.getPin().equals(pin)).orElse(false);
    }

    public boolean deleteUserById(Long id) {
        // Delete a user by their ID
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
