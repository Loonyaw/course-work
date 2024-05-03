package ua.opnu.bankist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.opnu.bankist.model.Card;
import ua.opnu.bankist.model.Transaction;
import ua.opnu.bankist.model.User;
import ua.opnu.bankist.repo.TransactionRepository;
import ua.opnu.bankist.repo.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CardService cardService;
    @Autowired
    private TransactionRepository transactionRepository;

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

    public boolean requestLoan(Long userId, double amount) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Transaction loanTransaction = new Transaction();
            loanTransaction.setUser(user);
            loanTransaction.setAmount(amount);
            loanTransaction.setTransactionType("LOAN");
            loanTransaction.setTransactionDate(new Date());
            transactionRepository.save(loanTransaction);
            return true;
        }
        return false;
    }

    public boolean transferMoney(Long fromUserId, Long toUserId, double amount) {
        Optional<User> fromUserOpt = userRepository.findById(fromUserId);
        Optional<User> toUserOpt = userRepository.findById(toUserId);

        if (fromUserOpt.isPresent() && toUserOpt.isPresent()) {
            User fromUser = fromUserOpt.get();
            User toUser = toUserOpt.get();

            Transaction withdrawalTransaction = new Transaction();
            withdrawalTransaction.setUser(fromUser);
            withdrawalTransaction.setAmount(-amount);
            withdrawalTransaction.setTransactionType("WITHDRAWAL");
            withdrawalTransaction.setTransactionDate(new Date());

            Transaction depositTransaction = new Transaction();
            depositTransaction.setUser(toUser);
            depositTransaction.setAmount(amount);
            depositTransaction.setTransactionType("DEPOSIT");
            depositTransaction.setTransactionDate(new Date());

            transactionRepository.save(withdrawalTransaction);
            transactionRepository.save(depositTransaction);
            return true;
        }
        return false;
    }
}
