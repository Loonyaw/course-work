package ua.opnu.springlab2.dto;

import lombok.Data;

@Data
public class EmployeeWithDepartmentDTO {
    private Long id;
    private String name;
    private String position;
    private int salary;
    private SimpleDepartmentDTO department;
}