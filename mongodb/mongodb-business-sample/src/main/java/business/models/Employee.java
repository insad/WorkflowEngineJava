package business.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity("Employee")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Employee {
    private String name;
    private Map<UUID, String> roles = new HashMap<>();
    private UUID structDivisionId;
    private String structDivisionName;
    @Id
    private UUID id;
    private boolean head;

    public Employee(UUID id, String name, UUID structDivisionId, boolean head) {
        this.id = id;
        this.name = name;
        this.structDivisionId = structDivisionId;
        this.head = head;
    }
}
