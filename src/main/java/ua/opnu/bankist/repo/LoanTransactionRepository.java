package ua.opnu.bankist.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.opnu.bankist.annotations.LogRepository;
import ua.opnu.bankist.model.LoanTransaction;

@Repository
@LogRepository
public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long> {
}