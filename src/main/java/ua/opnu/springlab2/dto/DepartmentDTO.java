package ua.opnu.springlab2.dto;

import lombok.Data;

import java.util.List;


@Data
public class DepartmentDTO {
    private Long id;
    private String name;
    private String location;
    private List<EmployeeDTO> employees;
    private ManagerDTO head;
}
