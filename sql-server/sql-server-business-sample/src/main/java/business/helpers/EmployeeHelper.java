package business.helpers;

import business.models.Employee;
import business.models.Role;
import business.persistence.ApplicationContextProvider;
import business.repository.EmployeeRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmployeeHelper {
    private static final Object LOCK = new Object();
    private static volatile List<Employee> employeeAll = null;

    public static List<Employee> getEmployeeCache() {
        if (employeeAll == null) {
            synchronized (LOCK) {
                if (employeeAll == null) {
                    employeeAll = getAll();
                }
            }
        }

        return employeeAll;
    }

    public static List<Employee> getAll() {
        EmployeeRepository employeeRepository = getEmployeeRepository();
        Iterable<Employee> employees = employeeRepository.findAll();
        ArrayList<Employee> result = new ArrayList<>();
        for (Employee employee : employees) {
            result.add(employee);
        }
        return result;
    }

    public static Employee getEmployee(UUID id) {
        List<Employee> employeeCache = getEmployeeCache();
        for (Employee employee : employeeCache) {
            if (employee.getId().equals(id)) {
                return employee;
            }
        }
        return null;
    }

    public static String getListRoles(Employee item) {
        return StringUtil.join(",", CollectionUtil.select(item.getRole(),
                new CollectionUtil.ItemTransformer<Role, String>() {
                    @Override
                    public String transform(Role role) {
                        return role.getName();
                    }
                }));
    }

    private static EmployeeRepository getEmployeeRepository() {
        return ApplicationContextProvider.getBean(EmployeeRepository.class);
    }
}