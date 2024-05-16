package ua.opnu.bankist.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.opnu.bankist.model.Loan;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
}