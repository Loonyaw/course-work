package ua.opnu.springlab2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Department {
    // id, name, location, headId
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String location;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "head_id", nullable = false)
    private Manager head;

}
