package ua.opnu.springlab2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.opnu.springlab2.dto.EmployeeWithDepartmentDTO;
import ua.opnu.springlab2.dto.ManagerWithTeamDTO;
import ua.opnu.springlab2.dto.SimpleDepartmentDTO;
import ua.opnu.springlab2.model.Department;
import ua.opnu.springlab2.model.Employee;
import ua.opnu.springlab2.model.Manager;
import ua.opnu.springlab2.repo.ManagerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerService {

    private final ManagerRepository managerRepository;

    @Autowired
    public ManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    public List<ManagerWithTeamDTO> findAllManagersWithTeams() {
        List<Manager> managers = managerRepository.findAll();
        return managers.stream().map(this::convertToManagerWithTeamDto).collect(Collectors.toList());
    }

    private ManagerWithTeamDTO convertToManagerWithTeamDto(Manager manager) {
        ManagerWithTeamDTO dto = new ManagerWithTeamDTO();
        dto.setId(manager.getId());
        dto.setTeamMembers(manager.getTeamMembers().stream()
                .map(this::convertToEmployeeWithDepartmentDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private EmployeeWithDepartmentDTO convertToEmployeeWithDepartmentDto(Employee employee) {
        EmployeeWithDepartmentDTO dto = new EmployeeWithDepartmentDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setPosition(employee.getPosition());
        dto.setSalary(employee.getSalary());

        if (employee.getDepartment() != null) {
            dto.setDepartment(convertToSimpleDepartmentDto(employee.getDepartment()));
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

