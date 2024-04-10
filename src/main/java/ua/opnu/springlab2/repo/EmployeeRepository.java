package ua.opnu.springlab2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.opnu.springlab2.model.Employee;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findBySalaryGreaterThan(int salary);
    List<Employee> findByDepartmentId(Long departmentId);
}
