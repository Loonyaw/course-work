package ua.opnu.springlab2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.opnu.springlab2.dto.DepartmentDTO;
import ua.opnu.springlab2.dto.EmployeeDTO;
import ua.opnu.springlab2.dto.ManagerDTO;
import ua.opnu.springlab2.dto.SimpleDepartmentDTO;
import ua.opnu.springlab2.model.Department;
import ua.opnu.springlab2.model.Employee;
import ua.opnu.springlab2.model.Manager;
import ua.opnu.springlab2.repo.DepartmentRepository;
import ua.opnu.springlab2.repo.EmployeeRepository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public DepartmentDTO getDepartmentById(Long departmentId) {
        Optional<Department> department = departmentRepository.findById(departmentId);
        return department.map(this::convertToDepartmentDto).orElse(null);
    }

    public List<DepartmentDTO> findDepartmentsWithMoreEmployees(int employeeCount) {
        List<Department> departments = departmentRepository.findDepartmentsWithEmployeeCountGreaterThan(employeeCount);
        return departments.stream().map(this::convertToDepartmentDto).collect(Collectors.toList());
    }

    public List<DepartmentDTO> findAllDepartmentsWithManagersAndEmployees() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream().map(this::convertToDepartmentDtoWithManagersAndEmployees).collect(Collectors.toList());
    }

    private DepartmentDTO convertToDepartmentDtoWithManagersAndEmployees(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setLocation(department.getLocation());

        if (department.getHead() != null) {
            dto.setHead(convertToManagerDto(department.getHead()));
        }

        List<EmployeeDTO> employeeDTOs = department.getEmployees()
                .stream()
                .map(this::convertToEmployeeDto)
                .collect(Collectors.toList());
        dto.setEmployees(employeeDTOs);

        return dto;
    }

    private DepartmentDTO convertToDepartmentDto(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setLocation(department.getLocation());

        List<EmployeeDTO> employeeDTOs = employeeRepository.findByDepartmentId(department.getId())
                .stream()
                .map(this::convertToEmployeeDto)
                .collect(Collectors.toList());
        dto.setEmployees(employeeDTOs);

        if (department.getHead() != null) {
            dto.setHead(convertToManagerDto(department.getHead()));
        }

        return dto;
    }

    private ManagerDTO convertToManagerDto(Manager manager) {
        ManagerDTO dto = new ManagerDTO();
        dto.setId(manager.getId());
        return dto;
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
}
