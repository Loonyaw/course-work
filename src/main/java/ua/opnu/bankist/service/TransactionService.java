package ua.opnu.bankist.service;

import ua.opnu.bankist.model.Transaction;
import ua.opnu.bankist.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

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
}
