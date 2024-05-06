package ua.opnu.bankist.service;

import ua.opnu.bankist.model.Transaction;
import ua.opnu.bankist.model.TransactionType;
import ua.opnu.bankist.model.User;
import ua.opnu.bankist.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.opnu.bankist.repo.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;

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

    public boolean transferMoney(Long fromUserId, Long toUserId, double amount) {
        if (fromUserId.equals(toUserId)) {
            // Prevent users from transferring money to themselves
            return false;
        }

        Optional<User> fromUserOpt = userRepository.findById(fromUserId);
        Optional<User> toUserOpt = userRepository.findById(toUserId);

        if (fromUserOpt.isPresent() && toUserOpt.isPresent()) {
            User fromUser = fromUserOpt.get();
            User toUser = toUserOpt.get();

            double fromUserBalance = fromUser.getTransactions().stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            if (fromUserBalance < amount) {
                return false; // Insufficient funds
            }

            // Create withdrawal transaction
            Transaction withdrawalTransaction = new Transaction();
            withdrawalTransaction.setUser(fromUser);
            withdrawalTransaction.setAmount(-amount);
            withdrawalTransaction.setTransactionType(TransactionType.WITHDRAWAL);
            withdrawalTransaction.setTransactionDate(new Date());

            // Create deposit transaction
            Transaction depositTransaction = new Transaction();
            depositTransaction.setUser(toUser);
            depositTransaction.setAmount(amount);
            depositTransaction.setTransactionType(TransactionType.DEPOSIT);
            depositTransaction.setTransactionDate(new Date());

            // Save both transactions
            transactionRepository.save(withdrawalTransaction);
            transactionRepository.save(depositTransaction);

            return true;
        } else {
            return false;
        }
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
