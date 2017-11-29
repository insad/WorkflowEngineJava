package business.helpers;

import business.models.Employee;
import business.persistence.PersistenceHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.StringUtil;

import java.util.List;
import java.util.Map;
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
        return PersistenceHelper.asList(Employee.class);
    }

    public static String getNameById(UUID id) {
        String res = "Unknown";
        Employee item = PersistenceHelper.get(Employee.class, id);
        if (item != null) {
            res = item.getName();
        }
        return res;
    }

    public static String getListRoles(Employee item) {
        return StringUtil.join(",", CollectionUtil.select(item.getRoles().entrySet(),
                new CollectionUtil.ItemTransformer<Map.Entry<UUID, String>, String>() {
                    @Override
                    public String transform(Map.Entry<UUID, String> entry) {
                        return entry.getValue();
                    }
                }));
    }
}