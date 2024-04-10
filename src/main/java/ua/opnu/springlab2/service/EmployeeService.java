package ua.opnu.springlab2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.opnu.springlab2.dto.EmployeeDTO;
import ua.opnu.springlab2.dto.ManagerDTO;
import ua.opnu.springlab2.dto.SimpleDepartmentDTO;
import ua.opnu.springlab2.model.Department;
import ua.opnu.springlab2.model.Employee;
import ua.opnu.springlab2.model.Manager;
import ua.opnu.springlab2.repo.EmployeeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<EmployeeDTO> findAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().map(this::convertToEmployeeDto).collect(Collectors.toList());
    }

    private EmployeeDTO convertToEmployeeDto(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setPosition(employee.getPosition());
        dto.setSalary(employee.getSalary());

        if (employee.getDepartment() != null) {
            dto.setDepartment(convertToSimpleDepartmentDto(employee.getDepartment()));
        }

        if (employee.getManager() != null) {
            dto.setManager(convertToManagerDto(employee.getManager()));
        }
        return dto;
    }

    private SimpleDepartmentDTO convertToSimpleDepartmentDto(Department department) {
        SimpleDepartmentDTO dto = new SimpleDepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setLocation(department.getLocation());
        return dto;
    }

    private ManagerDTO convertToManagerDto(Manager manager) {
        if (manager == null) {
            return null;
        }
        ManagerDTO dto = new ManagerDTO();
        dto.setId(manager.getId());
        return dto;
    }

    public List<EmployeeDTO> findBySalaryGreaterThan(int salary) {
        List<Employee> employees = employeeRepository.findBySalaryGreaterThan(salary);
        return employees.stream().map(this::convertToEmployeeDto).collect(Collectors.toList());
    }

    public List<EmployeeDTO> findByDepartmentId(Long departmentId) {
        List<Employee> employees = employeeRepository.findByDepartmentId(departmentId);
        return employees.stream().map(this::convertToEmployeeDto).collect(Collectors.toList());
    }
}
