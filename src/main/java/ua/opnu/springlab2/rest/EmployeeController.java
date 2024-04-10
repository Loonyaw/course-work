package ua.opnu.springlab2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ua.opnu.springlab2.dto.EmployeeDTO;
import ua.opnu.springlab2.service.EmployeeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.findAllEmployees();
    }

    @GetMapping("/salary/{salary}")
    public List<EmployeeDTO> getEmployeesBySalary(@PathVariable int salary) {
        return employeeService.findBySalaryGreaterThan(salary);
    }

    @GetMapping("/department/{departmentId}")
    public List<EmployeeDTO> getEmployeesByDepartment(@PathVariable Long departmentId) {
        return employeeService.findByDepartmentId(departmentId);
    }

}
