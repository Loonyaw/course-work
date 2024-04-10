package ua.opnu.springlab2.dto;

import lombok.Data;

import java.util.List;

@Data
public class ManagerWithTeamDTO {
    private Long id;
    private List<EmployeeWithDepartmentDTO> teamMembers;
}