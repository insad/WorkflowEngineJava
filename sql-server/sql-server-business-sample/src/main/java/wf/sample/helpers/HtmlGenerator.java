package wf.sample.helpers;

import business.models.Employee;
import business.models.Role;
import business.models.StructDivision;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.StringUtil;

import java.util.Collection;
import java.util.List;

public final class HtmlGenerator {
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
        Collection<String> roles = CollectionUtil.select(m.getRole(),
                new CollectionUtil.ItemTransformer<Role, String>() {
                    @Override
                    public String transform(Role role) {
                        return role.getName();
                    }
                });
        sb.append(String.format("%s", StringUtil.join(",", roles)));
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
        sb.append(String.format("<input type='hidden' name='%s.ParentId' value='%s'></input>", valuePrefix,
                m.getParent() != null ? m.getParent().getId() : ""));
        sb.append(String.format("<td class='columnTree'><b>%s</b></td>", m.getName()));
        sb.append("<td></td>");
        sb.append("</tr>");

        Collection<Employee> employeeCollection = CollectionUtil.where(employees,
                new CollectionUtil.ItemCondition<Employee>() {
                    @Override
                    public boolean check(Employee c) {
                        return m.equals(c.getStructDivision());
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
                        return m.equals(c.getParent());
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
            if (m.getParent() == null) {
                sb.append(generateColumnHtml("Columns", m, model, employees, index, null));
                index[0]++;
            }
        }
        return sb.toString();
    }
}