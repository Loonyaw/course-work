package ua.opnu.bankist.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.opnu.bankist.model.*;
import ua.opnu.bankist.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private LoanService loanService;
    @Autowired
    private LoanTransactionRepository loanTransactionRepository;
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
        User fromUser = userRepository.findById(fromUserId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        User toUser = userRepository.findById(toUserId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Assuming the users have only one card each for simplicity
        Card fromCard = cardRepository.findFirstByUserId(fromUserId);
        Card toCard = cardRepository.findFirstByUserId(toUserId);

        if (fromCard == null || toCard == null) {
            throw new IllegalArgumentException("Card not found for one of the users.");
        }

        if (fromCard.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        // Convert the amount to the recipient's currency
        double convertedAmount = convertAmount(amount, fromCard.getCurrency(), toCard.getCurrency());

        // Deduct from the sender's card balance
        fromCard.setBalance(fromCard.getBalance() - amount);
        cardRepository.save(fromCard);

        // Add to the recipient's card balance
        toCard.setBalance(toCard.getBalance() + convertedAmount);
        cardRepository.save(toCard);

        // Record the transaction
        Transaction transaction = new Transaction();
        transaction.setUser(fromUser);
        transaction.setAmount(-amount);
        transaction.setTransactionDate(new Date());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transactionRepository.save(transaction);

        Transaction transactionReceived = new Transaction();
        transactionReceived.setUser(toUser);
        transactionReceived.setAmount(convertedAmount);
        transactionReceived.setTransactionDate(new Date());
        transactionReceived.setTransactionType(TransactionType.TRANSFER);
        transactionRepository.save(transactionReceived);

        return true;
    }



    public boolean requestLoan(Long userId, double amount) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }

        try {
            Loan loan = loanService.issueLoan(userOpt.get(), amount);

            LoanTransaction transaction = new LoanTransaction();
            transaction.setLoan(loan);
            transaction.setAmount(loan.getAmount());
            transaction.setTransactionDate(new Date());
            transaction.setType(TransactionType.LOAN_ISSUE);
            loanTransactionRepository.save(transaction);

            return true;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
