package ua.opnu.springlab2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.opnu.springlab2.dto.ManagerWithTeamDTO;
import ua.opnu.springlab2.service.ManagerService;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    private final ManagerService managerService;

    @Autowired
    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/teams")
    public List<ManagerWithTeamDTO> getAllManagersWithTheirTeams() {
        return managerService.findAllManagersWithTeams();
    }
}
