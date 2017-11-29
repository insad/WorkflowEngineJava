package wf.sample.controllers;

import business.helpers.EmployeeHelper;
import business.repository.EmployeeRepository;
import business.repository.RoleRepository;
import business.repository.StructDivisionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class SettingsController {

    private final RoleRepository roleRepository;
    private final StructDivisionRepository structDivisionRepository;

    public SettingsController(RoleRepository roleRepository,
                              StructDivisionRepository structDivisionRepository) {
        this.roleRepository = roleRepository;
        this.structDivisionRepository = structDivisionRepository;
    }

    @GetMapping("Settings/Edit")
    public String edit(Map<String, Object> model) {
        model.put("schemeName", "SimpleWF");
        model.put("employees", EmployeeHelper.getEmployeeCache());
        model.put("roles", roleRepository.findAll());
        model.put("structDivision", structDivisionRepository.findAll());
        return "settings/edit";
    }
}
