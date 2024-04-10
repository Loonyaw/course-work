package ua.opnu.springlab2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.opnu.springlab2.model.Department;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    @Query("SELECT d FROM Department d WHERE SIZE(d.employees) > ?1")
    List<Department> findDepartmentsWithEmployeeCountGreaterThan(int employeeCount);
    // reference - https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
}
