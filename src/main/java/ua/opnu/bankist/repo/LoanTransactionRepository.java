package ua.opnu.bankist.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.opnu.bankist.model.LoanTransaction;

public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long> {
}