package wf.sample.controllers;

import business.helpers.EmployeeHelper;
import business.models.Employee;
import business.models.Role;
import business.models.StructDivision;
import business.persistence.PersistenceHelper;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.IOUtil;
import optimajet.workflow.core.util.StringUtil;
import optimajet.workflow.mongodb.WorkflowScheme;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Controller
public class SettingsController {

    private static final String SCHEME_NAME = "SimpleWF";

    private static String loadScheme() {
        try (InputStream inputStream = SettingsController.class.getResourceAsStream("/scheme.xml")) {
            return IOUtil.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String generateColumnHtml(String name, Employee m, int index, String refId) {
        String valuePrefix = String.format("%s[%d]", name, index);

        StringBuilder sb = new StringBuilder();
        String trName = String.format("tr_%s%d", name, index);

        sb.append(String.format("<tr Id='%s' %s>", trName,
                StringUtil.isNullOrEmpty(refId) ? StringUtil.EMPTY : String.format("class='child-of-%s'", refId)));
        sb.append(String.format("<input type='hidden' name='%s.Id' value='%s'></input>", valuePrefix, m.getId()));
        sb.append("<td class='columnTree'>");
        sb.append(String.format("%s", m.getName()));
        if (m.isHead()) {
            sb.append(" <b>Head</b>");
        }
        sb.append("</td>");
        sb.append("<td>");
        sb.append(String.format("%s", StringUtil.join(",", m.getRoles().values())));
        sb.append("</td>");
        sb.append("</tr>");

        return sb.toString();
    }

    // note ref-параметр index перенесён как массив с одним элементом
    private static String generateColumnHtml(String name, final StructDivision m, List<StructDivision> model,
                                             List<Employee> employees, int[] index, String refId) {
        String valuePrefix = String.format("%s[%d]", name, index[0]);

        StringBuilder sb = new StringBuilder();
        String trName = String.format("tr_%s%d", name, index[0]);

        sb.append(String.format("<tr Id='%s' %s>", trName,
                StringUtil.isNullOrEmpty(refId) ? StringUtil.EMPTY : String.format("class='child-of-%s'", refId)));
        sb.append(String.format("<input type='hidden' name='%s.Id' value='%s'></input>", valuePrefix, m.getId()));
        sb.append(String.format("<input type='hidden' name='%s.ParentId' value='%s'></input>", valuePrefix, m.getParentId()));
        sb.append(String.format("<td class='columnTree'><b>%s</b></td>", m.getName()));
        sb.append("<td></td>");
        sb.append("</tr>");

        Collection<Employee> employeeCollection = CollectionUtil.where(employees,
                new CollectionUtil.ItemCondition<Employee>() {
                    @Override
                    public boolean check(Employee c) {
                        return m.getId().equals(c.getStructDivisionId());
                    }
                });
        for (Employee item : employeeCollection) {
            index[0]++;
            sb.append(generateColumnHtml(name, item, index[0], trName));
        }

        Collection<StructDivision> divisionCollection = CollectionUtil.where(model,
                new CollectionUtil.ItemCondition<StructDivision>() {
                    @Override
                    public boolean check(StructDivision c) {
                        return m.getId().equals(c.getParentId());
                    }
                });
        for (StructDivision item : divisionCollection) {
            index[0]++;
            sb.append(generateColumnHtml(name, item, model, employees, index, trName));
        }

        return sb.toString();
    }

    public String generateStructDivisions(List<StructDivision> model, List<Employee> employees) {
        StringBuilder sb = new StringBuilder();
        int index[] = new int[]{0};
        for (StructDivision m : model) {
            if (m.getParentId() == null) {
                sb.append(generateColumnHtml("Columns", m, model, employees, index, null));
                index[0]++;
            }
        }
        return sb.toString();
    }

    @GetMapping("Settings/Edit")
    public String edit(Map<String, Object> model) {
        return getModel(model);
    }

    @GetMapping("Settings/GenerateData")
    public String generateData() {
        Role[] roles = new Role[]{
                new Role(UUID.fromString("8D378EBE-0666-46B3-B7AB-1A52480FD12A"), "Big Boss"),
                new Role(UUID.fromString("412174C2-0490-4101-A7B3-830DE90BCAA0"), "Accountant"),
                new Role(UUID.fromString("71FFFB5B-B707-4B3C-951C-C37FDFCC8DFB"), "User")
        };

        List<StructDivision> sd = Arrays.asList(
                new StructDivision(UUID.fromString("B14F5D81-5B0D-4ACC-92B8-27CBBE39086B"), "Group 1", UUID.fromString("F6E34BDF-B769-42DD-A2BE-FEE67FAF9045")),
                new StructDivision(UUID.fromString("7E9FD972-C775-4C6B-9D91-47E9397BD2E6"), "Group 1.1", UUID.fromString("B14F5D81-5B0D-4ACC-92B8-27CBBE39086B")),
                new StructDivision(UUID.fromString("DC195A4F-46F9-41B2-80D2-77FF9C6269B7"), "Group 1.2", UUID.fromString("B14F5D81-5B0D-4ACC-92B8-27CBBE39086B")),
                new StructDivision(UUID.fromString("C5DCC148-9C0C-45C4-8A68-901D99A26184"), "Group 2.2", UUID.fromString("72D461B2-234B-40D6-B410-B261964BA291")),
                new StructDivision(UUID.fromString("72D461B2-234B-40D6-B410-B261964BA291"), "Group 2", UUID.fromString("F6E34BDF-B769-42DD-A2BE-FEE67FAF9045")),
                new StructDivision(UUID.fromString("BC21A482-28E7-4951-8177-E57813A70FC5"), "Group 2.1", UUID.fromString("72D461B2-234B-40D6-B410-B261964BA291")),
                new StructDivision(UUID.fromString("F6E34BDF-B769-42DD-A2BE-FEE67FAF9045"), "Head Group")
        );

        Employee e1 = new Employee(
                UUID.fromString("E41B48E3-C03D-484F-8764-1711248C4F8A"),
                "Maria",
                UUID.fromString("C5DCC148-9C0C-45C4-8A68-901D99A26184"),
                false
        );

        e1.getRoles().put(UUID.fromString("412174C2-0490-4101-A7B3-830DE90BCAA0"), "Accountant");
        e1.getRoles().put(UUID.fromString("71FFFB5B-B707-4B3C-951C-C37FDFCC8DFB"), "User");

        Employee e2 = new Employee(
                UUID.fromString("BBE686F8-8736-48A7-A886-2DA25567F978"),
                "Mark",
                UUID.fromString("7E9FD972-C775-4C6B-9D91-47E9397BD2E6"),
                false
        );
        e2.getRoles().put(UUID.fromString("71FFFB5B-B707-4B3C-951C-C37FDFCC8DFB"), "User");

        Employee e3 = new Employee(
                UUID.fromString("81537E21-91C5-4811-A546-2DDDFF6BF409"),
                "Silviya",
                UUID.fromString("F6E34BDF-B769-42DD-A2BE-FEE67FAF9045"),
                true
        );
        e3.getRoles().put(UUID.fromString("8D378EBE-0666-46B3-B7AB-1A52480FD12A"), "Big Boss");
        e3.getRoles().put(UUID.fromString("71FFFB5B-B707-4B3C-951C-C37FDFCC8DFB"), "User");

        Employee e4 = new Employee(
                UUID.fromString("B0E6FD4C-2DB9-4BB6-A62E-68B6B8999905"),
                "Margo",
                UUID.fromString("DC195A4F-46F9-41B2-80D2-77FF9C6269B7"),
                false
        );
        e4.getRoles().put(UUID.fromString("71FFFB5B-B707-4B3C-951C-C37FDFCC8DFB"), "User");

        Employee e5 = new Employee(
                UUID.fromString("DEB579F9-991C-4DB9-A17D-BB1ECCF2842C"),
                "Max",
                UUID.fromString("B14F5D81-5B0D-4ACC-92B8-27CBBE39086B"),
                true
        );
        e5.getRoles().put(UUID.fromString("71FFFB5B-B707-4B3C-951C-C37FDFCC8DFB"), "User");

        Employee e6 = new Employee(
                UUID.fromString("91F2B471-4A96-4AB7-A41A-EA4293703D16"),
                "John",
                UUID.fromString("7E9FD972-C775-4C6B-9D91-47E9397BD2E6"),
                true
        );

        e6.getRoles().put(UUID.fromString("71FFFB5B-B707-4B3C-951C-C37FDFCC8DFB"), "User");
        Employee[] employees = new Employee[]{e1, e2, e3, e4, e5, e6};

        for (Role r : roles) {
            if (PersistenceHelper.get(Role.class, r.getId()) == null) {
                PersistenceHelper.save(r);
            }
        }

        for (StructDivision e : sd) {
            if (PersistenceHelper.get(StructDivision.class, e.getId()) == null) {
                PersistenceHelper.save(e);
            }
        }

        for (final Employee e : employees) {
            if (PersistenceHelper.get(Employee.class, e.getId()) == null) {
                StructDivision division = CollectionUtil.first(sd, new CollectionUtil.ItemCondition<StructDivision>() {
                    @Override
                    public boolean check(StructDivision c) {
                        return c.getId().equals(e.getStructDivisionId());
                    }
                });
                e.setStructDivisionName(division.getName());
                PersistenceHelper.save(e);
            }
        }

        if (PersistenceHelper.get(WorkflowScheme.class, SCHEME_NAME) == null) {
            WorkflowScheme workflowScheme = new WorkflowScheme();
            workflowScheme.setId(SCHEME_NAME);
            workflowScheme.setCode(SCHEME_NAME);
            workflowScheme.setScheme(loadScheme());
            PersistenceHelper.save(workflowScheme);
        }
        return "redirect:Edit";
    }

    private String getModel(Map<String, Object> model) {
        model.put("schemeName", "SimpleWF");
        model.put("employees", EmployeeHelper.getAll());
        model.put("roles", PersistenceHelper.asList(Role.class));
        model.put("structDivision", PersistenceHelper.asList(StructDivision.class));
        return "settings/edit";
    }
}
