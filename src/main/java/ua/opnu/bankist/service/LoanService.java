package ua.opnu.bankist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.opnu.bankist.annotations.LogService;
import ua.opnu.bankist.model.*;
import ua.opnu.bankist.repo.CardRepository;
import ua.opnu.bankist.repo.LoanRepository;
import ua.opnu.bankist.repo.TransactionRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@LogService
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CardRepository cardRepository;

    private static final double DEFAULT_INTEREST_RATE = 5.0;

    public Loan issueLoan(User user, double amount) {
        if (hasUnpaidLoans(user.getId())) {
            throw new IllegalArgumentException("User has unpaid loans. Cannot issue a new loan.");
        }

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setAmount(amount);
        loan.setInterestRate(calculateInterestRate(amount));
        loan.setIssueDate(new Date());
        loan.setDueDate(calculateDueDate(amount));

        Card card = cardRepository.findFirstByUserId(user.getId());
        if (card != null) {
            card.setBalance(card.getBalance() + amount);
            cardRepository.save(card);
        }

        return loanRepository.save(loan);
    }

    public boolean hasUnpaidLoans(Long userId) {
        List<Loan> loans = loanRepository.findByUserId(userId);
        for (Loan loan : loans) {
            if (loan.getAmount() > 0) {
                return true;
            }
        }
        return false;
    }

    public double calculateInterestRate(double amount) {
        if (amount <= 1000) {
            return DEFAULT_INTEREST_RATE;
        } else if (amount <= 5000) {
            return DEFAULT_INTEREST_RATE - 0.5;
        } else {
            return DEFAULT_INTEREST_RATE - 1.0;
        }
    }

    public Date calculateDueDate(double amount) {
        Calendar calendar = Calendar.getInstance();
        if (amount <= 1000) {
            calendar.add(Calendar.MONTH, 12);
        } else if (amount <= 5000) {
            calendar.add(Calendar.MONTH, 24);
        } else {
            calendar.add(Calendar.MONTH, 36);
        }
        return calendar.getTime();
    }

    public void repayLoan(Long loanId, Double repaymentAmount) throws Exception {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);
        if (!loanOpt.isPresent()) {
            throw new Exception("Loan not found");
        }

        Loan loan = loanOpt.get();
        User user = loan.getUser();

        Card card = cardRepository.findFirstByUserId(user.getId());
        if (card == null) {
            throw new Exception("No card found for user.");
        }

        if (repaymentAmount > loan.getAmount()) {
            throw new Exception("Repayment amount exceeds the remaining loan amount.");
        }

        double cardBalance = card.getBalance();
        if (repaymentAmount > cardBalance) {
            throw new Exception("Insufficient balance.");
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(-repaymentAmount);
        transaction.setTransactionDate(new Date());
        transaction.setTransactionType(TransactionType.LOAN_REPAYMENT);
        transactionRepository.save(transaction);

        card.setBalance(cardBalance - repaymentAmount);
        cardRepository.save(card);

        loan.setAmount(loan.getAmount() - repaymentAmount);
        if (loan.getAmount() <= 0) {
            loanRepository.delete(loan);
        } else {
            loanRepository.save(loan);
        }
    }
}
