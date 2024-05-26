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
@LogService
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CardService cardService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user, String currency) {
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
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean authenticate(String username, String password, String pin) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(u -> passwordEncoder.matches(password, u.getPassword()) && u.getPin().equals(pin)).orElse(false);
    }

    public boolean deleteUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
