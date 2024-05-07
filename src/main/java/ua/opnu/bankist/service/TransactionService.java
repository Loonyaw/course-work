package ua.opnu.bankist.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.opnu.bankist.model.Card;
import ua.opnu.bankist.model.Transaction;
import ua.opnu.bankist.model.TransactionType;
import ua.opnu.bankist.model.User;
import ua.opnu.bankist.repo.CardRepository;
import ua.opnu.bankist.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.opnu.bankist.repo.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CardRepository cardRepository;
    private Map<String, Double> currencyRates;

    public TransactionService() {
        loadCurrencyRates();
    }

    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> findTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionsForUser(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    private void loadCurrencyRates() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Map<String, Double>> data = mapper.readValue(new File("src/main/resources/static/bankist/exchangeRates.json"), HashMap.class);
            currencyRates = data.get("rates");
        } catch (IOException e) {
            e.printStackTrace();
            currencyRates = new HashMap<>();
        }
    }

    public double convertAmount(double amount, String fromCurrency, String toCurrency) {
        double rateFrom = currencyRates.getOrDefault(fromCurrency, 1.0);
        double rateTo = currencyRates.getOrDefault(toCurrency, 1.0);
        return (amount * rateFrom) / rateTo;
    }

    public boolean transferMoney(Long fromUserId, Long toUserId, double amount) {
        if (fromUserId.equals(toUserId)) {
            // Prevent self-transfer
            return false;
        }

        Optional<User> fromUserOpt = userRepository.findById(fromUserId);
        Optional<User> toUserOpt = userRepository.findById(toUserId);

        if (!fromUserOpt.isPresent() || !toUserOpt.isPresent()) {
            return false;
        }

        User fromUser = fromUserOpt.get();
        User toUser = toUserOpt.get();

        // Retrieve cards for each user
        Card fromUserCard = cardRepository.findFirstByUserId(fromUserId);
        Card toUserCard = cardRepository.findFirstByUserId(toUserId);

        if (fromUserCard == null || toUserCard == null) {
            return false;
        }

        double fromUserBalance = fromUser.getTransactions().stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        if (fromUserBalance < amount) {
            return false; // Insufficient funds
        }

        // Convert currency if necessary
        double convertedAmount = convertAmount(amount, fromUserCard.getCurrency(), toUserCard.getCurrency());

        // Create withdrawal transaction
        Transaction withdrawalTransaction = new Transaction();
        withdrawalTransaction.setUser(fromUser);
        withdrawalTransaction.setAmount(-amount);
        withdrawalTransaction.setTransactionType(TransactionType.WITHDRAWAL);
        withdrawalTransaction.setTransactionDate(new Date());
        transactionRepository.save(withdrawalTransaction);

        // Create deposit transaction
        Transaction depositTransaction = new Transaction();
        depositTransaction.setUser(toUser);
        depositTransaction.setAmount(convertedAmount);
        depositTransaction.setTransactionType(TransactionType.DEPOSIT);
        depositTransaction.setTransactionDate(new Date());
        transactionRepository.save(depositTransaction);

        return true;
    }


    public boolean requestLoan(Long userId, double amount) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Transaction loanTransaction = new Transaction();
            loanTransaction.setUser(user);
            loanTransaction.setAmount(amount);
            loanTransaction.setTransactionType(TransactionType.LOAN);
            loanTransaction.setTransactionDate(new Date());
            transactionRepository.save(loanTransaction);
            return true;
        }
        return false;
    }


}
