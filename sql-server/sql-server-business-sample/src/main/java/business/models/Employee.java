package business.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import optimajet.workflow.core.util.CollectionUtil;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Employee")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Employee {
    @Column(name = "name")
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "StructDivisionId")
    private StructDivision structDivision;
    @Id
    @Column(name = "Id")
    private UUID id;
    @Column(name = "IsHead")
    private boolean head;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "EmployeeRole", joinColumns = @JoinColumn(name = "EmloyeeId"),
            inverseJoinColumns = @JoinColumn(name = "RoleId"))
    private List<Role> role;

    public boolean inRole(final String roleName) {
        return CollectionUtil.any(role, new CollectionUtil.ItemCondition<Role>() {
            @Override
            public boolean check(Role role) {
                return role.getName().equals(roleName);
            }
        });
    }
}
