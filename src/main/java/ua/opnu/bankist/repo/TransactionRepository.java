package ua.opnu.bankist.repo;

import org.springframework.stereotype.Repository;
import ua.opnu.bankist.annotations.LogRepository;
import ua.opnu.bankist.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
@LogRepository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
}
