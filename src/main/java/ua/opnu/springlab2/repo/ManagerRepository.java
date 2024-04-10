package ua.opnu.springlab2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.opnu.springlab2.model.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
}
