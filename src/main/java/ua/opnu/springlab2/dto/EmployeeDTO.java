package ua.opnu.springlab2.dto;

import lombok.Data;

@Data
public class EmployeeDTO {
    private long id;
    private String name;
    private String position;
    private int salary;
    private SimpleDepartmentDTO department;
    private ManagerDTO manager;
}
