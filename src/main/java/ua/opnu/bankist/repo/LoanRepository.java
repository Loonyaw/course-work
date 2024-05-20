package ua.opnu.bankist.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.opnu.bankist.annotations.LogRepository;
import ua.opnu.bankist.model.Loan;

import java.util.List;

@Repository
@LogRepository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
}