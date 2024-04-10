package ua.opnu.springlab2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.opnu.springlab2.dto.DepartmentDTO;
import ua.opnu.springlab2.service.DepartmentService;

import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/{id}")
    public DepartmentDTO getDepartmentById(@PathVariable Long id) {
        return departmentService.getDepartmentById(id);
    }

    @GetMapping("/more-employees/{count}")
    public List<DepartmentDTO> getDepartmentsWithMoreEmployees(@PathVariable("count") int count) {
        return departmentService.findDepartmentsWithMoreEmployees(count);
    }

    @GetMapping
    public List<DepartmentDTO> getAllDepartmentsWithManagersAndEmployees() {
        return departmentService.findAllDepartmentsWithManagersAndEmployees();
    }
}
