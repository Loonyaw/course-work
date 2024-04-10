package ua.opnu.springlab2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Manager {
    // id, employeeId
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonIgnore
    @OneToOne(mappedBy = "head")
    private Department department;

    @OneToMany(mappedBy = "manager")
    private List<Employee> teamMembers;

}
